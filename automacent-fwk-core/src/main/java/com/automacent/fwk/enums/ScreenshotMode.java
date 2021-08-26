package com.automacent.fwk.enums;

/**
 * ENUM describing the screenshot mode. This determines the places where the
 * screenshot will be taken automatically by the framework. These values can be
 * set in the test (TestNG xml file) as a parameter so that the MODE will be set
 * for the whole SUITE or for a particular test instance
 * 
 * @author sighil.sivadas
 */
public enum ScreenshotMode {
	ON_FAILURE, AFTER_STEP, BEFORE_STEP, BEFORE_ACTION, AFTER_ACTION;

	public static ScreenshotMode getDefault() {
		return ON_FAILURE;
	}
}
