package com.automacent.fwk.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.automacent.fwk.enums.BrowserId;
import com.automacent.fwk.enums.ErrorCode;
import com.automacent.fwk.exceptions.SetupFailedFatalException;
import com.automacent.fwk.reporting.Logger;

import io.github.bonigarcia.wdm.DriverManagerType;
import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Driver object holding {@link WebDriver} instaces for Selenium Web Tests
 * 
 * @author sighil.sivadas
 *
 */
public class Driver {

	private static final Logger _logger = Logger.getLogger(Driver.class);

	protected Driver(String ieDriverLocation, String chromeDriverLocation, String geckoDriverLocation,
			String scriptTimeoutInSeconds, String pageLoadTimeoutInSeconds, String socketTimeoutInSeconds) {
		setIeDriverLocation(ieDriverLocation);
		setChromeDriverLocation(chromeDriverLocation);
		setGeckoDriverLocation(geckoDriverLocation);

		setScriptTimeoutInSeconds(scriptTimeoutInSeconds);
		setPageLoadTimeoutInSeconds(pageLoadTimeoutInSeconds);
		setSocketTimeoutInSeconds(socketTimeoutInSeconds);
	}

	protected Driver(String ieDriverLocation, String chromeDriverLocation, String geckoDriverLocation,
			long scriptTimeoutInSeconds, long pageLoadTimeoutInSeconds, long socketTimeoutInSeconds,
			BrowserId browserId) {
		this.ieDriverLocation = ieDriverLocation;
		this.chromeDriverLocation = chromeDriverLocation;
		this.geckoDriverLocation = geckoDriverLocation;

		setScriptTimeoutInSeconds(scriptTimeoutInSeconds);
		setPageLoadTimeoutInSeconds(pageLoadTimeoutInSeconds);
		setSocketTimeoutInSeconds(socketTimeoutInSeconds);
		setBrowserId(browserId);
	}

	// Default Driver -------------------------------------

	private static Driver defaultDriver;

	/**
	 * Setup default {@link Driver}. This will be used when setting up Test suite
	 * level parameters in the {@link BaseTestSelenium}
	 * 
	 * @param ieDriverLocation
	 *            Path of the IE driver server executable
	 * @param chromeDriverLocation
	 *            Path of the Chrome driver server executable
	 * @param geckoDriverLocation
	 *            Path of the Firefox driver server executable
	 * @param scriptTimeoutInSeconds
	 *            Selenium javascript timeout
	 * @param pageLoadTimeoutInSeconds
	 *            Selenium page load timeout
	 * @param socketTimeoutInSeconds
	 *            {@link WebDriver} SocketTimeoutException timeout
	 */
	public static void setupDefaultDriver(String ieDriverLocation, String chromeDriverLocation,
			String geckoDriverLocation,
			String scriptTimeoutInSeconds, String pageLoadTimeoutInSeconds, String socketTimeoutInSeconds) {
		defaultDriver = new Driver(ieDriverLocation, chromeDriverLocation, geckoDriverLocation,
				scriptTimeoutInSeconds, pageLoadTimeoutInSeconds, socketTimeoutInSeconds);
	}

	/**
	 * 
	 * @return Default {@link Driver} instance
	 */
	public static Driver getDefaultDriver() {
		if (defaultDriver == null) {
			throw new SetupFailedFatalException("Default Driver instance is not configured");
		}
		return defaultDriver;
	}

	public static Driver cloneDefaultDriver(BrowserId browserId) {
		return new Driver(getDefaultDriver().getIeDriverLocation(), getDefaultDriver().getChromeDriverLocation(),
				getDefaultDriver().getGeckoDriverLocation(), getDefaultDriver().getScriptTimeoutInSeconds(),
				getDefaultDriver().getPageLoadTimeoutInSeconds(), getDefaultDriver().getSocketTimeoutInSeconds(),
				browserId);
	}

	// Variables ------------------------------------------

	private String ieDriverLocation;
	private String chromeDriverLocation;
	private String geckoDriverLocation;

	private long scriptTimeoutInSeconds = 300;
	private long pageLoadTimeoutInSeconds = 300;
	private long socketTimeoutInSeconds = 300;

	private BrowserId browserId;
	private WebDriver webDriver;
	private Boolean browserCrashed = false;

