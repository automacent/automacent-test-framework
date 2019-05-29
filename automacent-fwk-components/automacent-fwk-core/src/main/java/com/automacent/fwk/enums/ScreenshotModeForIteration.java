package com.automacent.fwk.enums;

/**
 * 
 * ENUM describing the MODE in which screenshots have to be taken for
 * iterations. This can be set in the test (TestNG xml file) as a parameter so
 * that the MODE will be set for the whole SUITE or for a particular test
 * instance
 * 
 * @author sighil.sivadas
 *
 */
public enum ScreenshotModeForIteration {
	FAILED_ITERATION, EACH_ITERATION, LAST_ITERATION;

	public static ScreenshotModeForIteration getDefault() {
		return FAILED_ITERATION;
	}
}
