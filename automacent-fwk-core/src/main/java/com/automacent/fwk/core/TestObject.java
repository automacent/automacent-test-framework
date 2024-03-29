package com.automacent.fwk.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.automacent.fwk.annotations.Action;
import com.automacent.fwk.enums.ErrorCode;
import com.automacent.fwk.enums.RepeatMode;
import com.automacent.fwk.enums.RetryMode;
import com.automacent.fwk.enums.ScreenshotMode;
import com.automacent.fwk.enums.ScreenshotModeForIteration;
import com.automacent.fwk.enums.ScreenshotType;
import com.automacent.fwk.exceptions.SetupFailedFatalException;
import com.automacent.fwk.recovery.RecoveryManager;
import com.automacent.fwk.reporting.Logger;
import com.automacent.fwk.utils.EnumUtils;

import io.github.bonigarcia.wdm.config.DriverManagerType;


/**
 * Class which holds parameters required for execution of Test. Each Test class
 * will be assigned a {@link TestObject}. This class is declared and used the
 * {@link BaseTest} class
 * 
 * @author sighil.sivadas
 */
public class TestObject {

	private static final Logger _logger = Logger.getLogger(TestObject.class);

	// Test parameters ----------------------------------------------

	private ITestContext testContext;

	/**
	 * Get the test parameters from the {@link ITestContext#getCurrentXmlTest()}
	 * 
	 * @return {@link Map} of parameter key-value pairs
	 */
	public Map<String, String> getTestParameters() {
		if (testContext == null)
			throw new SetupFailedFatalException(
					"Failed to retrieve test parameters. Test Context not set in Test Object. Test will exit");
		return testContext.getCurrentXmlTest().getAllParameters();
	}

	/**
	 * Get Test parameter pertaining to the current {@link Test}
	 * 
	 * @param key Name of the TestNG {@link Parameters}
	 * @return Value of the parameter
	 */
	public String getTestParameter(String key) {
		String value = getTestParameters().get(key);
		if (value == null)
			throw new SetupFailedFatalException(
					String.format("Requested Test Parameter, %s, not found. Test will exit", key));
		_logger.info(String.format("Fetched test parameter, %s, with value %s", key, value));
		return value;
	}

	/**
	 * Add Test Parameter
	 * 
	 * @param key   Test parameter Key
	 * @param value Test Parameter Value
	 */
	public void addTestParameter(String key, String value) {
		_logger.info(String.format("Setting test parameter { %s : %s }", key, value));
		testContext.getCurrentXmlTest().addParameter(key, value);
		_logger.debug(String.format("Test parameters %s", getTestParameters().toString()));
	}

	/**
	 * This method will append the provided value with the key as comma seperated
	 * values
	 * 
	 * @param key   Parameter key
	 * @param value Parameter Value
	 */
	public void appendTestParameter(String key, String value) {
		String existing = "";
		try {
			existing = getTestParameter(key);
		} catch (SetupFailedFatalException e) {
		}
		if (!existing.isEmpty()) {
			value = String.format("%s,%s", existing, value);
		}
		addTestParameter(key, value);
	}

	/**
	 * Set the {@link ITestContext} related to the current XML test being executed.
	 * 
	 * @param testContext {@link ITestContext}
	 */
	public void setTestContext(ITestContext testContext) {
		_logger.info(String.format("Setting test context %s", testContext));
		this.testContext = testContext;
		setTestName(testContext.getCurrentXmlTest().getName());
	}

	// Driver Manager -----------------------------------------------

	private DriverManager driverManager;

	/**
	 * Get the driver manager
	 * 
	 * @return {@link DriverManager} object
	 */
	public DriverManager getDriverManager() {
		return driverManager;
	}

	/**
	 * Set the {@link DriverManager} instance
	 * 
	 * @param driverManager {@link DriverManager} instance
	 */
	public void setDriverManager(DriverManager driverManager) {
		this.driverManager = driverManager;
	}

	// Debugger Address ----------------------------------------------------

	private String debuggerAddress = "";

