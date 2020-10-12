package com.automacent.fwk.exceptions;

import com.automacent.fwk.enums.RepeatMode;

/**
 * Exception to be used when no time is remaining / invocation count equals zero
 * and thus, the main test method cannot be invoked
 * 
 * @author sighil.sivadas
 */
public class MainTestInvocationFailedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MainTestInvocationFailedException(String methodName, RepeatMode repeatMode) {
		super(String.format("%s not executed. No time/count remaining. Repeat mode is %s", methodName,
				repeatMode.name()));
	}

	public MainTestInvocationFailedException(String methodName, String message) {
		super(String.format("Failed to execute @Test %s. %s", methodName, message));
	}
}
