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
