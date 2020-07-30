package com.automacent.fwk.selenium;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.automacent.fwk.annotations.Action;
import com.automacent.fwk.core.BaseTest;

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
	 * {@link CustomExpectedConditions#waitTillproxyElementLocated(WebElement)} will take
	 * care of this scenario
	 * 
	 * @param proxyElement Proxy {@link WebElement} object
	 * @return null if not found, element if found
	 */
	@Action
	public static ExpectedCondition<WebElement> waitTillproxyElementLocated(final WebElement proxyElement) {
		return new ExpectedCondition<WebElement>() {
			@Override
			public WebElement apply(WebDriver driver) {
				driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
				try {
					proxyElement.getTagName();
				} catch (NoSuchElementException e) {
					return null;
				} finally {
					driver.manage().timeouts().implicitlyWait(BaseTest.getTestObject().getTimeoutInSeconds(),
							TimeUnit.SECONDS);
				}
				return proxyElement;
			}

			@Override
			public String toString() {
				return "Proxy element to be locate in DOM";
			}
		};
	}

	@Action
	public static ExpectedCondition<WebElement> waitTillTextInElementNotMatches(final WebElement proxyElement, String text) {
		return new ExpectedCondition<WebElement>() {
			@Override
			public WebElement apply(WebDriver driver) {
				driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
				try {
					return !proxyElement.getText().equals("") ? proxyElement : null;
				} catch (NoSuchElementException e) {
					return null;
				} finally {
					driver.manage().timeouts().implicitlyWait(BaseTest.getTestObject().getTimeoutInSeconds(),
							TimeUnit.SECONDS);
				}
			}

			@Override
			public String toString() {
				return "Text in Element Not Matches given value";
			}
		};
	}
}