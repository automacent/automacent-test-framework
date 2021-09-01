package com.automacent.fwk.exceptions;

import com.automacent.fwk.annotations.Step;

/**
 * Exception while executing {@link Step} methods. This exception will help in
 * reporting test failures
 * 
 * @author sighil.sivadas
 */
public class StepExecutionException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String methodName;

	public StepExecutionException(String methodName, Throwable cause) {
		super(String.format("[@Step %s] %s", methodName, cause.getMessage()), cause);
		this.methodName = methodName;
	}

	/**
	 * Get the method name where the failure occurred
	 * 
	 * @return Method name
	 */
	public String getMethodName() {
		return methodName;
	}

}
