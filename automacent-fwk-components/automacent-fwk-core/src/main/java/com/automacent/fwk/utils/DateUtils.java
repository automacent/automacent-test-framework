package com.automacent.fwk.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.automacent.fwk.execution.TestExecutor;

/**
 * Util class related to date formatting
 * 
 * @author sighil.sivadas
 */
public class DateUtils {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss,SSS");

	private static final SimpleDateFormat suiteNameSdf = new SimpleDateFormat("yyyy-MMM-dd-HH-mm-ss");

	/**
	 * Get date in the date format yyyy-MMM-dd HH:mm:ss,SSS
	 * 
	 * @return String representing date
	 */
	public static String getDate() {
		return sdf.format(new Date());
	}

	/**
	 * Get date in the date format yyyy-MMM-ddTHH:mm:ss for setting up the suite
	 * name in {@link TestExecutor}
	 * 
	 * @return String representing date
	 */
	public static String getDateFormattedForSuiteName() {
		return suiteNameSdf.format(new Date());
	}
}
