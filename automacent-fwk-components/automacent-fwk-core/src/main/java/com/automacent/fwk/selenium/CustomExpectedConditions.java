package com.automacent.fwk.selenium;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
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
 * @deprecated Use {@link AutomacentExpectedConditions} instead
 * @author sighil.sivadas
 *
 */
@Deprecated
public class CustomExpectedConditions {

	/**
	 * An expectation for checking that an element is present on the DOM of a page.
	 * This does not necessarily mean that the element is visible.
	 *
	 * @param locator used to find the element
	 * @return the WebElement once it is located
	 */
	public static ExpectedCondition<WebElement> presenceOfElementLocated(final By locator) {
		return new ExpectedCondition<WebElement>() {
			@Override
			public WebElement apply(WebDriver driver) {
				try {
					driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
					return driver.findElement(locator);
				} finally {
					driver.manage().timeouts().implicitlyWait(BaseTest.getTestObject().getTimeoutInSeconds(),
							TimeUnit.SECONDS);
				}
			}

			@Override
			public String toString() {
				return "presence of element located by: " + locator;
			}
		};
	}

	/**
	 * 
	 * In case of using {@link FindBy} to locate elements, there is no
	 * {@link ExpectedConditions} to override the ImplicitWait set and use
	 * {@link WebDriverWait}. The custom proxyElementLocated
	 * {@link CustomExpectedConditions#waitTillProxyElementLocated(WebElement)} will
	 * take care of this scenario
	 * 
	 * @param proxyElement Proxy {@link WebElement} object
	 * @return null if not found, element if found
	 */
	@Action
	public static ExpectedCondition<WebElement> waitTillProxyElementLocated(final WebElement proxyElement) {
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

	/**
	 * Wait until text in an element not matches the provided text
	 * 
	 * @param proxyElement The proxy {@link WebElement}
	 * @param text         The text which is expected not present in
	 *                     {@link WebElement#getText()}
	 * @return {@link ExpectedCondition} containing the {@link WebElement}
	 */
	@Action
	public static ExpectedCondition<WebElement> waitTillTextInElementNotMatches(final WebElement proxyElement,
			String text) {
		return new ExpectedCondition<WebElement>() {
			@Override
			public WebElement apply(WebDriver driver) {
				driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
				try {
					return !proxyElement.getText().equals("") ? proxyElement : null;
				} finally {
					driver.manage().timeouts().implicitlyWait(BaseTest.getTestObject().getTimeoutInSeconds(),
							TimeUnit.SECONDS);
				}
			}

			@Override
			public String toString() {
				ExpectedConditions.alertIsPresent();
				return "Text in Element Not Matches given value";
			}
		};
	}

	/**
	 * Wait until an element is no longer attached to the DOM.
	 *
	 * @param element The element to wait for.
	 * @return false if the element is still attached to the DOM, true otherwise.
	 */
	@Action
	public static ExpectedCondition<Boolean> stalenessOf(final WebElement element) {
		return new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
				try {
					// Calling any method forces a staleness check
					element.isEnabled();
					return false;
				} catch (StaleElementReferenceException expected) {
					return true;
				} catch (NoSuchElementException e) {
					return true;
				} finally {
					driver.manage().timeouts().implicitlyWait(BaseTest.getTestObject().getTimeoutInSeconds(),
							TimeUnit.SECONDS);
				}
			}

			@Override
			public String toString() {
				return String.format("element (%s) to become stale", element);
			}
		};
	}

	/**
	 * Wait until provided {@link WebElement} is visible
	 * 
	 * @param element Element whose visibility has to be checked
	 * @return True if element is visible
	 */
	public static ExpectedCondition<Boolean> visibilityOf(final WebElement element) {
		return new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
				try {
					return element.isDisplayed();
				} finally {
					driver.manage().timeouts().implicitlyWait(BaseTest.getTestObject().getTimeoutInSeconds(),
							TimeUnit.SECONDS);
				}
			}

			@Override
			public String toString() {
				return "visibility of " + element;
			}
		};
	}
}