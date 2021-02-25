package com.automacent.fwk.core;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocator;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
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
public abstract class PageObject implements IPageObject {

	private static Logger _logger = Logger.getLogger(PageObject.class);

	protected WebDriver driver;
	protected SearchContext component;
	protected By parentContainerLocator;
	private WebElement superContainer;

	/**
	 * Get the parent container
	 * 
	 * @return Parent container
	 */
	protected WebElement getParentContainer() {
		return (WebElement) component;
	}

	/**
	 * Reinitializes the {@link PageFactory}. This method can be use to
	 * re-initialize {@link WebElement} found by {@link FindBy} in page objects
	 * 
	 * @return true if re-initialization successful
	 */
	public boolean reInitializePageObject() {
		_logger.info("Attempting to re-initialize page object");
		boolean isSuccessful = false;
		if (parentContainerLocator != null) {
			if (superContainer == null) {
				try {
					PageFactory.initElements(field -> {
						return new DefaultElementLocator(driver.findElement(parentContainerLocator), field);
					}, this);
					this.component = driver.findElement(parentContainerLocator);
					this.superContainer = null;
					isSuccessful = true;
				} catch (Exception e) {
					_logger.info(String.format(
							"Error trying to reinitialize page object with parent Container Locator. Error is %s",
							e.getMessage()));
				}
			} else {
				try {
					((WebElement) superContainer).getTagName();
					WebElement newSuperContainer = superContainer;
					PageFactory.initElements(field -> {
						return new DefaultElementLocator(newSuperContainer.findElement(parentContainerLocator),
								field);
					}, this);
					this.component = superContainer.findElement(parentContainerLocator);
					this.superContainer = newSuperContainer;
					isSuccessful = true;
				} catch (Exception e) {
					e.printStackTrace();
					_logger.info(String.format(
							"Error trying to reinitialize page object with parent Container Locator and super container. Error is %s",
							e.getMessage()));
				}
			}
		} else {
			try {
				((WebElement) component).getTagName();
				PageFactory.initElements(field -> {
					return new DefaultElementLocator(component, field);
				}, this);
				this.superContainer = null;
				this.parentContainerLocator = null;
				isSuccessful = true;
			} catch (Exception e) {
				_logger.info(String.format(
						"Error trying to reinitialize page object with parent Container. Error is %s", e.getMessage()));
			}
		}

		if (isSuccessful)
			_logger.info("Reinitialized page object");
		return isSuccessful;
	}

	/**
	 * Initialize page objects using {@link WebDriver}
	 */
	public PageObject() {
		By parentContainerLocator = By.tagName("body");
		this.driver = BaseTest.getTestObject().getDriverManager().getActiveDriver().getWebDriver();
		PageFactory.initElements(field -> {
			return new DefaultElementLocator(driver.findElement(parentContainerLocator), field);
		}, this);
		setExplicitWaitInSeconds((int) BaseTest.getTestObject().getTimeoutInSeconds());
		this.parentContainerLocator = parentContainerLocator;
		this.component = driver.findElement(parentContainerLocator);
		this.superContainer = null;
	}

	/**
	 * Initialize page objects using the provided parent container XPATH. When
	 * invoking this constructor, the provided container should be visible in the
	 * Page
	 * 
	 * @param parentContainerLocator {@link By} identifier to the parent container
	 *                               element
	 */
	public PageObject(By parentContainerLocator) {
		this.driver = BaseTest.getTestObject().getDriverManager().getActiveDriver().getWebDriver();
		PageFactory.initElements(field -> {
			return new DefaultElementLocator(driver.findElement(parentContainerLocator), field);
		}, this);
		setExplicitWaitInSeconds((int) BaseTest.getTestObject().getTimeoutInSeconds());
		this.parentContainerLocator = parentContainerLocator;
		this.component = driver.findElement(parentContainerLocator);
		this.superContainer = null;
	}

	/**
	 * Initialize page objects using the provided parent container element. When
	 * invoking this constructor, the provided container should be visible in the
	 * Page
	 * 
	 * @param parentContainer Parent Container element
	 */
	public PageObject(WebElement parentContainer) {
		this.driver = BaseTest.getTestObject().getDriverManager().getActiveDriver().getWebDriver();
		PageFactory.initElements(field -> {
			return new DefaultElementLocator(parentContainer, field);
		}, this);
		setExplicitWaitInSeconds((int) BaseTest.getTestObject().getTimeoutInSeconds());
		this.parentContainerLocator = null;
		this.component = parentContainer;
		this.superContainer = null;
	}

	/**
	 * Initialize page objects using the provided parent container identifier. When
	 * invoking this constructor, the provided page container should be visible in
	 * the Page
	 * 
	 * @param superContainer         Super Parent Container
	 * @param parentContainerLocator {@link By} identifier to the parent container
	 *                               element
	 */
	public PageObject(WebElement superContainer, By parentContainerLocator) {
		this.driver = BaseTest.getTestObject().getDriverManager().getActiveDriver().getWebDriver();
		PageFactory.initElements(field -> {
			return new DefaultElementLocator(superContainer.findElement(parentContainerLocator), field);
		}, this);
		setExplicitWaitInSeconds((int) BaseTest.getTestObject().getTimeoutInSeconds());
		this.parentContainerLocator = parentContainerLocator;
		this.component = superContainer.findElement(parentContainerLocator);
		this.superContainer = superContainer;
	}

