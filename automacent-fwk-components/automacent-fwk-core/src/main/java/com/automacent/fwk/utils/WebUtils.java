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
		String PageTitle = driver.getTitle();
		while (PageTitle.equals("Certificate Error: Navigation Blocked")) {
			driver.navigate().to("javascript:document.getElementById('overridelink').click()");
			PageTitle = driver.getTitle();
			_logger.info("Certificate Error found and handled");
		}

		while (PageTitle.equals("Privacy error")) {
			driver.findElement(By.id("details-button")).click();
			driver.findElement(By.id("proceed-link")).click();
		}
	}
}
