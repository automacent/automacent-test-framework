package com.automacent.fwk.exceptions;

/**
 * Custom exception thrown to indicate that an issue occurred during the
 * execution of recovery scenarios.
 * 
 * @author sighil.sivadas
 */
public class RecoveryFailedException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RecoveryFailedException(String message) {
		super(message);
	}

	public RecoveryFailedException(String message, Throwable e) {
		super(String.format("%s %s", message, e.getMessage()), e);
	}

	public RecoveryFailedException(Throwable e) {
		super(e);
	}

}
