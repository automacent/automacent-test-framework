package com.automacent.fwk.core;

import java.util.regex.Pattern;

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

	protected static final Pattern FIND_HTML_TAGS = Pattern.compile("<[^>]*>");

}
