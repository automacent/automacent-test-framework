package com.automacent.fwk.selenium;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Custom Expected conditions to be used with {@link WebDriverWait}
 * 
 * @author sighil.sivadas
 *
 */
public class CustomExpectedConditions {

	/**
	 * 
	 * In case of using {@link FindBy} to locate elements, there is no
	 * {@link ExpectedConditions} to override the ImplicitWait set and use
	 * {@link WebDriverWait}. The custom proxyElementLocated
	 * {@link CustomExpectedConditions#proxyElementLocated(WebElement)} will take
	 * care of this scenario
	 * 
	 * @param proxyElement
	 * @return null if not found, element if found
	 */
	public static ExpectedCondition<WebElement> proxyElementLocated(final WebElement proxyElement) {
		return new ExpectedCondition<WebElement>() {
			@Override
			public WebElement apply(WebDriver driver) {
				try {
					proxyElement.getTagName();
				} catch (NoSuchElementException e) {
					return null;
				}
				return proxyElement;
			}

			@Override
			public String toString() {
				return "Proxy element to be locate in DOM";
			}
		};
	}
}