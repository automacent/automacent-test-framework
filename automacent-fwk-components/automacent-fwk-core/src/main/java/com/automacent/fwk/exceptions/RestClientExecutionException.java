package com.automacent.fwk.exceptions;

/**
 * Exception thrown on the executing rest client
 * 
 * @author sighil.sivadas
 */
public class RestClientExecutionException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RestClientExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public RestClientExecutionException(String message) {
		super(message);
	}
}
