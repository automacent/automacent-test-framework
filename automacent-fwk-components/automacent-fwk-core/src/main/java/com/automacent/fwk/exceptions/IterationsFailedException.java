package com.automacent.fwk.exceptions;

import java.util.Map;

/**
 * Exception while executing tests containing iterations. This exception is use
 * dto handle test failure due to iteration failure
 * 
 * @author sighil.sivadas
 */
public class IterationsFailedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8187605870144828935L;

	private IterationsFailedException(String message) {
		super(message);
	}

	/**
	 * initialize the Exception with the map of test failure errors
	 * 
	 * @param errorMap
	 */
	public IterationsFailedException(Map<Integer, String> errorMap) {
		String message = "Test failed because there are iteration failures";
		for (int iteration : errorMap.keySet()) {
			message += String.format("%s\nIteration %s - %s", message, iteration, errorMap.get(iteration));
		}
		new IterationsFailedException(message);
	}
}
