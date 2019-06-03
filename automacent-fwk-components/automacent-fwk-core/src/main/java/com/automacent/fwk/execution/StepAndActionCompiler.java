package com.automacent.fwk.execution;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.automacent.fwk.annotations.Action;
import com.automacent.fwk.annotations.Step;
import com.automacent.fwk.core.BaseTest;
import com.automacent.fwk.enums.MethodType;
import com.automacent.fwk.enums.RepeatMode;
import com.automacent.fwk.enums.ScreenshotMode;
import com.automacent.fwk.exceptions.ActionExecutionException;
import com.automacent.fwk.exceptions.StepExecutionException;
import com.automacent.fwk.exceptions.TestDurationExceededException;
import com.automacent.fwk.reporting.ExecutionLogManager;
import com.automacent.fwk.reporting.ReportingTools;
import com.automacent.fwk.utils.AspectJUtils;
import com.automacent.fwk.utils.ThreadUtils;

/**
 * This class contains aspects for manipulating {@link Action} and {@link Step}
 * methods
 * 
 * @author sighil.sivadas
 *
 */
@Aspect
public class StepAndActionCompiler {

	/**
	 * Aspect for {@link Action} methods. Pre and post method execution logic is
	 * written here
	 * 
	 * @param point
	 *            {@link ProceedingJoinPoint} to get access to {@link Action} method
	 * @return Result Result of execution
	 */
	@Around("execution(* *(..)) && @annotation(com.automacent.fwk.annotations.Action)")
	public Object aroundActionCompilerAspect(ProceedingJoinPoint point) {
		long startTime = new Date().getTime();
		ExecutionLogManager.logMethodStart(point, MethodType.ACTION);
		String methodNameWithArguments = AspectJUtils.getMethodNameWithArguments(point);

		if (BaseTest.getTestObject().getScreenshotModes().contains(ScreenshotMode.BEFORE_ACTION))
			ReportingTools.takeScreenshot(ScreenshotMode.BEFORE_ACTION.name());

		Object result = null;
		try {
			ThreadUtils.sleepFor((int) BaseTest.getTestObject().getSlowdownDurationInSeconds());
			result = point.proceed();
		} catch (Throwable e) {
			if (ExceptionManager.shouldPerformActionRetry(e))
				try {
					result = point.proceed();
				} catch (Throwable ee) {
					throw new ActionExecutionException(methodNameWithArguments, ee);
				}
			else
				throw new ActionExecutionException(methodNameWithArguments, e);
		}

		ExecutionLogManager.logMethodEnd(point, MethodType.ACTION, new Date().getTime() - startTime, result);

		if (BaseTest.getTestObject().getScreenshotModes().contains(ScreenshotMode.AFTER_ACTION))
			ReportingTools.takeScreenshot(ScreenshotMode.AFTER_ACTION.name());

		if (BaseTest.getTestObject().getRepeatMode() == RepeatMode.TEST_DURATION
				&& IterationManager.getManager().isTestDurationElapsed()
				&& ExceptionManager.isMethodUnderExecutionATest())
			throw new TestDurationExceededException();

		return result;

	}

	/**
	 * Aspect for {@link Step} methods. Pre and post method execution logic is
	 * written here
	 * 
	 * @param point
	 *            {@link ProceedingJoinPoint} to get access to {@link Step} method
	 * @return Result Result of execution
	 */
	@Around("execution(* *(..)) && @annotation(com.automacent.fwk.annotations.Step)")
	public Object aroundStepCompilerAspect(ProceedingJoinPoint point) {
		long startTime = new Date().getTime();
		ExecutionLogManager.logMethodStart(point, MethodType.STEP);
		String methodNameWithArguments = AspectJUtils.getMethodNameWithArguments(point);

		if (BaseTest.getTestObject().getScreenshotModes().contains(ScreenshotMode.BEFORE_STEP))
			ReportingTools.takeScreenshot(ScreenshotMode.BEFORE_STEP.name());

		Object result = null;
		try {
			result = point.proceed();
		} catch (Throwable e) {
			throw new StepExecutionException(methodNameWithArguments, e);
		}

		ExecutionLogManager.logMethodEnd(point, MethodType.STEP, new Date().getTime() - startTime, result);

		if (BaseTest.getTestObject().getScreenshotModes().contains(ScreenshotMode.AFTER_STEP))
			ReportingTools.takeScreenshot(ScreenshotMode.AFTER_STEP.name());

		return result;
	}
}