	/**
	 * set the debugger address
	 * 
	 * @param debuggerAddress Port of running chrome browser which selenium can take
	 *                        control of
	 */
	public void setDebuggerAddress(String debuggerAddress) {
		if (!debuggerAddress.isEmpty()) {
			if (this.getDriverManager().getDriverManagerType().name().equals(DriverManagerType.CHROME.name())) {
				this.debuggerAddress = debuggerAddress;
				_logger.info(String.format("debuggerAddress set to %s", getDebuggerAddress()));
			} else {
				_logger.warn(String.format("debuggerAddress cannot be set because browser is not %s",
						DriverManagerType.CHROME.name()));
			}
		}
	}

	/**
	 * 
	 * @return debuggerAddress
	 */
	public String getDebuggerAddress() {
		return debuggerAddress;
	}

	// Download Location --------------------------------------------

	private String downloadLocation = "";

	/**
	 * set the download location
	 * 
	 * @param downloadLocation Location for downloaded files on browser
	 */
	public void setDownloadLocation(String downloadLocation) {
		if (!downloadLocation.isEmpty()) {
			try {
				File directory = new File(downloadLocation);
				if (!directory.exists())
					directory.mkdirs();
				this.downloadLocation = directory.getAbsolutePath();
			} catch (Exception e) {
				_logger.warn("Error setting download location", e);
			}
		}

		_logger.info(String.format("debuggerAddress set to %s", getDebuggerAddress()));

	}

	/**
	 * 
	 * @return downloadLocation
	 */
	public String getDownloadLocation() {
		return downloadLocation;
	}

	// Test Name ----------------------------------------------------

	private String testName = "undefined";

	/**
	 * @return Test name
	 */
	public String getTestName() {
		return testName;
	}

	/**
	 * Set Test name
	 * 
	 * @param testName Name of the test
	 */
	private void setTestName(String testName) {
		if (!testName.trim().isEmpty())
			this.testName = testName;
		else
			_logger.warn(String.format("%s Blank testName parameter provided. Will use default test name",
					ErrorCode.INVALID_PARAMETER_VALUE.name()));
		_logger.info(String.format("testName set to %s", getTestName()));
	}

	// Timeouts -----------------------------------------------------

	private long timeoutInSeconds = 20;
	private long slowdownDurationInSeconds = 1;

	/**
	 * Get timeout value. This is the value which defines the time execution will
	 * poll/wait before throwing out an Exception
	 * 
	 * @return timeout in seconds
	 */
	public long getTimeoutInSeconds() {
		return timeoutInSeconds;
	}

	/**
	 * Set timeout value. This is the value which defines the time execution will
	 * poll/wait before throwing out an Exception
	 * 
	 * @param timeoutInSeconds Timeout in Seconds
	 */
	public void setTimeoutInSeconds(long timeoutInSeconds) {
		this.timeoutInSeconds = timeoutInSeconds;
		_logger.info(String.format("timeoutInSeconds set to %s", getTimeoutInSeconds()));
	}

	/**
	 * Get the wait time between execution of subsequent {@link Action} methods
	 * 
	 * @return Slow down duration in seconds
	 */
	public long getSlowdownDurationInSeconds() {
		return slowdownDurationInSeconds;
	}

	/**
	 * Set the wait time between execution of subsequent {@link Action} methods
	 * 
	 * @param slowdownDurationInSeconds Slow down duration in seconds
	 */
	public void setSlowdownDurationInSeconds(long slowdownDurationInSeconds) {
		this.slowdownDurationInSeconds = slowdownDurationInSeconds;
		_logger.info(String.format("slowdownDurationInSeconds set to %s", getSlowdownDurationInSeconds()));
	}

	// Repeat Mode --------------------------------------------------

	private RepeatMode repeatMode;
	private long testDurationInSeconds = 0;
	private long delayBetweenIterationInSeconds = 0;
	private long invocationCount = 1;

	/**
	 * Get {@link RepeatMode}. This parameter determines whether a Test method has
	 * to be repeated and the mode of repetition
	 * 
	 * @return {@link RepeatMode}
	 */
	public RepeatMode getRepeatMode() {
		return repeatMode;
	}

