package com.automacent.fwk.exceptions;

import com.automacent.fwk.core.BaseTest;

/**
 * Exception thrown when the test exceeds the set test duration
 * 
 * @author sighil.sivadas
 */
public class TestDurationExceededException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4174917245245884909L;

	public TestDurationExceededException() {
		super(String.format("Set test duration of %s exceeded.", BaseTest.getTestObject().getTestDurationInSeconds()));
	}

}
