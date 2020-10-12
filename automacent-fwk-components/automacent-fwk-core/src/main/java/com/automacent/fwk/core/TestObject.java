package com.automacent.fwk.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.automacent.fwk.utils.StringUtils;

/**
 * Class which holds parameters required for execution of Test. Each Test class
 * will be assinged a {@link TestObject}. This class is declared and used the
 * {@link BaseTest} class
 * 
 * @author sighil.sivadas
 */
public class TestObject {

	private static final Logger _logger = Logger.getLogger(TestObject.class);

	// Test parameters ----------------------------------------------

	private Map<String, Map<String, String>> testParametersMap = new HashMap<>();

	private Map<String, String> getTestParameters() {
		Map<String, String> testParameters = testParametersMap.get(testName);
		if (testParameters == null) {
			testParameters = new HashMap<String, String>();
		}
		testParametersMap.put(testName, testParameters);
		return testParameters;
	}

	/**
	 * Get Test parameter pertaining to the current {@link Test}
	 * 
	 * @param parameter Name of the TestNG {@link Parameters}
	 * @return Value of the parameter
	 */
	public String getTestParameter(String parameter) {
		String value = getTestParameters().get(parameter);
		if (value == null)
			throw new SetupFailedFatalException(
					String.format("Requested Test Parameter %s not found. Test will exit", parameter));
		return value;
	}

	/**
	 * Add Test Parameter
	 * 
	 * @param key   Test parameter Key
	 * @param value Test Parameter Value
	 */
	public void addTestParameter(String key, String value) {
		getTestParameters().put(key, value);
	}

	public void appendTestParameter(String key, String value) {
		String existing = "";
		try {
			existing = getTestParameter(key);
		} catch (SetupFailedFatalException e) {
		}
		if (!existing.isEmpty()) {
			value = String.format("%s,%s", existing, value);
		}
		getTestParameters().put(key, value);
	}

	/**
	 * Set all test parameters
	 * 
	 * @param testParameters {@link Map} of Test Parameters
	 */
	public void setTestParameters(Map<String, String> testParameters) {
		getTestParameters().putAll(testParameters);
		_logger.info("Setting test parameters");
	}

	// Driver Manager -----------------------------------------------

	private DriverManager driverManager;

	/**
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
	public void setTestName(String testName) {
		if (!testName.trim().isEmpty())
			this.testName = testName;
		else
			_logger.warn(String.format(" %sBlank testName parameter provided. Will use default test name",
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
	public void setTimeoutInSeconds(String timeoutInSeconds) {
		try {
			this.timeoutInSeconds = Long.parseLong(timeoutInSeconds);
		} catch (Exception e) {
			_logger.warn(String.format("%s timeoutInSeconds must be a number. Given %s. Default value will be used",
					ErrorCode.INVALID_PARAMETER_VALUE.name(), timeoutInSeconds));
		}
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
	public void setSlowdownDurationInSeconds(String slowdownDurationInSeconds) {
		try {
			this.slowdownDurationInSeconds = Long.parseLong(slowdownDurationInSeconds);
		} catch (Exception e) {
			_logger.warn(
					String.format("%s slowdownDurationInSeconds must be a number. Given %s, Default value will be used",
							ErrorCode.INVALID_PARAMETER_VALUE.name(), timeoutInSeconds));
		}
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
	public void setRepeatMode(String repeatMode) {
		this.repeatMode = StringUtils.getEnumFromString(RepeatMode.class, repeatMode);
		if (this.repeatMode == null) {
			_logger.warn(String.format("%s for repeatMode. Expected one of %s. Got %s. Default value will be set",
					ErrorCode.INVALID_PARAMETER_VALUE.name(), RepeatMode.values(), repeatMode));
			this.repeatMode = RepeatMode.getDefault();
		}
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
	public void setTestDurationInSeconds(String testDurationInSeconds) {
		try {
			this.testDurationInSeconds = Long.parseLong(testDurationInSeconds);
		} catch (Exception e) {
			_logger.warn(
					String.format("%s testDurationInSeconds must be a number. Given %s. Default value will be used",
							ErrorCode.INVALID_PARAMETER_VALUE.name(), testDurationInSeconds));
		}
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
	public void setInvocationCount(String invocationCount) {
		try {
			this.invocationCount = Long.parseLong(invocationCount);
		} catch (Exception e) {
			_logger.warn(String.format("%s invocationCount must be a number. Given %s. Default value will be used",
					ErrorCode.INVALID_PARAMETER_VALUE.name(), invocationCount));
		}
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
	public void setDelayBetweenIterationInSeconds(String delayBetweenIterationInSeconds) {
		try {
			this.delayBetweenIterationInSeconds = Long.parseLong(delayBetweenIterationInSeconds);
		} catch (Exception e) {
			_logger.warn(String.format(
					"%s delayBetweenIterationInSeconds must be a number. Given %s. Default value will be used",
					ErrorCode.INVALID_PARAMETER_VALUE.name(), delayBetweenIterationInSeconds));
		}
		_logger.info(String.format("delayBetweenIterationInSeconds set to %s", getDelayBetweenIterationInSeconds()));
	}

	// Screenshot ---------------------------------------------------

	private ScreenshotType screenshotType;
	private List<ScreenshotMode> screenshotModes = new ArrayList<>();
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
	public void setScreenshotType(String screenshotType) {
		this.screenshotType = StringUtils.getEnumFromString(ScreenshotType.class, screenshotType);
		if (this.screenshotType == null) {
			_logger.warn(String.format("%s for screenshotType. Expected one of %s. Got %s. Default value will be set",
					ErrorCode.INVALID_PARAMETER_VALUE.name(), ScreenshotType.values(), screenshotType));
			this.screenshotType = ScreenshotType.getDefault();
		}
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
		String screenshotModeArray[] = screenshotModes.split(",");
		for (String screenshotModeString : screenshotModeArray) {
			ScreenshotMode screenshotMode = StringUtils.getEnumFromString(ScreenshotMode.class, screenshotModeString);
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
	public void setScreenshotModeForIteration(String screenshotModeForIteration) {
		this.screenshotModeForIteration = StringUtils.getEnumFromString(ScreenshotModeForIteration.class,
				screenshotModeForIteration);
		if (this.screenshotModeForIteration == null) {
			_logger.warn(String.format(
					"%s for screenshotModeForIteration. Expected one of %s. Got %s. Default value will be set",
					ErrorCode.INVALID_PARAMETER_VALUE.name(), ScreenshotModeForIteration.values(),
					screenshotModeForIteration));
			this.screenshotModeForIteration = ScreenshotModeForIteration.getDefault();
		}
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
	public void setRetryMode(String retryMode) {
		this.retryMode = StringUtils.getEnumFromString(RetryMode.class, retryMode);
		if (this.retryMode == null) {
			_logger.warn(String.format("%s for retryMode. Expected one of %s. Got %s. Default value will be set",
					ErrorCode.INVALID_PARAMETER_VALUE.name(), RetryMode.values(), retryMode));
			this.retryMode = RetryMode.getDefault();
		}
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