	/**
	 * Initialize the specified page class and return object
	 * 
	 * @param <T>  Generic Type of Type {@link IPageObject}
	 * @param page Page class to be initialized
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

	// JavaScript Executor ------------------------------------------

	/**
	 * Get {@link JavascriptExecutor} object
	 * 
	 * @return {@link JavascriptExecutor}
	 */
	protected JavascriptExecutor getJavascriptExecutor() {
		return ((JavascriptExecutor) driver);
	}

	/**
	 * Execute provided JavaScript command
	 * 
	 * @param command  JavaScript
	 * @param elements {@link WebElement}s to be used JavaScript
	 */
	@SuppressWarnings("all")
	protected void executeJavascript(String command, WebElement... elements) {
		getJavascriptExecutor().executeScript(command, elements);
	}

	/**
	 * Perform JavaScript click
	 * 
	 * @param element {@link WebElement} on which click has to be performed
	 */
	@com.automacent.fwk.annotations.Action
	protected void javascriptClick(WebElement element) {
		executeJavascript("arguments[0].click()", element);
	}

	/**
	 * Scroll element to view using scrollIntoView() JavaScript function
	 * 
	 * @param element {@link WebElement} to scroll into view
	 */
	@com.automacent.fwk.annotations.Action
	protected void scrollElementToViewUsingJs(WebElement element) {
		executeJavascript("arguments[0].scrollIntoView(true)", element);
	}

	/**
	 * Clear a {@link WebElement} using JavaScript
	 * 
	 * @param element {@link WebElement}
	 */
	@com.automacent.fwk.annotations.Action
	protected void javascriptClearField(WebElement element) {
		executeJavascript("arguments[0].value = ''", element);
	}

	/**
	 * Send keys to {@link WebElement} using JavaScript
	 * 
	 * @param element The {@link WebElement} to which keys have to be sent
	 * @param keys    Keys to be sent
	 */
	@com.automacent.fwk.annotations.Action
	protected void javascriptSendKeys(WebElement element, String keys) {
		executeJavascript(String.format("arguments[0].value = '%s'", keys), element);
	}

	// Mouse Action -------------------------------------------------

	/**
	 * Get {@link Actions} instance
	 * 
	 * @return {@link Actions} object
	 */
	protected Actions mouse() {
		return new Actions(driver);
	}

	/**
	 * Execute mouse {@link Actions}
	 * 
	 * @param actions The sequence of {@link Actions} to be performed
	 */
	public void performMouseAction(Actions actions) {
		actions.build().perform();
	}

	/**
	 * Scroll element to view using selenium {@link Action} (mouse)
	 * 
	 * @param element {@link WebElement} to scroll into view
	 */
	@com.automacent.fwk.annotations.Action
	protected void scrollElementToViewUsingMouse(WebElement element) {
		performMouseAction(mouse().moveToElement(element));
	}

	/**
	 * Move mouse relative to an element and click on the location. Offset should be
	 * in pixels.
	 * 
	 * @param element Element against which we have to move
	 * @param x       x-axis to move
	 * @param y       y-axis to move
	 */
	@com.automacent.fwk.annotations.Action
	protected void moveRelativeToElementAndClick(WebElement element, int x, int y) {
		Actions actions = new Actions(driver);
		actions.moveToElement(element).moveByOffset(x, y);
		actions.perform();
		actions.click();

		Action action = actions.build();
		action.perform();
	}

	// Explicit wait ------------------------------------------------

	/**
	 * Get the {@link WebDriverWait} (explicit wait) object with the set timeout in
	 * seconds value
	 * 
	 * @param explicitWaitInSeconds Explicit wait timeout
	 * @return {@link WebDriverWait} object
	 */
	protected WebDriverWait explicitWait(int explicitWaitInSeconds) {
		return new WebDriverWait(driver, explicitWaitInSeconds);
	}

	/**
	 * Get the {@link WebDriverWait} (explicit wait) object
	 * 
	 * @return {@link WebDriverWait} object
	 */
	protected WebDriverWait explicitWait() {
		return new WebDriverWait(driver, getExplicitWaitInSeconds());
	}

	/**
	 * This method has to be implemented by all the Pages inheriting
	 * {@link PageObject}. It can be implemented in any of ways as shown in the
	 * below examples
	 * 
	 * <pre>
	 * &#64;Override
	 * public PageValidation pageValidation() {
	 * 	return new PageValidation() {
	 * 
	 * 		&#64;Override
	 * 		public void validate() {
	 * 			validateLoginWithPage();
	 * 		}
	 * 
	 * 		&#64;Step
	 * 		private void validateLoginPage() {
	 * 			Assert.assertTrue(isUsernameFieldFound(), "Username field is found");
	 * 			Assert.assertTrue(isUsernameFieldFound(), "Username field is found");
	 * 		}
	 * 	};
	 * }
	 * 
	 * &#64;Override
	 * public PageValidation pageValidation() {
	 * 	return new PageValidation() {
	 * 
	 * 		&#64;Override
	 * 		public void validate(String... parameters) {
	 * 			validateLoginWithPage(parameters[0]);
	 * 		}
	 * 
	 * 		&#64;Step
	 * 		private void validateLoginPage(String companyName) {
	 * 			Assert.assertEquals(getCompanyName(), companyName, "");
	 * 		}
	 * 	};
	 * }
	 * </pre>
	 * 
	 * @return {@link PageValidation}
	 */
	public abstract PageValidation pageValidation();

	public abstract class PageValidation {
		protected String title;

		public PageValidation() {
		}

		public PageValidation(String title) {
			this.title = title;
		}

		public abstract void validate();

		public void validate(String... parameters) {

		};
	}
}