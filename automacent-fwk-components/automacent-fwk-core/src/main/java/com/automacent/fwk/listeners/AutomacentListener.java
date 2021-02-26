package com.automacent.fwk.listeners;

import java.util.List;
import java.util.Map;

import org.testng.IExecutionListener;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.TestNGException;

import com.automacent.fwk.annotations.StepsAndPagesProcessor;
import com.automacent.fwk.core.BaseTest;
import com.automacent.fwk.enums.ScreenshotModeForIteration;
import com.automacent.fwk.exceptions.TestOrConfigurationSkipException;
import com.automacent.fwk.execution.IterationManager;
import com.automacent.fwk.launcher.LauncherClientManager;
import com.automacent.fwk.reporting.ExecutionLogManager;
import com.automacent.fwk.reporting.Logger;
import com.automacent.fwk.reporting.ReportingTools;
import com.automacent.fwk.utils.FileUtils;
import com.automacent.fwk.utils.ThreadUtils;

/**
 * This is the custom reporter for Automacent framework and should be specified
 * in the testNG listener. It contains methods for logging information on after
 * test completion (failure/pass)
 * 
 * @author sighil.sivadas
 *
 */
public class AutomacentListener extends TestListenerAdapter
		implements IInvokedMethodListener, IExecutionListener, IMethodInterceptor, ISuiteListener {

	private static final Logger _logger = Logger.getLogger(AutomacentListener.class);

	/**
	 * Override method for onStart where we start the {@link IterationManager} class
	 * to track the iteration and time.
	 */
	@Override
	public void onStart(ITestContext testContext) {
		_logger.info("----------------- Starting XML Test -------------------");
		_logger.info(String.format("XML Test    : %s", testContext.getCurrentXmlTest().getName()));
		_logger.info(String.format("Thread Name : %s", ThreadUtils.getThreadName()));
		_logger.info(String.format("Thread ID : %s", ThreadUtils.getThreadId()));
		_logger.info("-------------------------------------------------------");
		_logger.debug("Starting timekeeper " + IterationManager.getManager().getElapsedTimeInMilliSeconds());
		LauncherClientManager.getManager().enableClient();
		LauncherClientManager.getManager().startTest(testContext);
		super.onStart(testContext);
	}

	/**
	 * Override {@link ISuiteListener#onStart(ISuite)} method for setting framework
	 * parameters. If not set, apply default values
	 */
	@Override
	public void onStart(ISuite suite) {
		ISuiteListener.super.onStart(suite);
		Map<String, String> parameters = suite.getXmlSuite().getAllParameters();

		String launcherClients = parameters.get("launcherClients");
		launcherClients = launcherClients == null || launcherClients.isEmpty()
				? System.getProperty("automacent.launcherClients", "")
				: launcherClients;
		parameters.put("launcherClients", launcherClients);

		String runName = parameters.get("runName");
		runName = runName == null || runName.isEmpty() ? System.getProperty("automacent.runName", "") : runName;
		parameters.put("runName", runName);

		String batchNumber = parameters.get("batchNumber");
		batchNumber = batchNumber == null || batchNumber.isEmpty() ? System.getProperty("automacent.batchNumber", "")
				: batchNumber;
		parameters.put("batchNumber", batchNumber);

		String retryMode = parameters.get("retryMode");
		retryMode = retryMode == null || retryMode.isEmpty() ? System.getProperty("automacent.retryMode", "OFF")
				: retryMode;
		parameters.put("retryMode", retryMode);

		String recoveryClasses = parameters.get("recoveryClasses");
		recoveryClasses = recoveryClasses == null || recoveryClasses.isEmpty()
				? System.getProperty("automacent.recoveryClasses", "")
				: recoveryClasses;
		parameters.put("recoveryClasses", recoveryClasses);

		String timeoutInSeconds = parameters.get("timeoutInSeconds");
		timeoutInSeconds = timeoutInSeconds == null || timeoutInSeconds.isEmpty()
				? System.getProperty("automacent.timeoutInSeconds", "20")
				: timeoutInSeconds;
		parameters.put("timeoutInSeconds", timeoutInSeconds);

		String slowdownDurationInSeconds = parameters.get("slowdownDurationInSeconds");
		slowdownDurationInSeconds = slowdownDurationInSeconds == null || slowdownDurationInSeconds.isEmpty()
				? System.getProperty("automacent.slowdownDurationInSeconds", "1")
				: slowdownDurationInSeconds;
		parameters.put("slowdownDurationInSeconds", slowdownDurationInSeconds);

		String repeatMode = parameters.get("repeatMode");
		repeatMode = repeatMode == null || repeatMode.isEmpty() ? System.getProperty("automacent.repeatMode", "OFF")
				: repeatMode;
		parameters.put("repeatMode", repeatMode);

		String testDurationInSeconds = parameters.get("testDurationInSeconds");
		testDurationInSeconds = testDurationInSeconds == null || testDurationInSeconds.isEmpty()
				? System.getProperty("automacent.testDurationInSeconds", "0")
				: testDurationInSeconds;
		parameters.put("testDurationInSeconds", testDurationInSeconds);

		String invocationCount = parameters.get("invocationCount");
		invocationCount = invocationCount == null || invocationCount.isEmpty()
				? System.getProperty("automacent.invocationCount", "0")
				: invocationCount;
		parameters.put("invocationCount", invocationCount);

		String delayBetweenIterationInSeconds = parameters.get("delayBetweenIterationInSeconds");
		delayBetweenIterationInSeconds = delayBetweenIterationInSeconds == null
				|| delayBetweenIterationInSeconds.isEmpty()
						? System.getProperty("automacent.delayBetweenIterationInSeconds", "0")
						: delayBetweenIterationInSeconds;
		parameters.put("delayBetweenIterationInSeconds", delayBetweenIterationInSeconds);

		String ieDriverLocation = parameters.get("ieDriverLocation");
		ieDriverLocation = ieDriverLocation == null || ieDriverLocation.isEmpty()
				? System.getProperty("automacent.ieDriverLocation", "")
				: ieDriverLocation;
		parameters.put("ieDriverLocation", ieDriverLocation);

		String chromeDriverLocation = parameters.get("chromeDriverLocation");
		chromeDriverLocation = chromeDriverLocation == null || chromeDriverLocation.isEmpty()
				? System.getProperty("automacent.chromeDriverLocation", "")
				: chromeDriverLocation;
		parameters.put("chromeDriverLocation", chromeDriverLocation);

		String geckoDriverLocation = parameters.get("geckoDriverLocation");
		geckoDriverLocation = geckoDriverLocation == null || geckoDriverLocation.isEmpty()
				? System.getProperty("automacent.geckoDriverLocation", "")
				: geckoDriverLocation;
		parameters.put("geckoDriverLocation", geckoDriverLocation);

		String scriptTimeoutInSeconds = parameters.get("scriptTimeoutInSeconds");
		scriptTimeoutInSeconds = scriptTimeoutInSeconds == null || scriptTimeoutInSeconds.isEmpty()
				? System.getProperty("automacent.scriptTimeoutInSeconds", "300")
				: scriptTimeoutInSeconds;
		parameters.put("scriptTimeoutInSeconds", scriptTimeoutInSeconds);

		String pageLoadTimeoutInSeconds = parameters.get("pageLoadTimeoutInSeconds");
		pageLoadTimeoutInSeconds = pageLoadTimeoutInSeconds == null || pageLoadTimeoutInSeconds.isEmpty()
				? System.getProperty("automacent.pageLoadTimeoutInSeconds", "300")
				: pageLoadTimeoutInSeconds;
		parameters.put("pageLoadTimeoutInSeconds", pageLoadTimeoutInSeconds);

		String socketTimeoutInSeconds = parameters.get("socketTimeoutInSeconds");
		socketTimeoutInSeconds = socketTimeoutInSeconds == null || socketTimeoutInSeconds.isEmpty()
				? System.getProperty("automacent.socketTimeoutInSeconds", "300")
				: socketTimeoutInSeconds;
		parameters.put("socketTimeoutInSeconds", socketTimeoutInSeconds);

		String browser = parameters.get("browser");
		browser = browser == null || browser.isEmpty() ? System.getProperty("automacent.browser", "CHROME") : browser;
		parameters.put("browser", browser);

		String baseUrl = parameters.get("baseUrl");
		baseUrl = baseUrl == null || baseUrl.isEmpty() ? System.getProperty("automacent.baseUrl", "") : baseUrl;
		parameters.put("baseUrl", baseUrl);

		String screenshotType = parameters.get("screenshotType");
		screenshotType = screenshotType == null || screenshotType.isEmpty()
				? System.getProperty("automacent.screenshotType", "BROWSER_SCREENSHOT")
				: screenshotType;
		parameters.put("screenshotType", screenshotType);

		String screenshotMode = parameters.get("screenshotMode");
		screenshotMode = screenshotMode == null || screenshotMode.isEmpty()
				? System.getProperty("automacent.screenshotMode", "ON_FAILURE")
				: screenshotMode;
		parameters.put("screenshotMode", screenshotMode);

		String screenshotModeForIteration = parameters.get("screenshotModeForIteration");
		screenshotModeForIteration = screenshotModeForIteration == null || screenshotModeForIteration.isEmpty()
				? System.getProperty("automacent.screenshotModeForIteration", "LAST_ITERATION")
				: screenshotModeForIteration;
		parameters.put("screenshotModeForIteration", screenshotModeForIteration);

		_logger.info("Setup default framework parameters completed");

		suite.getXmlSuite().setParameters(parameters);
	}

	/**
	 * Override method for onTestFailure in the TestNG library. Override is done to
	 * log test skips and test failure
	 */
	@Override
	public void onTestFailure(ITestResult testResult) {
		Throwable throwable = testResult.getThrowable() == null ? new TestOrConfigurationSkipException()
				: testResult.getThrowable();
		if (throwable instanceof TestNGException)
			ExecutionLogManager.logTestSkip(testResult);
		else
			ExecutionLogManager.logListenerFailure(testResult);
		super.onTestFailure(testResult);
	}

	/**
	 * Override method for onTestSkipped in the TestNG library. Override is done to
	 * log test skips
	 */
	@Override
	public void onTestSkipped(ITestResult testResult) {
		Throwable throwable = testResult.getThrowable() == null ? new TestOrConfigurationSkipException()
				: testResult.getThrowable();
		testResult.setThrowable(throwable);
		ExecutionLogManager.logTestSkip(testResult);
		super.onTestSkipped(testResult);
	}

	/**
	 * Override method for onConfigurationSkip in the TestNG library. Override is
	 * done to log proper failure
	 */
	@Override
	public void onConfigurationSkip(ITestResult testResult) {
		Throwable throwable = testResult.getThrowable() == null ? new TestOrConfigurationSkipException()
				: testResult.getThrowable();
		testResult.setThrowable(throwable);
		ExecutionLogManager.logTestSkip(testResult);
		super.onConfigurationSkip(testResult);
	}

	/**
	 * Override method for onFinish in the TestNG library. Override is done to log
	 * the iteration details and screenshot management according to the set
	 * {@link ScreenshotModeForIteration} parameter.
	 */
	@Override
	public void onFinish(ITestContext testContext) {
		ExecutionLogManager.logIterationDetails();
		ReportingTools.wipeScreenshotEntryInReports();
		LauncherClientManager.getManager().stopTest();
		super.onFinish(testContext);
	}

	@Override
	public void afterInvocation(IInvokedMethod invokedMethod, ITestResult testResult) {
	}

	@Override
	public void beforeInvocation(IInvokedMethod invokedMethod, ITestResult testResult) {

	}

	@Override
	public void afterInvocation(IInvokedMethod invokedMethod, ITestResult testResult, ITestContext testContext) {
	}

	/**
	 * Implement beforeInvocation method in the {@link IInvokedMethodListener} to
	 * listen for @Before and @After methods that are not part of the internal
	 * framework and initialize {@link StepsAndPagesProcessor}
	 */
	@Override
	public void beforeInvocation(IInvokedMethod invokedMethod, ITestResult testResult, ITestContext testContext) {
		String methodName = invokedMethod.getTestMethod().getMethodName();
		if (!methodName.startsWith("automacentInternal")
				&& BaseTest.getTestObject().getDriverManager().getActiveDriver() != null)
			StepsAndPagesProcessor.processAnnotation(invokedMethod.getTestMethod().getInstance());
	}

	/**
	 * Delete temporary folder before testNG execution start
	 */
	@Override
	public void onExecutionStart() {
		FileUtils.cleanTempDirectory();
	}

	/**
	 * Delete temporary folder after testNG execution complete
	 */
	@Override
	public void onExecutionFinish() {
		FileUtils.cleanTempDirectory();
	}

	/**
	 * Implement {@link IMethodInterceptor#intercept(List, ITestContext)} method to
	 * fix the sequencing Test cases in test classes specified within the
	 * &lt;TEST&gt; tag in TestNG XML file
	 */
	@Override
	public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
		_logger.info("------------------ Execution Order --------------------");
		_logger.info(String.format("XML Test  : %s", context.getCurrentXmlTest().getName()));
		for (IMethodInstance method : methods) {
			_logger.info(String.format("@Test     : %s", method.getMethod().getQualifiedName()));
		}
		_logger.info("-------------------------------------------------------");
		return methods;
	}
}