	/**
	 * Set {@link RepeatMode}. This parameter determines whether a Test method has
	 * to be repeated and the mode of repetition
	 * 
	 * @param repeatMode {@link RepeatMode}
	 */
	public void setRepeatMode(RepeatMode repeatMode) {
		this.repeatMode = repeatMode;
		_logger.info(String.format("repeatMode set to %s", getRepeatMode()));
	}

	/**
	 * Get test duration. This parameter is used if {@link RepeatMode} is set to
	 * {@link RepeatMode#TEST_DURATION}. The test will repeat untill Test duration
	 * is exceeded
	 * 
	 * @return Test duration in seconds
	 */
	public long getTestDurationInSeconds() {
		return testDurationInSeconds;
	}

	/**
	 * Set test duration. This parameter is used if {@link RepeatMode} is set to
	 * {@link RepeatMode#TEST_DURATION}. The test will repeat untill Test duration
	 * is exceeded
	 * 
	 * @param testDurationInSeconds Test Duration in seconds
	 */
	public void setTestDurationInSeconds(long testDurationInSeconds) {
		this.testDurationInSeconds = testDurationInSeconds;
		_logger.info(String.format("testDurationInSeconds set to %s", getTestDurationInSeconds()));
	}

	/**
	 * Get invocation count. This parameter is used if {@link RepeatMode} is set to
	 * {@link RepeatMode#INVOCATION_COUNT}. The test will repeat till the set
	 * invocation count value
	 * 
	 * @return invocation count
	 */
	public long getInvocationCount() {
		return invocationCount;
	}

	/**
	 * Set invocation count. This parameter is used if {@link RepeatMode} is set to
	 * {@link RepeatMode#INVOCATION_COUNT}. The test will repeat till the set
	 * invocation count value
	 * 
	 * @param invocationCount Invocation count value
	 */
	public void setInvocationCount(long invocationCount) {
		this.invocationCount = invocationCount;
		_logger.info(String.format("invocationCount set to %s", getInvocationCount()));
	}

	/**
	 * Get delay between iterations. This parameter is used if {@link RepeatMode} is
	 * set to {@link RepeatMode#TEST_DURATION} or
	 * {@link RepeatMode#INVOCATION_COUNT} and determines the wait time between
	 * subsequent iterations/repeat of test methods
	 * 
	 * @return Delay between iterations
	 */
	public long getDelayBetweenIterationInSeconds() {
		return delayBetweenIterationInSeconds;
	}

	/**
	 * Set delay between iterations. This parameter is used if {@link RepeatMode} is
	 * set to {@link RepeatMode#TEST_DURATION} or
	 * {@link RepeatMode#INVOCATION_COUNT} and determines the wait time between
	 * subsequent iterations/repeat of test methods
	 * 
	 * @param delayBetweenIterationInSeconds Delay between iteration in seconds
	 */
	public void setDelayBetweenIterationInSeconds(long delayBetweenIterationInSeconds) {
		this.delayBetweenIterationInSeconds = delayBetweenIterationInSeconds;
		_logger.info(String.format("delayBetweenIterationInSeconds set to %s", getDelayBetweenIterationInSeconds()));
	}

	// Screenshot ---------------------------------------------------

	private ScreenshotType screenshotType;
	private final List<ScreenshotMode> screenshotModes = new ArrayList<>();
	private ScreenshotModeForIteration screenshotModeForIteration;

	/**
	 * Get {@link ScreenshotType}. This Parameter determines the type of screenshot
	 * taken in case of web based tests
	 * 
	 * @return {@link ScreenshotType}
	 */
	public ScreenshotType getScreenshotType() {
		return screenshotType;
	}

	/**
	 * Set {@link ScreenshotType}. This Parameter determines the type of screenshot
	 * taken in case of web based tests
	 * 
	 * @param screenshotType {@link ScreenshotType}
	 */
	public void setScreenshotType(ScreenshotType screenshotType) {
		this.screenshotType = screenshotType;
		_logger.info(String.format("screenshotType set to %s", getScreenshotType()));
	}

