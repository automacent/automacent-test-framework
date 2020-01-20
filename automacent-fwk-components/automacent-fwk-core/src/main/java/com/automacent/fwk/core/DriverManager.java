package com.automacent.fwk.core;

import java.util.HashMap;

import com.automacent.fwk.annotations.Steps;
import com.automacent.fwk.annotations.StepsAndPagesProcessor;
import com.automacent.fwk.enums.BrowserId;
import com.automacent.fwk.enums.ErrorCode;
import com.automacent.fwk.exceptions.SetupFailedFatalException;
import com.automacent.fwk.reporting.Logger;
import com.automacent.fwk.utils.StringUtils;

import io.github.bonigarcia.wdm.DriverManagerType;

/**
 * Manage {@link Driver} object. This class can initiliaze {@link Driver}
 * objects and manage multiple {@link Driver} instances
 * 
 * @author sighil.sivadas
 */
public class DriverManager {

	private static final Logger _logger = Logger.getLogger(DriverManager.class);

	/**
	 * Create a new {@link Driver} instance from the default {@link Driver} instance
	 * 
	 * @param browser
	 *            {@link Browser}
	 * @param browserId
	 *            {@link BrowserId}
	 * @return
	 */
	private Driver configureNewDriverFromDefaultDriver(BrowserId browserId) {
		return Driver.cloneDefaultDriver(browserId);
	}

	// Browser ------------------------------------------------------

	private DriverManagerType browser;

	/**
	 * If {@link Browser} value is not set, the default value is returned
	 * 
	 * @return {@link Browser}
	 */
	public DriverManagerType getBrowser() {
		if (this.browser == null)
			_logger.warn(String.format("%s Browser is not set. Default value, %s, will be used",
					ErrorCode.INVALID_PARAMETER_VALUE.name(), DriverManagerType.CHROME.name()));
		return browser != null ? browser : DriverManagerType.CHROME;
	}

	/**
	 * Set {@link Browser}. If invalid value is provided, default value is set
	 * 
	 * @param browser
	 *            {@link Browser}
	 */
	public void setBrowser(String browser) {
		this.browser = StringUtils.getEnumFromString(DriverManagerType.class, browser);
		if (this.browser == null) {
			_logger.warn(String.format("%s for browser. Expected one of %s. Got %s. Default value will be set",
					ErrorCode.INVALID_PARAMETER_VALUE.name(), DriverManagerType.values(), browser));
			this.browser = DriverManagerType.CHROME;
		}
		_logger.info(String.format("Browser set to %s", getBrowser()));
	}

	// Driver -------------------------------------------------------

	private HashMap<BrowserId, Driver> driverMap = new HashMap<>();

	/**
	 * Get Driver instance.
	 * 
	 * @param browserId
	 *            {@link Browser}
	 * @return {@link Driver} instance with provided {@link BrowserId}
	 */
	private Driver getDriver(BrowserId browserId) {
		Driver driver = driverMap.get(browserId);
		if (driver == null)
			throw new SetupFailedFatalException(
					String.format("Requested driver instance with browser id %s is not found",
							browserId.name()));
		return driver;
	}

	/**
	 * Initialize driver, open browser and set active {@link Driver} based on the
	 * primary {@link BrowserId} ALPHA and default {@linkplain #browser} set in the
	 * {@link DriverManger}
	 * 
	 * @param testClassInstance
	 *            Test class instance
	 * @param browserId
	 *            {@link BrowserId}
	 * @param browser
	 *            {@link Browser}
	 */
	public void startBrowser(Object testClassInstance) {
		startBrowser(testClassInstance, BrowserId.getDefault());
	}

	/**
	 * Initialize driver, open browser and set active {@link Driver} based on the
	 * provided {@link BrowserId} and default {@linkplain #browser} set in the
	 * {@link DriverManger}
	 * 
	 * @param testClassInstance
	 *            Test class instance
	 * @param browserId
	 *            {@link BrowserId}
	 * @param browser
	 *            {@link Browser}
	 */
	public void startBrowser(Object testClassInstance, BrowserId browserId) {
		startBrowser(testClassInstance, browserId, getBrowser());
	}

	/**
	 * Initialize driver, open browser and set active {@link Driver} based on the
	 * provided {@link BrowserId} and {@link Browser} parameters.
	 * 
	 * @param testClassInstance
	 *            Test class instance
	 * @param browserId
	 *            {@link BrowserId}
	 * @param browser
	 *            {@link Browser}
	 */
	public void startBrowser(Object testClassInstance, BrowserId browserId, DriverManagerType browser) {
		if (driverMap.containsKey(browserId)) {
			throw new SetupFailedFatalException(
					String.format("Error starting new browser. The provided browser id, %s, is already in use",
							browserId));
		} else {
			Driver driver = configureNewDriverFromDefaultDriver(browserId);
			driver.startDriver(browser);
			driverMap.put(browserId, driver);
			setActiveDriver(browserId, testClassInstance);
		}

	}

	// Active Driver ------------------------------------------------

	private Driver activeDriver;

	/**
	 * 
	 * @return active {@link Driver} object
	 */
	public Driver getActiveDriver() {
		return activeDriver;
	}

	/**
	 * Set active {@link Driver} and initialize {@link Steps} class fields with the
	 * active driver
	 * 
	 * @param browserId
	 * @param testClassInstance
	 */
	public void setActiveDriver(BrowserId browserId, Object testClassInstance) {
		this.activeDriver = getDriver(browserId);
		StepsAndPagesProcessor.processAnnotation(testClassInstance);
	}

	/**
	 * Return the count of {@link Driver} instances managed by current
	 * {@link DriverManager} instance
	 * 
	 * @return count of {@link Driver} instance
	 */
	public int getDriverMapSize() {
		return driverMap.size();
	}

	/**
	 * Close the default {@link Driver} instance with {@link BrowserId} ALPHA
	 */
	public void killBrowser() {
		killBrowser(BrowserId.getDefault());
	}

	/**
	 * Kill the {@link Driver} instance with provided {@link BrowserId}
	 * 
	 * @param browserId
	 *            {@link BrowserId}
	 */
	public void killBrowser(BrowserId browserId) {
		Driver driver = getDriver(browserId);
		driverMap.remove(browserId);
		driver.terminateDriver();
	}

	/**
	 * Open the set Base URL parameter
	 */
	public void openBaseUrl() {
		getActiveDriver().getWebDriver().get(BaseTest.getTestObject().getBaseUrl());
	}
}
