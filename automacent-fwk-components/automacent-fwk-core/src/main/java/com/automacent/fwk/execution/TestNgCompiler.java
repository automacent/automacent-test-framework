package com.automacent.fwk.execution;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.testng.annotations.Test;

import com.automacent.fwk.annotations.Repeat;
import com.automacent.fwk.core.BaseTest;
import com.automacent.fwk.enums.MethodType;
import com.automacent.fwk.enums.RepeatMode;
import com.automacent.fwk.enums.RetryMode;
import com.automacent.fwk.exceptions.IterationFailedException;
import com.automacent.fwk.exceptions.MainTestInvocationFailedException;
import com.automacent.fwk.exceptions.TestDurationExceededException;
import com.automacent.fwk.reporting.ExecutionLogManager;
import com.automacent.fwk.reporting.Logger;

/**
 * This class contains aspects for manipulating TestNG Test, Before* and After*
 * methods. This class enhances the properties of TestNG test methods
 * 
 * @author sighil.sivadas
 *
 */
@Aspect
public class TestNgCompiler {

	private static final Logger _logger = Logger.getLogger(TestNgCompiler.class);

	/**
	 * Aspect for TestNG Before* methods. Pre and post method execution logic is
	 * written here
	 * 
	 * @param point
	 *            {@link ProceedingJoinPoint} to get access to method
	 * @return Result of execution
	 * @throws Throwable
	 *             Throwable resulting in test failure
	 */
	@Around("(execution(* *(..)) && @annotation(org.testng.annotations.BeforeSuite)) || "
			+ "(execution(* *(..)) && @annotation(org.testng.annotations.BeforeGroups)) || "
			+ "(execution(* *(..)) && @annotation(org.testng.annotations.BeforeClass)) || "
			+ "(execution(* *(..)) && @annotation(org.testng.annotations.BeforeTest)) || "
			+ "(execution(* *(..)) && @annotation(org.testng.annotations.BeforeMethod))")
	public Object aroundBeforeCompilerAspect(ProceedingJoinPoint point) throws Throwable {
		long startTime = new Date().getTime();
		ExecutionLogManager.logTestStart(point, MethodType.BEFORE);
		Object result = null;
		try {
			result = point.proceed();
			ExecutionLogManager.logTestSuccess(point, MethodType.BEFORE, new Date().getTime() - startTime);
		} catch (Throwable e) {
			ExecutionLogManager.logTestFailure(point, MethodType.BEFORE, e, new Date().getTime() - startTime);
			throw e;
		}
		return result;
	}

	/**
	 * Aspect for TestNG After* methods. Pre and post method execution logic is
	 * written here
	 * 
	 * @param point
	 *            {@link ProceedingJoinPoint} to get access to method
	 * @return Result of execution
	 * @throws Throwable
	 *             Throwable resulting in test failure
	 */
	@Around("(execution(* *(..)) && @annotation(org.testng.annotations.AfterSuite)) || "
			+ "(execution(* *(..)) && @annotation(org.testng.annotations.AfterGroups)) || "
			+ "(execution(* *(..)) && @annotation(org.testng.annotations.AfterClass)) || "
			+ "(execution(* *(..)) && @annotation(org.testng.annotations.AfterTest)) || "
			+ "(execution(* *(..)) && @annotation(org.testng.annotations.AfterMethod))")
	public Object aroundAfterCompilerAspect(ProceedingJoinPoint point) throws Throwable {
		long startTime = new Date().getTime();
		ExecutionLogManager.logTestStart(point, MethodType.AFTER);
		Object result = null;
		try {
			result = point.proceed();
			ExecutionLogManager.logTestSuccess(point, MethodType.AFTER, new Date().getTime() - startTime);
		} catch (Throwable e) {
			ExecutionLogManager.logTestFailure(point, MethodType.AFTER, e, new Date().getTime() - startTime);
			throw e;
		}
		return result;
	}

