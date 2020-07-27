package com.automacent.fwk.utils;

import static com.automacent.fwk.enums.LogType.HTML;
import static com.automacent.fwk.enums.LogType.TEXT;

import com.automacent.fwk.annotations.Action;
import com.automacent.fwk.annotations.Step;
import com.automacent.fwk.enums.LogType;

/**
 * Utils class for manipulating logs and log statements
 * 
 * @author sighil.sivadas
 */
public class LoggingUtils {
	/**
	 * Get the nesting level of logs. If test failed on {@link Action}, it checks
	 * how nested the {@link Action} call is i.e., how many {@link Step} calls are
	 * made before it in the execution stack.
	 * 
	 * @return nesting level * 4
	 */
	public static int getNestingLevelOfLogs() {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		int nestingLevel = 0;
		for (StackTraceElement stackTraceElement : stackTraceElements) {
			String methodName = stackTraceElement.getMethodName();
			if (methodName.contains("aroundBody") || methodName.contains("aroundActionCompilerAspect")
					|| methodName.contains("aroundStepCompilerAspect")
					|| methodName.contains("aroundBeforeCompilerAspect")
					|| methodName.contains("aroundAfterCompilerAspect")
					|| methodName.contains("aroundTestCompilerAspect"))
				nestingLevel += 2;
		}
		return nestingLevel;
	}

	/**
	 * Returns space specified as &nbsp; for HTML and " " for TEXT logs
	 * 
	 * @param nestingLevel Number of spaces
	 * @param logType      {@link LogType}
	 * @return space
	 */
	public static String getSpaceForNestingLevel(int nestingLevel, LogType logType) {
		String space = "";
		while (nestingLevel-- > 0) {
			if (logType == HTML)
				space += "&nbsp;";
			else if (logType == TEXT)
				space += " ";
		}
		return space;
	}

	/**
	 * Add space between camel cased string
	 * 
	 * @param source camel cased string
	 * @return String with spaces
	 */
	public static String addSpaceToCamelCaseString(String source) {
		return source.replaceAll(String.format("%s", "(?=[A-Z])"), " ").toLowerCase();
	}

	/**
	 * Add question mark to string which is a question/query
	 * 
	 * @param source String to be formatted
	 * @return Formatted string
	 */
	public static String addGrammer(String source) {
		if (source.startsWith("is"))
			return source + "?";
		return source;
	}
}
