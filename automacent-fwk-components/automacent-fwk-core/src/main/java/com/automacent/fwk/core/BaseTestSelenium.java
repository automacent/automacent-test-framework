package com.automacent.fwk.core;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.automacent.fwk.enums.BrowserId;
import com.automacent.fwk.enums.ScreenshotMode;
import com.automacent.fwk.enums.ScreenshotModeForIteration;
import com.automacent.fwk.enums.ScreenshotType;

import io.github.bonigarcia.wdm.DriverManagerType;

/**
 * All Selenium test classes must inherit this class. Class contains essential
 * setup code for the framework to work
 * 
 * @author sighil.sivadas
 **/

public abstract class BaseTestSelenium extends BaseTest {

	/**
	 * Set up parameters required by the framework for managing Selenium drivers.
	 * These parameters are set at Test suite level
	 * 
	 * @param ieDriverLocation
	 *            Absolute/relative path to the location of IE driver server
	 *            executable
	 * @param chromeDriverLocation
	 *            Absolute/relative path to the location of Chrome driver server
	 *            executable
	 * @param geckoDriverLocation
	 *            Absolute/relative path to the location of Firefox driver server
	 *            executable
	 * @param scriptTimeoutInSeconds
	 *            Timeout for Selenium Javascript Executor script
	 * @param pageLoadTimeoutInSeconds
	 *            Timeout for browser page load
	 * @param socketTimeoutInSeconds
	 *            Timeout for socket exceptions
	 */
	@BeforeSuite
	@Parameters({ "ieDriverLocation", "chromeDriverLocation", "geckoDriverLocation", "scriptTimeoutInSeconds",
			"pageLoadTimeoutInSeconds", "socketTimeoutInSeconds" })
	public void automacentInternalSetDriverParameters(
			@Optional("") String ieDriverLocation,
			@Optional("") String chromeDriverLocation,
			@Optional("") String geckoDriverLocation,
			@Optional("300") String scriptTimeoutInSeconds,
			@Optional("300") String pageLoadTimeoutInSeconds,
			@Optional("300") String socketTimeoutInSeconds) {
		Driver.setupDefaultDriver(ieDriverLocation, chromeDriverLocation, geckoDriverLocation,
				scriptTimeoutInSeconds, pageLoadTimeoutInSeconds, socketTimeoutInSeconds);
	}

	/**
	 * Set uo parameters required for Web Test. These parameters are set at test
	 * level and custom values per test can be provided
	 * 
	 * @param browser
	 *            {@link Browser}
	 * @param screenshotType
	 *            {@link ScreenshotType}
	 * @param screenshotMode
	 *            comma seperated {@link ScreenshotMode} values
	 * @param screenshotModeForIteration
	 *            {@link ScreenshotModeForIteration}
	 * @param testContext
	 *            testNg {@link ITestContext}
	 */
	@BeforeTest
	@Parameters({ "browser", "screenshotType", "screenshotMode", "screenshotModeForIteration", "baseUrl" })
	public void automacentInternalSetWebTestParameters(
			@Optional("CHROME") String browser,
			@Optional("BROWSER_SCREENSHOT") String screenshotType,
			@Optional("ON_FAILURE") String screenshotMode,
			@Optional("LAST_ITERATION") String screenshotModeForIteration,
			String baseUrl,
			ITestContext testContext) {
		TestObject testObject = BaseTest.getTestObject();
		testObject.setDriverManager(new DriverManager());
		testObject.getDriverManager().setBrowser(browser);
		testObject.setScreenshotType(screenshotType);
		testObject.setScreenshotModes(screenshotMode);
		testObject.setScreenshotModeForIteration(screenshotModeForIteration);
		testObject.setBaseUrl(baseUrl);
	}

	/**
	 * Start the primary browser with {@link BrowserId} ALPHA and default. The
	 * browser, on starting becomes the active browser/driver. Use
	 * {@link #setActiveDriver(BrowserId)} to change the active browser/driver
	 * {@link Browser}
	 */
	protected void startBrowser() {
		BaseTest.getTestObject().getDriverManager().startBrowser(this);
	}

	/**
	 * Start new browser with provided {@link BrowserId} and default
	 * {@link Browser}. The browser, on starting becomes the active browser/driver.
	 * Use {@link #setActiveDriver(BrowserId)} to change the active browser/driver
	 * 
	 * @param browserId
	 *            {@link BrowserId}
	 */
	protected void startBrowser(BrowserId browserId) {
		BaseTest.getTestObject().getDriverManager().startBrowser(this, browserId);
	}

	/**
	 * Start new browser with provided {@link BrowserId} and provided
	 * {@link Browser}. The browser, on starting becomes the active browser/driver.
	 * Use {@link #setActiveDriver(BrowserId)} to change the active browser/driver
	 * 
	 * @param browserId
	 *            {@link BrowserId}
	 * @param browser
	 *            {@link DriverManagerType}
	 */
	protected void startBrowser(BrowserId browserId, DriverManagerType browser) {
		BaseTest.getTestObject().getDriverManager().startBrowser(this, browserId, browser);
	}

	/**
	 * Set the browser/driver with the specified {@link BrowserId} as the active
	 * browser/driver.
	 * 
	 * @param browserId
	 *            {@link BrowserId}
	 */
	protected void setActiveDriver(BrowserId browserId) {
		BaseTest.getTestObject().getDriverManager().setActiveDriver(browserId, this);
	}

	/**
	 * Get the current active {@link WebDriver}. This is the only method that allows
	 * access to the {@link WebDriver} instance in the test cases
	 * 
	 * @return {@link WebDriver}
	 */
	public WebDriver getActiveDriver() {
		return BaseTest.getTestObject().getDriverManager().getActiveDriver().getWebDriver();
	}

	/**
	 * 
	 * @return {@link BrowserId} of the current active {@link WebDriver}
	 */
	public String getActiveDriverName() {
		return BaseTest.getTestObject().getDriverManager().getActiveDriver().getBrowserId().name();
	}

	/**
	 * Get the count of the total {@link WebDriver} instances spawned by the current
	 * test
	 * 
	 * @return count
	 */
	public int getDriverCount() {
		return BaseTest.getTestObject().getDriverManager().getDriverMapSize();
	}

	/**
	 * Close and quit the primary {@link WebDriver} instance with {@link BrowserId}
	 * ALPHA
	 */
	protected void quitBrowser() {
		BaseTest.getTestObject().getDriverManager().killBrowser();
	}

	/**
	 * Close and quit the {@link WebDriver} instance with the specified
	 * {@link BrowserId}
	 */
	protected void quitBrowser(BrowserId browserId) {
		BaseTest.getTestObject().getDriverManager().killBrowser(browserId);
	}
}
