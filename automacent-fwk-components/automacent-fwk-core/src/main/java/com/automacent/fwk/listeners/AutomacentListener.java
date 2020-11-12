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
		String runName = parameters.get("runName");
		String batchNumber = parameters.get("batchNumber");
		String retryMode = parameters.get("retryMode");
		String recoveryClasses = parameters.get("recoveryClasses");
		String timeoutInSeconds = parameters.get("timeoutInSeconds");
		String slowdownDurationInSeconds = parameters.get("slowdownDurationInSeconds");
		String repeatMode = parameters.get("repeatMode");
		String testDurationInSeconds = parameters.get("testDurationInSeconds");
		String invocationCount = parameters.get("invocationCount");
		String delayBetweenIterationInSeconds = parameters.get("delayBetweenIterationInSeconds");
		String ieDriverLocation = parameters.get("ieDriverLocation");
		String chromeDriverLocation = parameters.get("chromeDriverLocation");
		String geckoDriverLocation = parameters.get("geckoDriverLocation");
		String scriptTimeoutInSeconds = parameters.get("scriptTimeoutInSeconds");
		String pageLoadTimeoutInSeconds = parameters.get("pageLoadTimeoutInSeconds");
		String socketTimeoutInSeconds = parameters.get("socketTimeoutInSeconds");
		String browser = parameters.get("browser");
		String baseUrl = parameters.get("baseUrl");
		String screenshotType = parameters.get("screenshotType");
		String screenshotMode = parameters.get("screenshotMode");
		String screenshotModeForIteration = parameters.get("screenshotModeForIteration");

		launcherClients = launcherClients == null || launcherClients.isEmpty()
				? System.getProperty("automacent.launcherClients", "")
				: launcherClients;
		runName = runName == null || runName.isEmpty() ? System.getProperty("automacent.runName", "") : runName;
		batchNumber = batchNumber == null || batchNumber.isEmpty() ? System.getProperty("automacent.batchNumber", "")
				: batchNumber;
		retryMode = retryMode == null || retryMode.isEmpty() ? System.getProperty("automacent.retryMode", "OFF")
				: retryMode;
		recoveryClasses = recoveryClasses == null || recoveryClasses.isEmpty()
				? System.getProperty("automacent.recoveryClasses", "")
				: recoveryClasses;
		timeoutInSeconds = timeoutInSeconds == null || timeoutInSeconds.isEmpty()
				? System.getProperty("automacent.timeoutInSeconds", "20")
				: timeoutInSeconds;
		slowdownDurationInSeconds = slowdownDurationInSeconds == null || slowdownDurationInSeconds.isEmpty()
				? System.getProperty("automacent.slowdownDurationInSeconds", "1")
				: slowdownDurationInSeconds;
		repeatMode = repeatMode == null || repeatMode.isEmpty() ? System.getProperty("automacent.repeatMode", "OFF")
				: repeatMode;
		testDurationInSeconds = testDurationInSeconds == null || testDurationInSeconds.isEmpty()
				? System.getProperty("automacent.testDurationInSeconds", "0")
				: testDurationInSeconds;
		invocationCount = invocationCount == null || invocationCount.isEmpty()
				? System.getProperty("automacent.invocationCount", "0")
				: invocationCount;
		delayBetweenIterationInSeconds = delayBetweenIterationInSeconds == null
				|| delayBetweenIterationInSeconds.isEmpty()
						? System.getProperty("automacent.delayBetweenIterationInSeconds", "0")
						: delayBetweenIterationInSeconds;
		ieDriverLocation = ieDriverLocation == null || ieDriverLocation.isEmpty()
				? System.getProperty("automacent.ieDriverLocation", "")
				: ieDriverLocation;
		chromeDriverLocation = chromeDriverLocation == null || chromeDriverLocation.isEmpty()
				? System.getProperty("automacent.chromeDriverLocation", "")
				: chromeDriverLocation;
		geckoDriverLocation = geckoDriverLocation == null || geckoDriverLocation.isEmpty()
				? System.getProperty("automacent.geckoDriverLocation", "")
				: geckoDriverLocation;
		scriptTimeoutInSeconds = scriptTimeoutInSeconds == null || scriptTimeoutInSeconds.isEmpty()
				? System.getProperty("automacent.scriptTimeoutInSeconds", "300")
				: scriptTimeoutInSeconds;
		pageLoadTimeoutInSeconds = pageLoadTimeoutInSeconds == null || pageLoadTimeoutInSeconds.isEmpty()
				? System.getProperty("automacent.pageLoadTimeoutInSeconds", "300")
				: pageLoadTimeoutInSeconds;
		socketTimeoutInSeconds = socketTimeoutInSeconds == null || socketTimeoutInSeconds.isEmpty()
				? System.getProperty("automacent.socketTimeoutInSeconds", "300")
				: socketTimeoutInSeconds;
		browser = browser == null || browser.isEmpty() ? System.getProperty("automacent.browser", "CHROME") : browser;
		baseUrl = baseUrl == null || baseUrl.isEmpty() ? System.getProperty("automacent.baseUrl") : baseUrl;
		screenshotType = screenshotType == null || screenshotType.isEmpty()
				? System.getProperty("automacent.screenshotType", "BROWSER_SCREENSHOT")
				: screenshotType;
		screenshotMode = screenshotMode == null || screenshotMode.isEmpty()
				? System.getProperty("automacent.screenshotMode", "ON_FAILURE")
				: screenshotMode;
		screenshotModeForIteration = screenshotModeForIteration == null || screenshotModeForIteration.isEmpty()
				? System.getProperty("automacent.screenshotModeForIteration", "LAST_ITERATION")
				: screenshotModeForIteration;

		parameters.put("launcherClients", launcherClients);
		parameters.put("runName", runName);
		parameters.put("batchNumber", batchNumber);
		parameters.put("retryMode", retryMode);
		parameters.put("recoveryClasses", recoveryClasses);
		parameters.put("timeoutInSeconds", timeoutInSeconds);
		parameters.put("slowdownDurationInSeconds", slowdownDurationInSeconds);
		parameters.put("repeatMode", repeatMode);
		parameters.put("testDurationInSeconds", testDurationInSeconds);
		parameters.put("invocationCount", invocationCount);
		parameters.put("delayBetweenIterationInSeconds", delayBetweenIterationInSeconds);
		parameters.put("ieDriverLocation", ieDriverLocation);
		parameters.put("chromeDriverLocation", chromeDriverLocation);
		parameters.put("geckoDriverLocation", geckoDriverLocation);
		parameters.put("scriptTimeoutInSeconds", scriptTimeoutInSeconds);
		parameters.put("pageLoadTimeoutInSeconds", pageLoadTimeoutInSeconds);
		parameters.put("socketTimeoutInSeconds", socketTimeoutInSeconds);
		parameters.put("browser", browser);
		parameters.put("baseUrl", baseUrl);
		parameters.put("screenshotType", screenshotType);
		parameters.put("screenshotMode", screenshotMode);
		parameters.put("screenshotModeForIteration", screenshotModeForIteration);

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
