package com.automacent.fwk.exceptions;

import org.testng.TestNGException;

/**
 * Exception thrown when there is an error while executing a test method and the
 * test method is skipped due to it
 * 
 * @author sighil.sivadas
 */
public class TestOrConfigurationSkipException extends TestNGException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3147446753963664599L;

	public TestOrConfigurationSkipException() {
		super("Configuration / Test Skipped");
	}

	public TestOrConfigurationSkipException(Throwable t) {
		super("Configuration / Test Skipped ", t);
	}

}
