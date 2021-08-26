package com.automacent.fwk.exceptions;

/**
 * Exception thrownon the invoking rest client
 * 
 * @author sighil.sivadas
 */
public class RestClientInvocationException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RestClientInvocationException(String message, Throwable cause) {
		super(message, cause);
	}
}
