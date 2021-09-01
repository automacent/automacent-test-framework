package com.automacent.fwk.reporting;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.Test;

import com.automacent.fwk.annotations.Action;
import com.automacent.fwk.annotations.Repeat;
import com.automacent.fwk.annotations.Step;
import com.automacent.fwk.core.BaseTest;
import com.automacent.fwk.enums.MethodType;
import com.automacent.fwk.enums.RepeatMode;
import com.automacent.fwk.enums.ScreenshotMode;
import com.automacent.fwk.enums.TestStatus;
import com.automacent.fwk.execution.ExceptionManager;
import com.automacent.fwk.execution.IterationManager;
import com.automacent.fwk.launcher.LauncherClientManager;
import com.automacent.fwk.listeners.AutomacentListener;
import com.automacent.fwk.utils.AspectJUtils;
import com.automacent.fwk.utils.LoggingUtils;

/**
 * Class which handles logging for {@link Action}, {@link Step}, {@link Test},
 * {@link Repeat} Before* and After* testNG methods and Iterations. This
 * includes logging to File as well as Launcher
 * 
 * @author sighil.sivadas
 *
 */
public class ExecutionLogManager {

	private static final Logger _logger = Logger.getLogger(ExecutionLogManager.class);

	/**
	 * Log start of Step/Action method
	 * 
	 * @param point      {@link ProceedingJoinPoint}
	 * @param methodType {@link MethodType}
	 */
	public static void logMethodStart(ProceedingJoinPoint point, MethodType methodType) {
		String methodName = MethodSignature.class.cast(point.getSignature()).getMethod().getName();
		Logger.getLogger(MethodSignature.class.cast(point.getSignature()).getDeclaringType())
				.info(String.format("%s%s", LoggingUtils.addGrammer(LoggingUtils.addSpaceToCamelCaseString(methodName)),
						AspectJUtils.getArguments(point)));
		LauncherClientManager.getManager()
				.logStart(String.format("%s%s",
						LoggingUtils.addGrammer(LoggingUtils.addSpaceToCamelCaseString(methodName)),
						AspectJUtils.getArguments(point)), methodType);
	}

	/**
	 * 
	 * Log end of Step/Action method
	 * 
	 * @param point      {@link ProceedingJoinPoint}
	 * @param methodType {@link MethodType}
	 * @param testStatus {@link TestStatus}
	 * @param duration   Execution duration for method in milliseconds
	 * @param result     {@link ProceedingJoinPoint execution result}
	 * @param t          {@link Throwable}
	 */
	public static void logMethodEnd(ProceedingJoinPoint point, MethodType methodType, TestStatus testStatus,
			long duration, Object result, Throwable t) {
		String methodName = MethodSignature.class.cast(point.getSignature()).getMethod().getName();
		if (methodName.startsWith("is"))
			Logger.getLogger(MethodSignature.class.cast(point.getSignature()).getDeclaringType())
					.info(String.format("%s %s",
							LoggingUtils.addGrammer(LoggingUtils.addSpaceToCamelCaseString(methodName)),
							result != null ? result.toString() : ""));
		LauncherClientManager.getManager()
				.logEnd(String.format("%s%s",
						LoggingUtils.addGrammer(LoggingUtils.addSpaceToCamelCaseString(methodName)),
						AspectJUtils.getArguments(point)), methodType, testStatus, duration, t);
	}

	/**
	 * Log start of Test/Before/After TestNG methods
	 * 
	 * @param point      {@link ProceedingJoinPoint}
	 * @param methodType {@link MethodType}
	 */
	public static void logTestStart(ProceedingJoinPoint point, MethodType methodType) {
		String methodName = MethodSignature.class.cast(point.getSignature()).getMethod().getName();
		Logger.getLogger(MethodSignature.class.cast(point.getSignature()).getDeclaringType())
				.infoHeading(
						String.format("%s%s",
								LoggingUtils.addGrammer(LoggingUtils.addSpaceToCamelCaseString(methodName)),
								AspectJUtils.getArguments(point)));
		LauncherClientManager.getManager()
				.logStart(String.format("%s%s",
						LoggingUtils.addGrammer(LoggingUtils.addSpaceToCamelCaseString(methodName)),
						AspectJUtils.getArguments(point)), methodType);
	}

