package com.automacent.fwk.exceptions;

import com.automacent.fwk.annotations.Action;

/**
 * Exception while executing {@link Action} methods. This exception will help in
 * reporting test failures
 * 
 * @author sighil.sivadas
 */
public class ActionExecutionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ActionExecutionException(String methodName, Throwable cause) {
		super(String.format("[@Action %s] %s", methodName, cause.getMessage()), cause);
	}
}
