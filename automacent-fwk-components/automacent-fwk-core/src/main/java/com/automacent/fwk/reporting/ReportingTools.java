package com.automacent.fwk.reporting;

import java.awt.AWTError;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.testng.Reporter;

import com.automacent.fwk.core.BaseTest;
import com.automacent.fwk.enums.Color;
import com.automacent.fwk.enums.Css;
import com.automacent.fwk.enums.LogType;
import com.automacent.fwk.enums.RetryMode;
import com.automacent.fwk.enums.ScreenshotModeForIteration;
import com.automacent.fwk.enums.ScreenshotType;
import com.automacent.fwk.enums.TestStatus;
import com.automacent.fwk.execution.IterationManager;
import com.automacent.fwk.listeners.AutomacentListener;
import com.automacent.fwk.utils.DateUtils;
import com.automacent.fwk.utils.LoggingUtils;

/**
 * This class contains the methods for taking screenshots, logging statements to
 * the testNG report and customization of reports.
 * 
 * @author sighil.sivadas
 * 
 **/
public class ReportingTools {

	private static final Logger _logger = Logger.getLogger(ReportingTools.class);

	private static Rectangle screenshotFrame;

	static {
		try {
			screenshotFrame = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize().width,
					Toolkit.getDefaultToolkit().getScreenSize().height - 75);
			_logger.info(String.format("Screen resolution is %sx%s", Toolkit.getDefaultToolkit().getScreenSize().width,
					Toolkit.getDefaultToolkit().getScreenSize().height));
		} catch (AWTError e) {
			screenshotFrame = new Rectangle(1024, 768);
			BaseTest.getTestObject().setScreenshotType(ScreenshotType.BROWSER_SCREENSHOT.name());
			_logger.warn("Error Initilizing DESKTOP SCREENSHOT. BROWSER_SCREENSHOT will be used.", e);
		}
	}
	private static int screenshotNumber = 0;

	/*----------------------Print Screenshot to Report------------------*/

	/**
	 * Method for taking screenshot, creating file and adding the file to the report
	 * 
	 * @param message
	 *            Message to the printed
	 * @param status
	 *            Status of the test [FAIL/PASS]
	 * @param screenshotType
	 *            Specifies whether the screenshot is a WebDriver screenshot or a
	 *            Desktop screenshot
	 */
	private static void saveScreenshot(String message, TestStatus status, ScreenshotType screenshotType) {
		if (!message.isEmpty())
			if (status == TestStatus.FAIL)
				logErrorMessage(message);
			else
				logMessage(message);

		int iteration = IterationManager.getManager().getIteration();

		String screenShotDirectory = "screenshots" + File.separator + "itr_" + BaseTest.getTestObject().getTestName()
				+ "_" + iteration;
		String screenShotDirectoryPath = System.getProperty("automacent.reportdir") + File.separator
				+ screenShotDirectory;
		String screenShotName = "scr" + (++screenshotNumber) + ".png";
		String href = screenShotDirectory + File.separator + screenShotName;
		String screenShotFile = screenShotDirectoryPath + File.separator + screenShotName;

		if (screenshotType == ScreenshotType.BROWSER_SCREENSHOT)
			try {
				if (BaseTest.getTestObject().getDriverManager().getActiveDriver().getWebDriver() == null) {
					screenshotType = ScreenshotType.DESKTOP_SCREENSHOT;
					_logger.info("Screenshot type changed to Desktop Screenshot because driver is null");
				}
			} catch (Exception e) {
				screenshotType = ScreenshotType.DESKTOP_SCREENSHOT;
				_logger.info("Screenshot type changed to Desktop Screenshot because driver threw exception"
						+ e.getMessage());
			}

		boolean isScreenshotTaken = false;

		try {
			if (screenshotType == ScreenshotType.BROWSER_SCREENSHOT) {
				FileUtils.copyFile(
						((TakesScreenshot) BaseTest.getTestObject().getDriverManager().getActiveDriver().getWebDriver())
								.getScreenshotAs(OutputType.FILE),
						new File(screenShotFile));
				_logger.debug("Browser Screenshot taken - " + screenShotFile);
				isScreenshotTaken = true;
			}
		} catch (Exception e) {
			screenshotType = ScreenshotType.DESKTOP_SCREENSHOT;
			_logger.warn("Take Browser screenshot failed. Switching to desktop screenshot", e);
		}

		if (screenshotType == ScreenshotType.DESKTOP_SCREENSHOT) {
			try {
				File screenShotDirectoryFile = new File(screenShotDirectoryPath);
				if (!screenShotDirectoryFile.exists()) {
					screenShotDirectoryFile.mkdirs();
				}

				BufferedImage image = new Robot().createScreenCapture(screenshotFrame);
				File scrLocation = new File(screenShotFile);
				ImageIO.write(image, "png", scrLocation);
				_logger.debug("Desktop Screenshot taken - " + screenShotFile);
				isScreenshotTaken = true;
			} catch (Exception e) {
				_logger.warn("Taking Desktop screenshot failed", e);
			}
		}

		if (isScreenshotTaken) {
			Reporter.log("<div style='color: " + Color.BLACK.getColorValue() + "; font-size: small; "
					+ Css.UNDERLINE_NONE.getCssValue() + "'>" + DateUtils.getDate() + " : "
					+ LoggingUtils.getSpaceForNestingLevel(LoggingUtils.getNestingLevelOfLogs() + 1, LogType.HTML)
					+ "<a href='" + href + "'><img src='" + href + "' style='height:25%; width:25%;' alt='itr_"
					+ BaseTest.getTestObject().getTestName() + "_" + IterationManager.getManager().getIteration()
					+ "'/></a></div>");
		}
	}

	/**
	 * Take screenshot with the set {@link ScreenshotType} without printing any
	 * message to Report <br/>
	 */
	public static void takeScreenshot() {
		takeScreenshot("");
	}

	/**
	 * Take screenshot with the set {@link ScreenshotType} and print message to
	 * Report
	 * 
	 * @param message
	 *            Message to be printed to the report
	 */
	public static void takeScreenshot(String message) {
		saveScreenshot(message, TestStatus.PASS, BaseTest.getTestObject().getScreenshotType());
	}

	/**
	 * Take screenshot of the browser without printing any message to Report <br/>
	 */
	public static void takeBrowserScreenshot() {
		takeBrowserScreenshot("");
	}

	/**
	 * Take screenshot of the browser and print message to Report
	 * 
	 * @param message
	 *            Message to be printed to the report
	 */
	public static void takeBrowserScreenshot(String message) {
		saveScreenshot(message, TestStatus.PASS, ScreenshotType.BROWSER_SCREENSHOT);
	}

	/**
	 * Take screenshot of the desktop. This is similar to Print Screen Operation
	 * <br/>
	 */
	public static void captureDesktopScreen() {
		captureDesktopScreen("");
	}

	/**
	 * Take screenshot of the desktop and prints the message to report. This is
	 * similar to Print Screen Operation<br/>
	 * 
	 * @param message
	 *            Message to be printed to the report
	 */
	public static void captureDesktopScreen(String message) {
		saveScreenshot(message, TestStatus.PASS, ScreenshotType.DESKTOP_SCREENSHOT);
	}

	/**
	 * Take screenshot as per the set {@link ScreenshotType} and prints the message
	 * to report. This method is used for logging the failure message and taking the
	 * screenshot in the {@link AutomacentListener}. <br/>
	 * 
	 * @param message
	 *            Message to be printed to the report
	 */
	public static void logScreenshotOnFailure(String message) {
		saveScreenshot(message, TestStatus.FAIL, BaseTest.getTestObject().getScreenshotType());
	}

	/**
	 * Take screenshot of the web browser and prints the message to report. This
	 * method is used for logging the failure message and taking the screenshot in
	 * the {@link AutomacentListener}. <br/>
	 * 
	 * @param message
	 *            Message to be printed to the report
	 */
	public static void logBrowserScreenshotOnFailure(String message) {
		saveScreenshot(message, TestStatus.FAIL, ScreenshotType.BROWSER_SCREENSHOT);
	}

	/**
	 * Take screenshot of the screen and prints the message to report. This method
	 * is used for logging the failure message and taking the screenshot in the
	 * {@link AutomacentListener}. <br/>
	 * 
	 * @param message
	 *            Message to be printed to the report
	 */
	public static void logScreenGrabOnFailure(String message) {
		saveScreenshot(message, TestStatus.FAIL, ScreenshotType.DESKTOP_SCREENSHOT);
	}

	/**
	 * This method wipes the logs of all the screenshots included in the TestNG
	 * {@link Reporter} of all but the last iteration in the test. This method comes
	 * into effect when using the {@link IterationManager} class to iterate through
	 * steps. This method has no effect when the {@link ScreenshotModeForIteration}
	 * is set to {@link ScreenshotModeForIteration#EACH_ITERATION} and the entries
	 * before and after the {@link IterationManager} loop.Also, all the screenshots
	 * of failed iteration when {@link RetryMode} is set to values other than OFF,
	 * are saved
	 */
	public static void wipeScreenshotEntryInReports() {
		if (BaseTest.getTestObject().getScreenshotModeForIteration()
				.equals(ScreenshotModeForIteration.LAST_ITERATION)) {
			int iteration = IterationManager.getManager().getIteration();
			if (iteration != 0)
				try {
					while (--iteration > 0)
						if (!IterationManager.getManager().isIterationFailed(iteration)) {
							ListIterator<String> irt = Reporter.getOutput().listIterator();
							while (irt.hasNext())
								if (irt.next().contains(
										"alt='itr_" + BaseTest.getTestObject().getTestName() + "_" + iteration + "'")) {
									irt.set("<div></div>");
								}
						}
					_logger.info("Screenshot logs wiped for all but last iteration");
				} catch (Exception e) {
					_logger.warn("Error wiping screenshot logs in Report", e);
				}
		}
	}

	/**
	 * This method wipes the screenshot folder included in the TestNG
	 * {@link Reporter} of all but the last iteration in the test. This method comes
	 * into effect when using the {@link IterationManager} class to iterate through
	 * steps. This method has no effect when the {@link ScreenshotModeForIteration}
	 * is set to {@link ScreenshotModeForIteration#EACH_ITERATION} and the entries
	 * before and after the {@link IterationManager} loop. Also, all the screenshots
	 * of failed iteration when {@link RetryMode} is set to values other than OFF,
	 * are saved
	 */
	public static void wipeScreenshotDirectory() {
		if (BaseTest.getTestObject().getScreenshotModeForIteration().equals(ScreenshotModeForIteration.LAST_ITERATION)
				&& !IterationManager.getManager().isIterationFailed()) {
			int iteration = IterationManager.getManager().getIteration();
			String screenShotDirectoryPath = System.getProperty("automacent.reportdir") + File.separator + "screenshots"
					+ File.separator + "itr_" + BaseTest.getTestObject().getTestName() + "_" + iteration;

			if (iteration != 0)
				try {
					File file = new File(screenShotDirectoryPath);
					if (file.exists() && file.isDirectory()) {
						FileUtils.cleanDirectory(file);
						_logger.info("Screenshot directory wiped for the iteration");
					}
				} catch (Exception e) {
					_logger.warn("Error wiping screenshot directory", e);
				}
		}
	}

	/*------------------------Print Text to Report--------------------------*/

	/**
	 * This method prints the message to the report
	 * 
	 * @param bgColor
	 * @param textColor
	 * @param message
	 * @param messageType
	 */
	private static void log(Color textColor, Css underline, String message) {
		Reporter.log("<div style='color: " + textColor.getColorValue() + "; font-size: small; "
				+ underline.getCssValue() + "'>" + DateUtils.getDate() + " : " + message + "</div>");
	}

	/**
	 * Prints message to Report
	 * 
	 * @param message
	 *            Message to be printed to the report
	 */
	public static void logMessage(String message) {
		log(Color.BLACK, Css.UNDERLINE_NONE, message);
	}

	/**
	 * Prints message to Report in bold
	 * 
	 * @param message
	 *            Message to be printed to the report
	 */
	public static void logHeadingMessage(String message) {
		log(Color.BLACK, Css.UNDERLINE_SILVER_1PX_SOLID, message);
	}

	/**
	 * Prints error message to Report
	 * 
	 * @param message
	 *            Message to be printed to the report
	 */
	public static void logErrorMessage(String message) {
		log(Color.RED, Css.UNDERLINE_NONE, message);
	}

	/**
	 * Prints warning message to Report
	 * 
	 * @param message
	 *            Message to be printed to the report
	 */
	public static void logWarnMessage(String message) {
		log(Color.ORANGE, Css.UNDERLINE_NONE, message);
	}

	/*------------------------ on Failure Actions--------------------------*/

	/**
	 * Prints logs generated by selenium into selenium log
	 * 
	 * @param logType
	 *            {@link LogType}
	 */
	public static void captureSeleniumLogs1(String logType) {
		try {
			if (BaseTest.getTestObject().getDriverManager().getActiveDriver().getWebDriver() != null) {
				File parentDir = new File(System.getProperty("automacent.reportdir") + File.separator + "logs");
				if (!parentDir.exists())
					parentDir.mkdirs();

				StandardOpenOption option = StandardOpenOption.APPEND;
				File file = new File(parentDir.getAbsolutePath() + File.separator + "selenium_" + logType + ".log");
				if (!file.exists())
					option = StandardOpenOption.CREATE;
				Path path = Paths.get(file.getAbsolutePath());

				LogEntries logEntries = BaseTest.getTestObject().getDriverManager().getActiveDriver().getWebDriver()
						.manage().logs().get(logType);
				List<String> browserLogs = new ArrayList<>();
				browserLogs.add("Iteration" + IterationManager.getManager().getIteration());
				for (LogEntry entry : logEntries)
					browserLogs.add(String.format("%s %s %s %s", logType.toUpperCase(), entry.getTimestamp(),
							entry.getLevel(), entry.getMessage()));
				try {
					Files.write(path, browserLogs, Charset.forName("UTF-8"), option);
				} catch (IOException e) {
					_logger.warn(String.format("Failed to print log entries for LogType - %s", logType.toUpperCase()),
							e);
				}
				_logger.info("Printing log entries for LogType - " + logType.toUpperCase());
			}
		} catch (Exception e) {
			_logger.warn("Error while capturing Selenium Logs LogType - " + logType.toUpperCase(), e);
		}
	}
}
