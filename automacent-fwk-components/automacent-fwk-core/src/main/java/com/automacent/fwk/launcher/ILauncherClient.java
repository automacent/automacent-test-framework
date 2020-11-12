package com.automacent.fwk.launcher;

import org.testng.ITestContext;

import com.automacent.fwk.enums.MethodType;
import com.automacent.fwk.enums.TestStatus;
import com.automacent.fwk.exceptions.LauncherForceCompletedException;

/**
 * 
 * Rest client for updating results to Launcher DB
 * 
 * @author sighil.sivadas
 *
 */
public interface ILauncherClient {

	/**
	 * Enable client
	 */
	void enableClient();

	/**
	 * Enable all launcher client
	 */
	void disableClient();

	/**
	 * Mark start of XML test on all launcher client
	 * 
	 * @param testContext
	 */
	void startTest(ITestContext testContext);

	/**
	 * Mark success of test/iteration on launcher client
	 * 
	 * @param methodName Name of test method
	 * @param methodType {@link MethodType}
	 * @param iteration  Iteration number
	 * @param duration   Duration of execution of method
	 */
	void logSuccess(String methodName, MethodType methodType, int iteration, long duration);

	/**
	 * Mark failure of test/iteration on launcher client
	 * 
	 * @param methodName Name of test method
	 * @param methodType {@link MethodType}
	 * @param iteration  Iteration number
	 * @param e          {@link Throwable} resulting in failure
	 * @param duration   Duration of execution of method
	 */
	void logFailure(String methodName, MethodType methodType, int iteration, Throwable e, long duration);

	/**
	 * Mark completion of test on launcher client
	 */
	void stopTest();

	/**
	 * Send heart beat to the launcher server
	 *
	 * @throws LauncherForceCompletedException when test instance status is not
	 *                                         RUNNING
	 */
	void ping() throws LauncherForceCompletedException;

	/**
	 * Log start of {@link MethodType}
	 * 
	 * @param methodWithArguments Method name with arguments
	 * @param methodType          {@link MethodType}
	 */
	public void logStart(String methodWithArguments, MethodType methodType);

	/**
	 * Log {@link MethodType} completion and duration. If {@link MethodType} failure
	 * then log exception as well
	 * 
	 * @param methodWithArguments Method name with arguments
	 * @param methodType          {@link MethodType}
	 * @param testStatus          {@link TestStatus}
	 * @param duration            Duration in milliseconds
	 * @param t                   {@link Throwable}
	 */
	public void logEnd(String methodWithArguments, MethodType methodType, TestStatus testStatus, long duration,
			Throwable t);
}