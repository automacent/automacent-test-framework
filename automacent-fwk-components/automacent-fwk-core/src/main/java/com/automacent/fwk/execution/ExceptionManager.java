package com.automacent.fwk.execution;

import java.net.SocketTimeoutException;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.UnreachableBrowserException;

import com.automacent.fwk.annotations.Action;
import com.automacent.fwk.exceptions.LauncherForceCompletedException;
import com.automacent.fwk.exceptions.TestDurationExceededException;
import com.automacent.fwk.reporting.Logger;
import com.automacent.fwk.utils.ThreadUtils;

/**
 * This class manages failure execptions and maps the control flow for each type
 * of exception. With the control flow, framework will decide on the exeution
 * flow on failures
 * 
 * @author sighil.sivadas
 */
public class ExceptionManager {

	private static final Logger _logger = Logger.getLogger(ExceptionManager.class);

	private ExceptionManager() {
	}

	/**
	 * Check if the error qualifies for {@link Action} retry
	 * 
	 * @param e
	 *            {@link Throwable}
	 * @return True if retry required
	 */
	public static boolean shouldPerformActionRetry(Throwable ee) {
		boolean isRetry = false;
		while (ee != null) {
			if (ee instanceof StaleElementReferenceException) {
				_logger.info("Stale Element Reference Exception occured");
				isRetry = true;
				break;
			} else if (ee instanceof WebDriverException && ee.getMessage().contains("is not clickable at point")) {
				_logger.info("Element is not clickable at the given point");
				isRetry = true;
				break;
			}
			ee = ee.getCause();
		}
		if (isRetry)
			ThreadUtils.sleepFor(10);
		return isRetry;
	}

	/**
	 * Check if Exception cause is {@link StaleElementReferenceException}
	 * 
	 * @param e
	 *            {@link Throwable}
	 * @return True if given Throwable is a type of StaleElementReferenceException
	 */
	public static boolean isStaleElementReferenceException(Throwable e) {
		boolean isStale = false;
		while (e != null) {
			if (e instanceof StaleElementReferenceException) {
				isStale = true;
				break;
			}
			e = e.getCause();
		}
		return isStale;
	}

	/**
	 * Check if Exception comes under the category of different manifestations of
	 * {@link SocketTimeoutException}. When these Exceptions occur,
	 * {@link WebDriver} might freeze and not allow further execution
	 * 
	 * @param e
	 *            {@link Throwable}
	 * @return True if given Throwable is a type of SocketTimeoutException
	 */
	public static boolean isExceptionSocketTimeout(Throwable e) {
		boolean isSocketTimeout = false;
		while (e != null) {
			if (e instanceof UnreachableBrowserException
					&& e.getMessage().toLowerCase()
							.contains("Error communicating with the remote browser. It may have died.".toLowerCase())) {
				_logger.info("Error communicating with the remote browser occured");
				isSocketTimeout = true;
				break;
			} else if (e instanceof TimeoutException
					&& e.getMessage().toLowerCase()
							.contains("Timed out receiving message from renderer".toLowerCase())) {
				_logger.info("Exception timed out receiving message from renderer occured");
				isSocketTimeout = true;
				break;
			} else if (e instanceof WebDriverException
					&& e.getMessage().toLowerCase().contains("Read timed out".toLowerCase())) {
				_logger.info("Java socket timeout exception occured");
				isSocketTimeout = true;
				break;
			}
			e = e.getCause();
		}

		if (isSocketTimeout)
			ThreadUtils.sleepFor(10);

		return isSocketTimeout;
	}

	/**
	 * Check if Exception cause is {@link TestDurationExceededException}
	 * 
	 * @param e
	 *            {@link Throwable}
	 * @return True if given Throwable is a type of TestDurationExceededException
	 */
	public static boolean isExceptionTestDurationExceededException(Throwable e) {
		boolean isTestDurationExceeded = false;
		while (e != null) {
			if (e instanceof TestDurationExceededException) {
				isTestDurationExceeded = true;
				break;
			}
			e = e.getCause();
		}
		return isTestDurationExceeded;
	}

	/**
	 * Check if Exception cause is {@link LauncherForceCompletedException}
	 * 
	 * @param e
	 *            {@link Throwable}
	 * @return True if given Throwable is a type of LauncherForceCompletedException
	 */
	public static boolean isExceptionLauncherForceCompletedException(Throwable e) {
		boolean isLauncherForceCompleted = false;
		while (e != null) {
			if (e instanceof LauncherForceCompletedException) {
				isLauncherForceCompleted = true;
				break;
			}
			e = e.getCause();
		}
		return isLauncherForceCompleted;
	}

	/**
	 * Check if the method under execution is a test
	 * 
	 * @return True if method is a test
	 */
	public static boolean isMethodUnderExecutionATest() {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		for (StackTraceElement stackTraceElement : stackTraceElements) {
			String methodName = stackTraceElement.getMethodName();
			if (methodName.contains("aroundTestCompilerAspect"))
				return true;
		}
		return false;
	}
}
