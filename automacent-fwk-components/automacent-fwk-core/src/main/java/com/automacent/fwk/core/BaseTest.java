package com.automacent.fwk.core;

import java.util.HashMap;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.automacent.fwk.annotations.Action;
import com.automacent.fwk.enums.RepeatMode;
import com.automacent.fwk.enums.RetryMode;
import com.automacent.fwk.launcher.LauncherClientManager;
import com.automacent.fwk.recovery.RecoveryManager;
import com.automacent.fwk.utils.ThreadUtils;

/**
 * All Web services and Unit test classes must inherit this class. Class
 * contains essential setup code for the framework to work
 * 
 * @author sighil.sivadas
 */
public abstract class BaseTest {

	private static Map<Long, TestObject> testObjectMap = new HashMap<>();

	/**
	 * Get the {@link TestObject} for the current test. If {@link TestObject} is not
	 * initiated for current Test, a new {@link TestObject}is created
	 * 
	 * @param threadId Current execution thread id
	 * 
	 * @return {@link TestObject}
	 */
	private static TestObject getTestObject(long threadId) {
		if (!testObjectMap.containsKey(threadId))
			testObjectMap.put(threadId, new TestObject());
		return testObjectMap.get(threadId);
	}

	/**
	 * 
	 * @return {@link TestObject}
	 */
	public static TestObject getTestObject() {
		return getTestObject(ThreadUtils.getThreadId());
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
		LauncherClientManager.getManager().addLauncherClientClasses(launcherClients);
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
		testObject.setTestName(testContext.getCurrentXmlTest().getName());
		testObject.setTestParameters(testContext.getCurrentXmlTest().getAllParameters());
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