	/**
	 * Log successful completion of Test/Before/After TestNG methods
	 * 
	 * @param point      {@link ProceedingJoinPoint}
	 * @param methodType {@link MethodType}
	 * @param duration   Execution duration for method in milliseconds
	 */
	public static void logTestSuccess(ProceedingJoinPoint point, MethodType methodType, long duration) {
		String methodName = MethodSignature.class.cast(point.getSignature()).getMethod().getName();
		Logger.getLogger(MethodSignature.class.cast(point.getSignature()).getDeclaringType())
				.infoHeading(String.format("%s completed successfully",
						LoggingUtils.addSpaceToCamelCaseString(LoggingUtils.addGrammer(methodName))));
		LauncherClientManager.getManager()
				.logEnd(String.format("%s%s",
						LoggingUtils.addGrammer(LoggingUtils.addSpaceToCamelCaseString(methodName)),
						AspectJUtils.getArguments(point)), methodType, TestStatus.PASS, duration, null);
		if (methodType.equals(MethodType.TEST) && !BaseTest.getTestObject().getRepeatMode().equals(RepeatMode.OFF))
			return;
		LauncherClientManager.getManager().logSuccess(methodName, methodType, 0, duration);
	}

	/**
	 * Log failure of Test/Before/After TestNG methods
	 * 
	 * @param point      {@link ProceedingJoinPoint}
	 * @param methodType {@link MethodType}
	 * @param e          {@link Throwable}
	 * @param duration   Duration of Execution of method
	 */
	public static void logTestFailure(ProceedingJoinPoint point, MethodType methodType, Throwable e, long duration) {
		String methodName = MethodSignature.class.cast(point.getSignature()).getMethod().getName();
		Logger.getLogger(MethodSignature.class.cast(point.getSignature()).getDeclaringType()).error(
				String.format("%s failed", LoggingUtils.addSpaceToCamelCaseString(LoggingUtils.addGrammer(methodName))),
				e);

		if (BaseTest.getTestObject().getScreenshotModes().contains(ScreenshotMode.ON_FAILURE))
			if (ExceptionManager.isSocketTimeoutException(e))
				ReportingTools.logScreenGrabOnFailure("Test failed : " + e.getMessage());
			else
				ReportingTools.logScreenshotOnFailure("Test failed : " + e.getMessage());

		LauncherClientManager.getManager()
				.logEnd(String.format("%s%s",
						LoggingUtils.addGrammer(LoggingUtils.addSpaceToCamelCaseString(methodName)),
						AspectJUtils.getArguments(point)), methodType, TestStatus.FAIL, duration, e);

		if (methodType.equals(MethodType.TEST) && !BaseTest.getTestObject().getRepeatMode().equals(RepeatMode.OFF))
			return;
		LauncherClientManager.getManager().logFailure(methodName, methodType, 0, e, duration);
	}

	/**
	 * Log failure of Test/Before/After TestNG methods due to skipping of execution
	 * 
	 * @param testResult TestNG {@link ITestResult}
	 */
	public static void logTestSkip(ITestResult testResult) {
		ITestNGMethod testngMethod = testResult.getMethod();
		Throwable e = testResult.getThrowable();

		MethodType methodType = MethodType.TEST;
		if (testngMethod.isBeforeSuiteConfiguration() || testngMethod.isBeforeTestConfiguration()
				|| testngMethod.isBeforeClassConfiguration() || testngMethod.isBeforeMethodConfiguration()
				|| testngMethod.isBeforeGroupsConfiguration())
			methodType = MethodType.BEFORE;
		else if (testngMethod.isAfterMethodConfiguration() || testngMethod.isAfterClassConfiguration()
				|| testngMethod.isAfterTestConfiguration() || testngMethod.isAfterSuiteConfiguration()
				|| testngMethod.isAfterGroupsConfiguration())
			methodType = MethodType.AFTER;
		_logger.error(
				String.format("Execution skipped for %s method %s", methodType.name(), testngMethod.getMethodName()),
				e);

		LauncherClientManager.getManager().logFailure(testngMethod.getMethodName(), methodType, 0, e, 0);

		LauncherClientManager.getManager().logStart(testngMethod.getMethodName(), methodType);
		LauncherClientManager.getManager().logEnd(testngMethod.getMethodName(), methodType, TestStatus.SKIP, 0, e);
	}

	// Iteration logging --------------------------------------------

	/**
	 * Log start of iteration in case {@link Repeat} is true
	 * 
	 * @param iteration                  Iteration count
	 * @param elapsedTimeInMilliSeconds  Elapsed time since last iteration
	 * @param testDurationInMilliSeconds Set test duration
	 */
	public static void logIterationStart(long iteration, long elapsedTimeInMilliSeconds,
			long testDurationInMilliSeconds) {
		RepeatMode repeatMode = BaseTest.getTestObject().getRepeatMode();

		if (repeatMode.name().equals(RepeatMode.TEST_DURATION.name()))
			_logger.info(String.format("Starting Iteration : %s [%s/%s seconds elapsed]", iteration,
					TimeUnit.MILLISECONDS.toSeconds(elapsedTimeInMilliSeconds),
					TimeUnit.MILLISECONDS.toSeconds(testDurationInMilliSeconds)));
		else
			_logger.info(String.format("Starting Iteration : %s/%s", iteration,
					BaseTest.getTestObject().getInvocationCount()));

		LauncherClientManager.getManager().logStart(
				String.format("Iteration %s", IterationManager.getManager().getIteration()), MethodType.ITERATION);
	}

