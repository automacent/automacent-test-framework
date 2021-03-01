package com.automacent.fwk.core;

import java.util.HashMap;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.automacent.fwk.annotations.Action;
import com.automacent.fwk.enums.RepeatMode;
import com.automacent.fwk.enums.RetryMode;
import com.automacent.fwk.launcher.LauncherClientManager;
import com.automacent.fwk.recovery.RecoveryManager;
import com.automacent.fwk.reporting.Logger;
import com.automacent.fwk.utils.ThreadUtils;

/**
 * All Web services and Unit test classes must inherit this class. Class
 * contains essential setup code for the framework to work
 * 
 * @author sighil.sivadas
 */
public abstract class BaseTest {

	private static final Logger _logger = Logger.getLogger(BaseTest.class);

	private static Map<Long, TestObject> testObjectMap = new HashMap<>();

	/**
	 * Get the {@link TestObject} for the current test. If {@link TestObject} is not
	 * initiated for current Test, a new {@link TestObject}is created
	 * 
	 * @return {@link TestObject}
	 */
	public synchronized static TestObject getTestObject() {
		long threadId = ThreadUtils.getThreadId();
		if (!testObjectMap.containsKey(threadId)) {
			_logger.info(String.format("Constructing Test Object for threadId %s", threadId));
			testObjectMap.put(threadId, new TestObject());
		}
		return testObjectMap.get(threadId);
	}

	/**
	 * Get the test parameter defined by {@link Parameters} or added to the test
	 * parameter dynamically by calling
	 * {@link BaseTest#addTestParameter(String, String)} or
	 * {@link BaseTest#appendTestParameter(String, String)}
	 * 
	 * @param key Name of the parameter
	 * @return Parameter value
	 */
	protected String getTestParameter(String key) {
		return BaseTest.getTestObject().getTestParameter(key);
	}

	/**
	 * Add test parameter. If the parameter with key already exists, the value is
	 * overwritten
	 * 
	 * @param key   Name of the parameter
	 * @param value Value of the parameter
	 */
	protected void addTestParameter(String key, String value) {
		BaseTest.getTestObject().addTestParameter(key, value);
	}

	/**
	 * Append test parameter. this method will get the test parameter with the given
	 * key and append the provided value as comma seperated String
	 * 
	 * @param key   Name of the parameter
	 * @param value Value of the parameter
	 */
	protected void appendTestParameter(String key, String value) {
		BaseTest.getTestObject().appendTestParameter(key, value);
	}

	/**
	 * Set up launcher clients for framework.Launcher clients are REST based
	 * services which can update test run result to a custom dashboard. These
	 * parameters are set for the whole Test suite
	 * 
	 * @param launcherClients Comma seperated list of fully qualified launcher
	 *                        client class names
	 * @param runName         Run Name in the logger application
	 * @param batchNumber     Batch number in the logger application
	 */
	@BeforeSuite
	@Parameters({ "launcherClients", "runName", "batchNumber" })
	public void automacentInternalSetLauncherClients(@Optional("") String launcherClients, @Optional("") String runName,
			@Optional("") String batchNumber) {
		LauncherClientManager.getManager().generateLauncherClientMasterMap(launcherClients);
	}

	/**
	 * Set up common parameters required by the framework. These parameters will be
	 * set at Test Level. Each test can provide their custom set of parameters
	 * 
	 * @param repeatMode                     {@link RepeatMode}
	 * @param testDurationInSeconds          Duration for which the tests should run
	 *                                       in case {@link RepeatMode} ==
	 *                                       {@code TEST_DURATION}
	 * @param invocationCount                Number of times the tests should run in
	 *                                       case {@link RepeatMode} ==
	 *                                       {@code INVOCATION_COUNT}
	 * @param delayBetweenIterationInSeconds Delay between each repeat in case
	 *                                       {@link RepeatMode} ==
	 *                                       {@code INVOCATION_COUNT} ||
	 *                                       {@code TEST_DURATION}
	 * @param timeoutInSeconds               Wait time before exception is thrown
	 * @param slowdownDurationInSeconds      Wait between subsequent {@link Action}
	 *                                       methods
	 * @param retryMode                      {@link RetryMode}
	 * @param recoveryClasses                Comma seperated list of fully qualified
	 *                                       recovery class names in case the
	 *                                       {@code RetryMode} == ${code ON}
	 * @param testContext                    testNg {@link ITestContext}
	 */
	@BeforeTest
	@BeforeClass
	@Parameters({ "repeatMode", "testDurationInSeconds", "invocationCount", "delayBetweenIterationInSeconds",
			"timeoutInSeconds", "slowdownDurationInSeconds", "retryMode", "recoveryClasses" })
	public void automacentInternalSetParameters(
			@Optional("OFF") String repeatMode,
			@Optional("0") String testDurationInSeconds,
			@Optional("0") String invocationCount,
			@Optional("0") String delayBetweenIterationInSeconds,
			@Optional("20") String timeoutInSeconds,
			@Optional("1") String slowdownDurationInSeconds,
			@Optional("OFF") String retryMode,
			@Optional("") String recoveryClasses,
			ITestContext testContext) {
		System.setProperty("org.uncommons.reportng.escape-output", "false");

		TestObject testObject = BaseTest.getTestObject();
		testObject.setTestContext(testContext);
		testObject.setRepeatMode(repeatMode);
		testObject.setTestDurationInSeconds(testDurationInSeconds);
		testObject.setInvocationCount(invocationCount);
		testObject.setDelayBetweenIterationInSeconds(delayBetweenIterationInSeconds);
		testObject.setTimeoutInSeconds(timeoutInSeconds);
		testObject.setSlowdownDurationInSeconds(slowdownDurationInSeconds);
		testObject.setRetryMode(retryMode);
		testObject.setRecoveryManager(new RecoveryManager(recoveryClasses));
	}
}
