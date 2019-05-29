package com.automacent.fwk.enums;

/**
 * Enum describing the Retry Mode. Based on this, the framework determines
 * whether to retry a test or not. This can be set in the test (TestNG xml file)
 * as a parameter so that the MODE will be set for the whole SUITE or for a
 * particular test instance
 * 
 * @author sighil.sivadas
 */
public enum RetryMode {
	OFF, ON;

	public static RetryMode getDefault() {
		return OFF;
	}
}