	/**
	 * Aspect for TestNG {@link Test} methods. Pre and post method execution logic
	 * is written here. This method also implements {@link Repeat} logic
	 * 
	 * @param point
	 *            {@link ProceedingJoinPoint} to get access to method
	 * @return Result of execution
	 * @throws Throwable
	 *             Throwable resulting in test failure
	 */
	@Around("execution(* *(..)) && @annotation(org.testng.annotations.Test)")
	public Object aroundTestCompilerAspect(ProceedingJoinPoint point) throws Throwable {
		long startTime = new Date().getTime();
		Method method = MethodSignature.class.cast(point.getSignature()).getMethod();
		boolean repeat = false;
		RepeatMode repeatMode = BaseTest.getTestObject().getRepeatMode();
		if (!repeatMode.name().equals(RepeatMode.OFF.name())) {
			Annotation[] annotations = method.getAnnotations();
			for (Annotation annotation : annotations)
				if (annotation.annotationType() == Repeat.class) {
					repeat = true;
					break;
				}
			if (!repeat) {
				BaseTest.getTestObject().setRepeatMode(RepeatMode.OFF.name());
				_logger.info(String
						.format("Starting test without repeat logic sice @Repeat annotation is not used on the test. "
								+ "Repeat Mode is %s", repeatMode.name()));
			}
		} else {
			_logger.info("Starting test without repeat logic. Repeat Mode is OFF");
		}

		Object result = null;
		if (repeat) {
			ExecutionLogManager.logTestStart(point, MethodType.TEST);
			_logger.info(String.format("Starting test with the repeat logic. Repeat mode is %s", repeatMode.name()));
			while (IterationManager.getManager().isIterationRemaining()) {
				IterationManager.getManager().startIteration();
				long iterationStartTime = new Date().getTime();
				try {
					try {
						IterationManager.getManager().sleepBetweenIteration();
					} catch (TestDurationExceededException e) {
						_logger.warn("Test Duration exceeded during sleep between iterations");
						throw e;
					}
					BaseTest.getTestObject().getRecoveryManager().executeRecoveryScenarios();
					result = point.proceed();
					ExecutionLogManager.logIterationSuccess(point, new Date().getTime() - iterationStartTime);
				} catch (Throwable e) {
					if (ExceptionManager.isExceptionTestDurationExceededException(e)
							&& IterationManager.getManager().getIteration() > 1) {
						_logger.warn("Test Duration exceeded during execution. "
								+ "Iteration will exit without failure as at least one iteration is completed.");
					} else {
						ExecutionLogManager.logIterationFailure(point, e, new Date().getTime() - iterationStartTime);
						IterationManager.getManager().addError(e);
						IterationManager.getManager().setExecuteRecoveryScenarios(true);
					}
				} finally {
					IterationManager.getManager().stopIteration();
				}
			}

			if (IterationManager.getManager().getIteration() == 0) {
				Throwable e = new MainTestInvocationFailedException(method.getName(), repeatMode);
				IterationManager.getManager().addError(e);
			}
			Map<Integer, String> errorMap = IterationManager.getManager().getErrorMap();

			if (!errorMap.isEmpty()) {
				try {
					throw new IterationFailedException(errorMap);
				} catch (Exception e) {
					ExecutionLogManager.logTestFailure(point, MethodType.TEST, e, new Date().getTime() - startTime);
					throw e;
				}
			} else {
				ExecutionLogManager.logTestSuccess(point, MethodType.TEST, new Date().getTime() - startTime);
			}
		} else {
			ExecutionLogManager.logTestStart(point, MethodType.TEST);
			RetryMode retryMode = BaseTest.getTestObject().getRetryMode();
			try {
				result = point.proceed();
				ExecutionLogManager.logTestSuccess(point, MethodType.TEST, new Date().getTime() - startTime);
			} catch (Throwable e) {
				ExecutionLogManager.logTestFailure(point, MethodType.TEST, e, new Date().getTime() - startTime);
				if (retryMode.name().equals(RetryMode.ON.name())) {
					ExecutionLogManager.logTestStart(point, MethodType.RETRY);
					long retryStartTime = new Date().getTime();
					_logger.info("Retrying test as Retry Mode is ON");
					BaseTest.getTestObject().getRecoveryManager().executeRecoveryScenarios();
					try {
						result = point.proceed();
						ExecutionLogManager.logTestSuccess(point, MethodType.RETRY,
								new Date().getTime() - retryStartTime);
					} catch (Throwable ee) {
						ExecutionLogManager.logTestFailure(point, MethodType.RETRY, e,
								new Date().getTime() - retryStartTime);
						throw e;
					}
				} else {
					_logger.info("Execution will not retry test as Retry Mode is OFF");
					throw e;
				}
			}
		}
		return result;
	}
}
