package com.automacent.fwk.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.automacent.fwk.reporting.Logger;

/**
 * Repository of generic methods useful for handling common operations in web
 * pages
 * 
 * @author sighil.sivadas
 **/
public class WebUtils {

	private static final Logger _logger = Logger.getLogger(WebUtils.class);

	/**
	 * Handle certificate error in Internet Explorer
	 * 
	 * @param driver
	 *            WebDriver instance
	 */
	public static void handleCertificateError(WebDriver driver) {
		ThreadUtils.sleepFor(2);
		String pageTitle = driver.getTitle();
		while (pageTitle.toLowerCase().equals("Certificate Error: Navigation Blocked".toLowerCase())) {
			_logger.info(String.format("Page title is %s", pageTitle));
			driver.navigate().to("javascript:document.getElementById('overridelink').click()");
			_logger.info("Certificate Error found and handled");
			pageTitle = driver.getTitle();
		}

		while (pageTitle.toLowerCase().contains("Privacy error".toLowerCase())) {
			_logger.info(String.format("Page title is %s", pageTitle));
			driver.findElement(By.id("details-button")).click();
			driver.findElement(By.id("proceed-link")).click();
			_logger.info("Certificate Error found and handled");
			pageTitle = driver.getTitle();
		}
	}
}
