package com.automacent.fwk.core;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.automacent.fwk.enums.ExpectedCondition;
import com.automacent.fwk.reporting.Logger;
import com.automacent.fwk.selenium.CustomExpectedConditions;

/**
 * Base class for Page/View. All Page/View classes must extend this class.
 * Inheriting this call will enforce parity in constructor signature and provide
 * common functions for use in View/Page (page object) libraries.
 * 
 * @author sighil.sivadas
 */
public class PageObject implements IPageObject {

	private static Logger _logger = Logger.getLogger(PageObject.class);

	protected WebDriver driver;

	public PageObject() {
		driver = BaseTest.getTestObject().getDriverManager().getActiveDriver().getWebDriver();
		setExplicitWaitInSeconds((int) BaseTest.getTestObject().getTimeoutInSeconds());
		PageFactory.initElements(driver, this);
	}

	/**
	 * Initialize the specified page class and return object
	 * 
	 * @param page
	 *            Page class to be initialized
	 * @return Instance of class or null if error
	 */
	protected <T extends IPageObject> T getPage(Class<T> page) {
		try {
			return (T) page.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			_logger.warn(String.format("Error initializing component %s", page.getName()), e);
		}
		return null;
	}

	private int explicitWaitInSeconds = 0;

	/**
	 * @return Explicit wait timeout in seconds which is equal to timeout in seconds
	 *         parameter provided in Test class
	 */
	protected int getExplicitWaitInSeconds() {
		return explicitWaitInSeconds;
	}

	/**
	 * Set Explicit wait timeout in seconds
	 * 
	 * @param explicitWaitInSeconds
	 */
	private void setExplicitWaitInSeconds(int explicitWaitInSeconds) {
		this.explicitWaitInSeconds = explicitWaitInSeconds;
	}

	// Javascript Executor ------------------------------------------

	/**
	 * Get {@link JavascriptExecutor} object
	 * 
	 * @return {@link JavascriptExecutor}
	 */
	protected JavascriptExecutor getJavascriptExecutor() {
		return ((JavascriptExecutor) driver);
	}

	/**
	 * Execute provided Javascript command
	 * 
	 * @param command
	 *            javascript
	 * @param elements
	 *            {@link WebElement}s to be used Javascript
	 */
	@SuppressWarnings("all")
	@com.automacent.fwk.annotations.Action
	protected void executeJavascript(String command, WebElement... elements) {
		getJavascriptExecutor().executeScript(command, elements);
	}

	/**
	 * Perform Javascript click
	 * 
	 * @param element
	 *            {@link WebElement} on which click has to be performed
	 */
	@com.automacent.fwk.annotations.Action
	protected void javascriptClick(WebElement element) {
		executeJavascript("arguments[0].click()", element);
	}

	/**
	 * Scroll element to view using js scrollIntoView() Javascript function
	 * 
	 * @param element
	 *            {@link WebElement} to scroll into view
	 */
	@com.automacent.fwk.annotations.Action
	protected void scrollElementToViewUsingJs(WebElement element) {
		executeJavascript("arguments[0].scrollIntoView(true)", element);
	}

	// Mouse Action -------------------------------------------------

	/**
	 * Get {@link Actions} instance
	 * 
	 * @return @link Actions} object
	 */
	public Actions mouse() {
		return new Actions(driver);
	}

	/**
	 * Execute mouse {@link Actions}
	 * 
	 * @param actions
	 *            The sequence of {@link Actions} to be performed
	 */
	public void performMouseAction(Actions actions) {
		actions.build().perform();
	}

	/**
	 * Scroll element to view using selenium {@link Action} (mouse)
	 * 
	 * @param element
	 *            {@link WebElement} to scroll into view
	 */
	@com.automacent.fwk.annotations.Action
	protected void scrollElementToViewUsingMouse(WebElement element) {
		performMouseAction(mouse().moveToElement(element));
	}

	/**
	 * Move mouse relative to an element and click on the location. Offset should be
	 * in pixels.
	 * 
	 * @param driver
	 *            WebDriver instance
	 * @param element
	 *            Element against which we have to move
	 * @param x
	 *            x-axis to move
	 * @param y
	 *            y-axis to move
	 */
	@com.automacent.fwk.annotations.Action
	public void moveRelativeToElementAndClick(WebElement element, int x, int y) {
		Actions actions = new Actions(driver);
		actions.moveToElement(element).moveByOffset(x, y);
		actions.perform();
		actions.click();

		Action action = actions.build();
		action.perform();
	}

	// Explicit wait ------------------------------------------------

