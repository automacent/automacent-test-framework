package com.automacent.fwk.launcher;

import org.testng.IInvokedMethod;
import org.testng.ITestContext;
import org.testng.ITestResult;

import com.automacent.fwk.core.TestObject;
import com.automacent.fwk.enums.MethodType;
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
	 * 
	 */
	void enableClient();

	/**
	 * Enable all launcher client
	 */
	void disableClient();

	/**
	 * Mark start of test on all launcher client
	 * 
	 * @param testObject
	 * @param invokedMethod
	 * @param testResult
	 * @param testContext
	 */
	void startTest(TestObject testObject, IInvokedMethod invokedMethod, ITestResult testResult,
			ITestContext testContext);

	/**
	 * Mark success of test/iteration on launcher client
	 * 
	 * @param methodName
	 *            Name of test method
	 * @param methodType
	 *            {@link MethodType}
	 * @param iteration
	 *            Iteration number
	 * @param duration
	 *            Duration of execution of method
	 */
	void logSuccess(String methodName, MethodType methodType, int iteration, long duration);

	/**
	 * Mark failure of test/iteration on launcher client
	 * 
	 * @param methodName
	 *            Name of test method
	 * @param methodType
	 *            {@link MethodType}
	 * @param iteration
	 *            Iteration number
	 * @param e
	 *            {@link Throwable} resulting in failure
	 * @param duration
	 *            Duration of execution of method
	 */
	void logFailure(String methodName, MethodType methodType, int iteration, Throwable e, long duration);

	/**
	 * Mark completion of test on launcher client
	 */
	void stopTest();

	/**
	 * Send heart beat to the launcher server
	 *
	 * @throws LauncherForceCompletedException
	 *             when test instance status is not RUNNING
	 */
	void ping() throws LauncherForceCompletedException;
}