	// Executables --------------------------------------------------
	/**
	 * 
	 * @return absolute IE driver server executable path
	 */
	public String getIeDriverLocation() {
		return ieDriverLocation;
	}

	/**
	 * Set Custom IE driver server executable path.
	 * 
	 * @param ieDriverLocation
	 *            IE driver server executable path
	 */
	private void setIeDriverLocation(String ieDriverLocation) {
		if (!ieDriverLocation.equals("") && isCustomDriverFound(ieDriverLocation)) {
			this.ieDriverLocation = ieDriverLocation;
			System.setProperty("webdriver.ie.driver", getIeDriverLocation());
			_logger.info(String.format("ieDriverLocation set to %s", getIeDriverLocation()));
		} else {
			if (ieDriverLocation.equals(""))
				_logger.info("No custom ieDriverLocation provided");
			else
				_logger.warn("Invalid custom ieDriverLocation provided. Will use default");
		}
	}

	/**
	 * 
	 * @return absolute Chrome driver server executable path
	 */
	public String getChromeDriverLocation() {
		return chromeDriverLocation;
	}

	/**
	 * Set Custom Chrome driver server executable path.
	 * 
	 * @param chromeDriverLocation
	 *            Chrome driver server executable path
	 */
	private void setChromeDriverLocation(String chromeDriverLocation) {
		if (!chromeDriverLocation.equals("") && isCustomDriverFound(chromeDriverLocation)) {
			this.chromeDriverLocation = chromeDriverLocation;
			System.setProperty("webdriver.chrome.driver", getChromeDriverLocation());
			_logger.info(String.format("chromeDriverLocation set to %s", getChromeDriverLocation()));
		} else {
			if (chromeDriverLocation.equals(""))
				_logger.info("No custom chromeDriverLocation provided");
			else
				_logger.warn("Invalid custom chromeDriverLocation provided. Will use default");
		}
	}

	/**
	 * 
	 * @return absolute Firefox driver server executable path
	 */
	public String getGeckoDriverLocation() {
		return geckoDriverLocation;
	}

	/**
	 * Set Firefox driver server executable path.
	 * 
	 * @param geckoDriverLocation
	 *            Firefox driver server executable path
	 */
	private void setGeckoDriverLocation(String geckoDriverLocation) {
		if (!geckoDriverLocation.equals("") && isCustomDriverFound(geckoDriverLocation)) {
			this.geckoDriverLocation = geckoDriverLocation;
			System.setProperty("webdriver.gecko.driver", getGeckoDriverLocation());
			_logger.info(String.format("geckoDriverLocation set to %s", getGeckoDriverLocation()));
		} else {
			if (geckoDriverLocation.equals(""))
				_logger.info("No custom geckoDriverLocation provided");
			else
				_logger.warn("Invalid custom geckoDriverLocation provided. Will use default");
		}
	}

	/**
	 * Check if the provided driver server executable path is valid. If path is not
	 * valid, driver server executable packaged in the framework is extracted and
	 * its absolute path is returned
	 * 
	 * @param driverServerLocation
	 *            path of driver server executable
	 * @param driverName
	 *            Name of the driver
	 * @return
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private String extractDrivers(String driverServerLocation, String driverName) {
		File givenFile = new File(driverServerLocation);
		if (!givenFile.exists()) {
			try {
				_logger.info(String.format("Provided driver file, %s, does not exist for %s. "
						+ "Framework will extract and use the default driver executable", driverServerLocation,
						driverName));
				byte[] buffer = new byte[4096];
				String parent = "target" + File.separator + "driver";

				File f = new File(parent);
				if (!f.exists())
					f.mkdirs();

				File driverServer = new File(parent + File.separator + driverName);
				if (!driverServer.exists())
					driverServer.createNewFile();

				FileOutputStream output = new FileOutputStream(driverServer);
				InputStream input = Driver.class.getClassLoader()
						.getResourceAsStream("drivers/" + driverName);
				int bytesRead = input.read(buffer);
				while (bytesRead != -1) {
					output.write(buffer, 0, bytesRead);
					bytesRead = input.read(buffer);
				}
				output.close();
				input.close();
				return driverServer.getAbsolutePath();
			} catch (IOException e) {
				throw new SetupFailedFatalException("Error setting up driver executable", e);
			}
		}
		return givenFile.getAbsolutePath();
	}

	/**
	 * Check if the provided driver server executable path is valid. If path is not
	 * valid
	 * 
	 * @param driverServerLocation
	 *            path of driver server executable
	 * @return true if valid
	 */
	private boolean isCustomDriverFound(String driverServerLocation) {
		File givenFile = new File(driverServerLocation);
		return givenFile.exists();
	}

