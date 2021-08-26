package com.automacent.fwk.exceptions;

/**
 * Exception is thrown when there is an error in setting up the environment for
 * execution
 * 
 * @author sighil.sivadas
 */
public class SetupFailedFatalException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3147446753963664599L;

	public SetupFailedFatalException(Throwable cause) {
		super("Error in Setting up the environment for execution", cause);
	}

	public SetupFailedFatalException(String message, Throwable cause) {
		super(message, cause);
	}

	public SetupFailedFatalException(String message) {
		super(message);
	}
}
