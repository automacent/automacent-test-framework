package com.automacent.fwk.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Util class containing helper methods for manipulating AspectJ components
 * 
 * @author sighil.sivadas
 */
public class AspectJUtils {

	/**
	 * Get method name with arguments from the {@link ProceedingJoinPoint}
	 * 
	 * @param point {@link ProceedingJoinPoint}
	 * @return method name with arguments in the format methodName(arg1_value,
	 *         arg2_value ...)
	 */
	public static String getMethodNameWithArguments(ProceedingJoinPoint point) {
		return LoggingUtils.addGrammer(LoggingUtils.addSpaceToCamelCaseString(
				MethodSignature.class.cast(point.getSignature()).getMethod().getName() + getArguments(point)));
	}

	/**
	 * Get arguments/parameters from a method
	 * 
	 * @param point {@link ProceedingJoinPoint}
	 * @return Arguments in the format (arg1_value, arg2_value ...)
	 */
	public static String getArguments(ProceedingJoinPoint point) {
		int count = 0;
		Object[] signatureArgs = point.getArgs();
		String arguments = " (";
		for (Object signatureArg : signatureArgs) {
			if (++count > 1)
				arguments += ", ";
			if (signatureArg instanceof WebElement)
				arguments += "webelement";
			else if (signatureArg instanceof WebDriver)
				arguments += "webdriver";
			else if (signatureArg instanceof String)
				arguments += ((String) signatureArg).length() > 50
						? String.format("%s...", ((String) signatureArg).substring(0, 49))
						: signatureArg;
			else
				arguments += signatureArg;
		}
		arguments += ")";
		return count == 0 ? "" : arguments;
	}
}