	/**
	 * Set {@link ScreenshotMode}. This parameter determines the locations where
	 * screenshot has to be taken
	 * 
	 * @return {@link ScreenshotMode}
	 */
	public List<ScreenshotMode> getScreenshotModes() {
		return screenshotModes;
	}

	/**
	 * Set {@link ScreenshotMode}. This parameter determines the locations where
	 * screenshot has to be taken.
	 * 
	 * @param screenshotModes Comma separated values of screenshot modes
	 */
	public void setScreenshotModes(String screenshotModes) {
		String[] screenshotModeArray = screenshotModes.split(",");
		for (String screenshotModeString : screenshotModeArray) {
			ScreenshotMode screenshotMode = EnumUtils.getEnumFromString(ScreenshotMode.class, screenshotModeString);
			if (screenshotMode != null)
				this.screenshotModes.add(screenshotMode);
			else
				_logger.warn(String.format("%s for screenshotMode. Expected one of %s. Got %s",
						ErrorCode.INVALID_PARAMETER_VALUE.name(), ScreenshotMode.values(), screenshotMode));
		}

		if (this.screenshotModes.isEmpty()) {
			_logger.warn(String.format("%s for screenshotMode. No valid values set. Default value will be set",
					ErrorCode.INVALID_PARAMETER_VALUE.name()));
			this.screenshotModes.add(ScreenshotMode.getDefault());
		}

		if (!this.screenshotModes.contains(ScreenshotMode.getDefault()))
			this.screenshotModes.add(ScreenshotMode.getDefault());

		_logger.info(String.format("screenshotModes set to %s", getScreenshotModes()));
	}

	/**
	 * Get {@link ScreenshotModeForIteration}. This parameter determines how the
	 * screenshots are saved for iterations/repeat of test methods
	 * 
	 * @return {@link ScreenshotModeForIteration}
	 */
	public ScreenshotModeForIteration getScreenshotModeForIteration() {
		return screenshotModeForIteration;
	}

	/**
	 * Set {@link ScreenshotModeForIteration} This parameter determines how the
	 * screenshots are saved for iterations/repeat of test methods
	 * 
	 * @param screenshotModeForIteration {@link ScreenshotModeForIteration}
	 */
	public void setScreenshotModeForIteration(ScreenshotModeForIteration screenshotModeForIteration) {
		this.screenshotModeForIteration = screenshotModeForIteration;
		_logger.info(String.format("screenshotModeForIteration set to %s", getScreenshotModeForIteration()));
	}

	// Retry mode ---------------------------------------------------

	private RetryMode retryMode;
	private RecoveryManager recoveryManager;

	/**
	 * Get {@link RetryMode}. This parameter determines whether retry should be
	 * performed on test failure
	 * 
	 * @return {@link RetryMode}
	 */
	public RetryMode getRetryMode() {
		return retryMode;
	}

	/**
	 * Set {@link RetryMode}. This parameter determines whether retry should be
	 * performed on test failure
	 * 
	 * @param retryMode {@link RetryMode}
	 */
	public void setRetryMode(RetryMode retryMode) {
		this.retryMode = retryMode;
		_logger.info(String.format("retryMode set to %s", getRetryMode()));
	}

	/**
	 * Get {@link RecoveryManager}. The parameter sets the recovery classes to be
	 * called in case the test fails and had to be retried
	 * 
	 * @return {@link RecoveryManager} instance
	 */
	public RecoveryManager getRecoveryManager() {
		return recoveryManager;
	}

	/**
	 * Set {@link RecoveryManager} instance. The parameter sets the recovery classes
	 * to be called in case the test fails and had to be retried
	 * 
	 * @param recoveryManager {@link RecoveryManager} instance
	 */
	public void setRecoveryManager(RecoveryManager recoveryManager) {
		this.recoveryManager = recoveryManager;
	}

	// Base URL -----------------------------------------------------

	private String baseUrl;

	/**
	 * Get the base URL String
	 * 
	 * @return base URL String
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * Set base URL String
	 * 
	 * @param baseUrl Base URL String
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
}