	/**
	 * Log successful completion of Iteration
	 * 
	 * @param point    {@link ProceedingJoinPoint}
	 * @param duration Execution duration for method in milliseconds
	 */
	public static void logIterationSuccess(ProceedingJoinPoint point, long duration) {
		String methodName = MethodSignature.class.cast(point.getSignature()).getMethod().getName();
		Logger.getLogger(MethodSignature.class.cast(point.getSignature()).getDeclaringType())
				.info(String.format("iteration %s for %s method %s succeeded",
						IterationManager.getManager().getIteration(), MethodType.TEST.name(), methodName));
		LauncherClientManager.getManager().logSuccess(methodName, MethodType.TEST,
				IterationManager.getManager().getIteration(), duration);

		LauncherClientManager.getManager().logEnd(
				String.format("Iteration %s", IterationManager.getManager().getIteration()), MethodType.ITERATION,
				TestStatus.PASS, duration, null);
	}

	/**
	 * Log failure of iteration
	 * 
	 * @param point    {@link ProceedingJoinPoint}
	 * @param e        {@link Throwable} failure reason
	 * @param duration Duration of Execution of iteration
	 */
	public static void logIterationFailure(ProceedingJoinPoint point, Throwable e, long duration) {
		String methodName = MethodSignature.class.cast(point.getSignature()).getMethod().getName();
		Logger.getLogger(MethodSignature.class.cast(point.getSignature()).getDeclaringType())
				.error(String.format("Execution failed for %s method %s, iteration %s", MethodType.TEST.name(),
						methodName, IterationManager.getManager().getIteration(), e.getMessage(), e.getStackTrace()),
						e);

		if (BaseTest.getTestObject().getScreenshotModes().contains(ScreenshotMode.ON_FAILURE)) {
			if (ExceptionManager.isSocketTimeoutException(e))
				ReportingTools.logScreenGrabOnFailure("Test failed : " + e.getMessage());
			else
				ReportingTools.logScreenshotOnFailure("Test failed : " + e.getMessage());
		}

		LauncherClientManager.getManager().logFailure(methodName, MethodType.TEST,
				IterationManager.getManager().getIteration(), e, duration);

		LauncherClientManager.getManager().logEnd(
				String.format("Iteration %s", IterationManager.getManager().getIteration()), MethodType.ITERATION,
				TestStatus.FAIL, duration, e);
	}

	/**
	 * Log end of iteration in case {@link Repeat} is true
	 * 
	 * @param iteration                  Iteration count
	 * @param elapsedTimeInMilliSeconds  Elapsed time since last iteration
	 * @param testDurationInMilliSeconds Set test duration
	 */
	public static void logIterationEnd(long iteration, long elapsedTimeInMilliSeconds,
			long testDurationInMilliSeconds) {
	}

	// ----------------------------------------------------

	/**
	 * Log the execution result of a TestNG {@link Test} method after all the
	 * iterations. This method will come to play when the {@link RepeatMode} is
	 * {@link RepeatMode#INVOCATION_COUNT} or {@link RepeatMode#TEST_DURATION} and
	 * is called from the {@link AutomacentListener}
	 * 
	 * @param testResult TestNG execution result
	 */
	public static void logListenerFailure(ITestResult testResult) {
		ITestNGMethod testngMethod = testResult.getMethod();
		Throwable e = testResult.getThrowable();
		_logger.error(String.format("Execution completed with errors for TEST method %s", testngMethod.getMethodName()),
				e);
	}

	/**
	 * Log Iteratin details after Test competion.
	 */
	public static void logIterationDetails() {
		if (BaseTest.getTestObject().getRepeatMode() != RepeatMode.OFF) {
			String message = "Total Iteration Run " + IterationManager.getManager().getIteration();

			if (BaseTest.getTestObject().getRepeatMode() == RepeatMode.TEST_DURATION)
				message += " [Elapsed Time: "
						+ TimeUnit.MILLISECONDS.toSeconds(IterationManager.getManager().getElapsedTimeInMilliSeconds())
						+ "/" + BaseTest.getTestObject().getTestDurationInSeconds() + " seconds]";
			else
				message += " of " + BaseTest.getTestObject().getInvocationCount();

			_logger.infoHeading(message);
		} else {
			_logger.infoHeading("No Iterations run");
		}
	}
}
