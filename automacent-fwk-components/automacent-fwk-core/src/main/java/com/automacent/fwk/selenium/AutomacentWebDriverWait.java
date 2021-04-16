package com.automacent.fwk.selenium;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.automacent.fwk.core.BaseTest;

/**
 * This class adds additional methods to the {@link WebDriverWait} for reducing
 * the dependency on call {@link WebDriverWait#until}({@link ExpectedCondition}). The
 * additional methods will directly reference the {@link ExpectedCondition}
 * without the user explicitly calling the {@link ExpectedCondition} conditions.
 * 
 * All the new until* methods will throws {@link TimeoutException} if the
 * condition fails
 * 
 * @author sighil.sivadas
 *
 */
public class AutomacentWebDriverWait extends WebDriverWait {

	private final WebDriver driver;

	public AutomacentWebDriverWait(WebDriver driver, long timeOutInSeconds) {
		super(driver, timeOutInSeconds);
		this.driver = driver;
	}

	/**
	 * Execute the until() condition by setting the proper implicit and explicit
	 * wait timeouts
	 * 
	 * @param <V>       Return type of {@link ExpectedCondition}
	 * @param condition {@link ExpectedCondition}
	 * @return Object that is expected to be returned after the
	 *         {@link ExpectedCondition} succeeds
	 */
	private <V> V applyExplicit(ExpectedCondition<V> condition) {
		try {
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			return until(condition);
		} finally {
			driver.manage().timeouts().implicitlyWait(BaseTest.getTestObject().getTimeoutInSeconds(), TimeUnit.SECONDS);
		}
	}

	/**
	 * Wait until element defined by locator {@link By} is located in the DOM
	 * 
	 * @param by Element identifier
	 * @return Located {@link WebElement}
	 */
	public WebElement untilPresenceOfElementLocated(final By by) {
		return applyExplicit(ExpectedConditions.presenceOfElementLocated(by));
	}

	/**
	 * Wait until proxy element declared by @FindBy is located in the DOM
	 * 
	 * @param element {@link WebElement} on which condition has to be checked
	 * @return Located {@link WebElement}
	 */
	public WebElement untilProxyElementLocated(final WebElement element) {
		return applyExplicit(new ExpectedCondition<WebElement>() {
			@Override
			public WebElement apply(WebDriver driver) {
				element.getTagName();
				return element;
			}

			@Override
			public String toString() {
				return "Proxy element to be locate in DOM";
			}
		});
	}

	/**
	 * Wait until {@link WebElement} is visible in the DOM
	 * 
	 * @param element {@link WebElement} on which condition has to be checked
	 * @return Visible {@link WebElement}
	 */
	public WebElement untilVisibilityOf(final WebElement element) {
		return applyExplicit(ExpectedConditions.visibilityOf(element));
	}

	/**
	 * Wait until {@link WebElement} to be clickable in the UI
	 * 
	 * @param element {@link WebElement} on which condition has to be checked
	 * @return Clickable {@link WebElement}
	 */
	public WebElement untilElementToBeClickable(final WebElement element) {
		return applyExplicit(ExpectedConditions.elementToBeClickable(element));
	}

	/**
	 * Wait until {@link WebElement} is state has changed in the DOM and would need
	 * re-identification
	 * 
	 * @param element {@link WebElement} on which condition has to be checked
	 * @return true will element is stale
	 */
	public Boolean untilStalenessOf(final WebElement element) {
		return applyExplicit(ExpectedConditions.stalenessOf(element));
	}

	/**
	 * Wait until {@link WebElement} is does not contain the provided text
	 * 
	 * @param element {@link WebElement} on which condition has to be checked
	 * @param text    The text that should not be present
	 * @return true if text is not found in the {@link WebElement}
	 */
	public Boolean untilTextInElementNotMatches(final WebElement element, String text) {
		return applyExplicit(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				return !element.getText().equals(text);
			}

			@Override
			public String toString() {
				return "Text in Element Not Matches given value";
			}
		});
	}
}
