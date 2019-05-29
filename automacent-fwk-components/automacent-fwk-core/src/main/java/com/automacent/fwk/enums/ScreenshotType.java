package com.automacent.fwk.enums;

import com.automacent.fwk.reporting.ReportingTools;

/**
 * ENUM describing the type of screenshot. The ENUM value determines the type of
 * screenshot the framework takes by default when
 * {@link ReportingTools#takeScreenshot()} call is made. This can be set in the
 * test (TestNG xml file) as a parameter so that the MODE will be set for the
 * whole SUITE or for a particular test instance
 * 
 * @author sighil.sivadas
 *
 */
public enum ScreenshotType {
	OFF, BROWSER_SCREENSHOT, DESKTOP_SCREENSHOT;

	public static ScreenshotType getDefault() {
		return OFF;
	}
}