	// Timeouts -----------------------------------------------------

	/**
	 * 
	 * @return Selenium javascript timeout
	 */
	public long getScriptTimeoutInSeconds() {
		return scriptTimeoutInSeconds;
	}

	/**
	 * Set the Selenium javascript timeout
	 * 
	 * @param scriptTimeoutInSeconds
	 *            Selenium javascript timeout
	 */
	private void setScriptTimeoutInSeconds(long scriptTimeoutInSeconds) {
		this.scriptTimeoutInSeconds = scriptTimeoutInSeconds;
	}

	/**
	 * Set the Selenium javascript timeout. The String value of the parameter is
	 * converted to long value and set to variable. If parameter is invalid default
	 * value is set
	 * 
	 * @param scriptTimeoutInSeconds
	 *            Selenium javascript timeout
	 */
	private void setScriptTimeoutInSeconds(String scriptTimeoutInSeconds) {
		try {
			this.scriptTimeoutInSeconds = Long.parseLong(scriptTimeoutInSeconds);
		} catch (Exception e) {
			_logger.warn(String.format("%s scriptTimeoutInSeconds must be a number. Default value will be used",
					ErrorCode.INVALID_PARAMETER_VALUE.name()));
		}
		_logger.info(String.format("scriptTimeoutInSeconds set to %s", getScriptTimeoutInSeconds()));
	}

	/**
	 * 
	 * @return Selenium page load timeout
	 */
	public long getPageLoadTimeoutInSeconds() {
		return pageLoadTimeoutInSeconds;
	}

	/**
	 * Set the Selenium page load timeout
	 * 
	 * @param pageLoadTimeoutInSeconds
	 *            Selenium page load timeout
	 */
	private void setPageLoadTimeoutInSeconds(long pageLoadTimeoutInSeconds) {
		this.pageLoadTimeoutInSeconds = pageLoadTimeoutInSeconds;
	}

	/**
	 * Set the Selenium page load timeout. The String value of the parameter is
	 * converted to long value and set to variable. If parameter is invalid default
	 * value is set
	 * 
	 * @param pageLoadTimeoutInSeconds
	 *            Selenium page load timeout
	 */
	private void setPageLoadTimeoutInSeconds(String pageLoadTimeoutInSeconds) {
		try {
			this.pageLoadTimeoutInSeconds = Long.parseLong(pageLoadTimeoutInSeconds);
		} catch (Exception e) {
			_logger.warn(String.format("%s pageLoadTimeoutInSeconds must be a number. Default value will be used",
					ErrorCode.INVALID_PARAMETER_VALUE.name()));
		}
		_logger.info(String.format("pageLoadTimeoutInSeconds set to %s", getPageLoadTimeoutInSeconds()));
	}

	/**
	 * 
	 * @return {@link WebDriver} SocketTimeoutException timeout
	 */
	public long getSocketTimeoutInSeconds() {
		return socketTimeoutInSeconds;
	}

	/**
	 * Set the {@link WebDriver} SocketTimeoutException timeout
	 * 
	 * @param socketTimeoutInSeconds
	 *            {@link WebDriver} SocketTimeoutException timeout
	 */
	private void setSocketTimeoutInSeconds(long socketTimeoutInSeconds) {
		this.socketTimeoutInSeconds = socketTimeoutInSeconds;
	}

	/**
	 * Set the {@link WebDriver} SocketTimeoutException timeout. The String value of
	 * the parameter is converted to long value and set to variable. If parameter is
	 * invalid default value is set
	 * 
	 * @param socketTimeoutInSeconds
	 *            {@link WebDriver} SocketTimeoutException timeout
	 */
	private void setSocketTimeoutInSeconds(String socketTimeoutInSeconds) {
		try {
			this.socketTimeoutInSeconds = Long.parseLong(socketTimeoutInSeconds);
		} catch (Exception e) {
			_logger.warn(String.format("%s socketTimeoutInSeconds must be a number. Default value will be used",
					ErrorCode.INVALID_PARAMETER_VALUE.name()));
		}
		_logger.info(String.format("socketTimeoutInSeconds set to %s", getSocketTimeoutInSeconds()));
	}

