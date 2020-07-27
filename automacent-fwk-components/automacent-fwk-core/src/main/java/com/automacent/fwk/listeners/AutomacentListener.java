package com.automacent.fwk.listeners;

import org.testng.IExecutionListener;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener2;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.TestNGException;
import org.testng.annotations.BeforeTest;

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

/**
 * This is the custom reporter for Automacent framework and should be specified
 * in the testNG listener. It contains methods for logging information on after
 * test completion (failure/pass)
 * 
 * @author sighil.sivadas
 *
 */
public class AutomacentListener extends TestListenerAdapter implements IInvokedMethodListener2, IExecutionListener {

	private static final Logger _logger = Logger.getLogger(AutomacentListener.class);

	/**
	 * Override method for onStart where we start the {@link IterationManager} class
	 * to track the iteration and time.
	 */
	@Override
	public void onStart(ITestContext testContext) {
		_logger.debug("Starting timekeeper " + IterationManager.getManager().getElapsedTimeInMilliSeconds());
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

	public void afterInvocation(IInvokedMethod invokedMethod, ITestResult testResult) {

	}

	public void beforeInvocation(IInvokedMethod invokedMethod, ITestResult testResult) {

	}

	/**
	 * Implement afterInvocation method in the {@link IInvokedMethodListener2} to
	 * listen for the {@link BeforeTest} method automacentInternalSetParameters so
	 * that start test can be logged to the Launcher
	 */
	public void afterInvocation(IInvokedMethod invokedMethod, ITestResult testResult, ITestContext testContext) {
		String methodName = invokedMethod.getTestMethod().getMethodName();
		if (invokedMethod.isConfigurationMethod() && methodName.equals("automacentInternalSetParameters")) {
			LauncherClientManager.getManager().enableClient();
			LauncherClientManager.getManager().startTest(BaseTest.getTestObject(), invokedMethod, testResult,
					testContext);
		}
	}

	public void beforeInvocation(IInvokedMethod invokedMethod, ITestResult testResult, ITestContext testContext) {
		String methodName = invokedMethod.getTestMethod().getMethodName();
		if (!methodName.startsWith("automacentInternal")
				&& BaseTest.getTestObject().getDriverManager().getActiveDriver() != null)
			StepsAndPagesProcessor.processAnnotation(invokedMethod.getTestMethod().getInstance());
	}

	/**
	 * Delete temp folder before testng execution start
	 */
	@Override
	public void onExecutionStart() {
		FileUtils.cleanTempDirectory();
	}

	/**
	 * Delete temp folder after testng execution complete
	 */
	@Override
	public void onExecutionFinish() {
		FileUtils.cleanTempDirectory();
	}
}