	/**
	 * Get the {@link WebDriverWait} (explicit wait) object
	 * 
	 * @param explicitWaitInSeconds
	 *            Explicit wait timeout
	 * @return {@link WebDriverWait} object
	 */
	protected WebDriverWait explicitWait(int explicitWaitInSeconds) {
		return new WebDriverWait(driver, explicitWaitInSeconds);
	}

	/**
	 * Set implicit wait on the {@link WebDriver} instance to 1 seconds. This will
	 * reduce the time mismatch issues which can arise when using implicit wait and
	 * explicit wait. For example when checking element is clickable using explicit
	 * wait and explicit wait is less than implicit wait, the implicit wait timeout
	 * overrides the explicit wait
	 */
	private void setImplicitWaitToOneSecond() {
		_logger.debug("Setting implicit wait to 1");
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
	}

	/**
	 * Common function to implement the different explicit wait scenarios
	 * 
	 * @param expectedCondition
	 *            {@link ExpectedCondition}s
	 * @param by
	 *            {@link By} instance to identify the {@link WebElement} on which
	 *            wait has to be performed. This can be null if {@link WebElement}
	 *            parameter is provided
	 * @param element
	 *            {@link WebElement} instance on which wait has to be provided. This
	 *            can be null in {@link By} parameter is provided
	 * @param explicitWaitInSeconds
	 *            Explicit wait timeout in seconds
	 * @return {@link WebElement} on which wait was performed
	 */
	private WebElement waitUntil(ExpectedCondition expectedCondition, By by, WebElement element,
			int explicitWaitInSeconds) {
		setImplicitWaitToOneSecond();
		WebElement returnElement = null;
		try {
			switch (expectedCondition) {
			case PRESENCE_OF_ELEMENT_LOCATED_BY:
				returnElement = explicitWait(explicitWaitInSeconds)
						.until(ExpectedConditions.presenceOfElementLocated(by));
				break;
			case PROXY_ELEMENT_LOCATED:
				returnElement = explicitWait(explicitWaitInSeconds)
						.until(CustomExpectedConditions.proxyElementLocated(element));
				break;
			default:
				break;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			_logger.debug(String.format("Setting implicit wait to %s seconds",
					BaseTest.getTestObject().getTimeoutInSeconds()));
			driver.manage().timeouts().implicitlyWait(BaseTest.getTestObject().getTimeoutInSeconds(),
					TimeUnit.SECONDS);
		}
		return returnElement;
	}

	/**
	 * Override the Implicit wait and enforce the explicit wait for checking whether
	 * the {@link WebElement} is present in the DOM
	 *
	 * @param element
	 *            {@link WebElement} object
	 * @param explicitWaitInSeconds
	 *            Explicit wait timeout in seconds
	 * @return true if element is found
	 */
	public boolean isElementFound(WebElement element, int explicitWaitInSeconds) {
		boolean isFound = false;
		try {
			WebElement returnElement = waitUntil(ExpectedCondition.PROXY_ELEMENT_LOCATED, null, element,
					explicitWaitInSeconds);
			if (returnElement != null)
				isFound = true;
		} catch (TimeoutException | NoSuchElementException e) {
		}
		return isFound;
	}

	/**
	 * Override the Implicit wait and enforce the explicit wait for checking whether
	 * the {@link WebElement} is present in the DOM
	 *
	 * @param by
	 *            {@link By} object
	 * @param explicitWaitInSeconds
	 *            Explicit wait timeout in seconds
	 * @return true if element is found
	 */
	public boolean isElementFound(By by, int explicitWaitInSeconds) {
		boolean isFound = false;
		try {
			WebElement returnElement = waitUntil(ExpectedCondition.PRESENCE_OF_ELEMENT_LOCATED_BY, by, null,
					explicitWaitInSeconds);
			if (returnElement != null)
				isFound = true;
		} catch (TimeoutException | NoSuchElementException e) {
		}
		return isFound;
	}

	/**
	 * Override the Implicit wait and enforce the explicit wait for checking whether
	 * the {@link WebElement} is present in the DOM. Default explicit wait is used
	 *
	 * @param element
	 *            {@link WebElement} object
	 * @return true if element is found
	 */
	public boolean isElementFound(WebElement element) {
		return isElementFound(element, getExplicitWaitInSeconds());
	}

	/**
	 * Override the Implicit wait and enforce the explicit wait for checking whether
	 * the {@link WebElement} is present in the DOM. Default explicit wait is used
	 *
	 * @param by
	 *            {@link By} object
	 * @return true if element is found
	 */
	public boolean isElementFound(By by) {
		return isElementFound(by, getExplicitWaitInSeconds());
	}
}