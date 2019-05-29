package com.automacent.fwk.core;

import org.openqa.selenium.WebDriver;

/**
 * Base class for Web Test Step classes. All Step classes must extend this
 * class. Inheriting this class will enforce parity in constructor signature and
 * provide common functions for use in step libraries. This class provides
 * {@link BrowserControls} in addition to other Step executiokn utilities
 * 
 * @author sighil.sivadas
 *
 */
public class WebTestSteps extends BrowserControls {

	public WebTestSteps(WebDriver driver) {
		super(driver);
	}

}