	// Browser Id ---------------------------------------------------

	/**
	 * 
	 * @return {@link BrowserId} for the driver instance
	 */
	public BrowserId getBrowserId() {
		return browserId;
	}

	/**
	 * Set {@link BrowserId} for the driver instance
	 * 
	 * @param browserId
	 *            {@link BrowserId}
	 */
	private void setBrowserId(BrowserId browserId) {
		this.browserId = browserId;
	}

	// WebDriver ----------------------------------------------------

	/**
	 *
	 * @return {@link WebDriver}
	 */
	public WebDriver getWebDriver() {
		return webDriver;
	}

	/**
	 * This method initializes the driver (opens browser), maximizes browser window,
	 * sets timeouts and deletes the cookies.
	 * 
	 * @param browser
	 *            {@link Browser}
	 */
	public void startDriver(DriverManagerType browser) {
		try {
			if (browser.name().equals(DriverManagerType.IEXPLORER.name())) {
				DesiredCapabilities capab = DesiredCapabilities.internetExplorer();
				capab.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
				capab.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
				capab.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, false);
				InternetExplorerOptions ieOptions = new InternetExplorerOptions(capab);
				if (ieDriverLocation == null) {
					WebDriverManager.getInstance(DriverManagerType.IEXPLORER).setup();
					_logger.info("Using ieDriver from framework");
				}
				webDriver = new InternetExplorerDriver(ieOptions);
			} else if (browser.name().equals(DriverManagerType.CHROME.name())) {
				if (chromeDriverLocation == null) {
					WebDriverManager.getInstance(DriverManagerType.CHROME).setup();
					_logger.info("Using chromeDriver from framework");
				}
				webDriver = new ChromeDriver();
			} else if (browser.name().equals(DriverManagerType.FIREFOX.name())) {
				if (geckoDriverLocation == null) {
					WebDriverManager.getInstance(DriverManagerType.FIREFOX).setup();
					_logger.info("Using geckoDriver from framework");
				}
				webDriver = new FirefoxDriver();
			} else if (browser.name().equals(DriverManagerType.CHROMIUM.name())) {
				WebDriverManager.getInstance(DriverManagerType.CHROMIUM).setup();
				webDriver = new ChromeDriver();
			}
		} catch (Exception e) {
			throw new SetupFailedFatalException("Error initializing the driver", e);
		}

		webDriver.manage().timeouts().pageLoadTimeout(getPageLoadTimeoutInSeconds(), TimeUnit.MINUTES);
		_logger.info(String.format("Page Load timeout set to %s seconds", getPageLoadTimeoutInSeconds()));
		webDriver.manage().timeouts().implicitlyWait(BaseTest.getTestObject().getTimeoutInSeconds(), TimeUnit.SECONDS);
		_logger.info(String.format("Implicit wait set on driver to %s seconds",
				BaseTest.getTestObject().getTimeoutInSeconds()));
		webDriver.manage().timeouts().setScriptTimeout(getScriptTimeoutInSeconds(), TimeUnit.SECONDS);
		_logger.info(String.format("Script timeout set on driver to %s seconds", getScriptTimeoutInSeconds()));
		webDriver.manage().window().maximize();
		webDriver.manage().deleteAllCookies();
		_logger.info("Cookies deleted");
	}

	/**
	 * Close and quit driver
	 */
	public void terminateDriver() {
		if (webDriver != null) {
			_logger.info(String.format("Quiting driver %s", webDriver));
			webDriver.close();
			webDriver.quit();
		} else {
			_logger.warn(String.format("Driver %s is already dead", webDriver));
		}
	}

	// Browser crash --------------------------------------

	/**
	 * Check if browser has crashed
	 * 
	 * @return True if browser is crashed
	 */
	public Boolean isbrowserCrashed() {
		return browserCrashed;
	}

	/**
	 * Mark browser as crashed
	 */
	public void markBrowserCrashed() {
		this.browserCrashed = true;
	}

}
