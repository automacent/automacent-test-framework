package com.automacent.fwk.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.automacent.fwk.execution.TestExecutor;
import com.automacent.fwk.reporting.Logger;

/**
 * Utility class related to date formatting
 * 
 * @author sighil.sivadas
 */
public class DateUtils {

	private static final Logger _logger = Logger.getLogger(DateUtils.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss,SSS");

	private static final SimpleDateFormat suiteNameSdf = new SimpleDateFormat("yyyy-MMM-dd-HH-mm-ss");

	private static final SimpleDateFormat cookieExpirySdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

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

	/**
	 * Parse input Cookie Expiry date String in format yyyy-MM-dd'T'HH:mm:ss.SSSZ to
	 * Date
	 * 
	 * @param cookieExpiry Cookie Expiry Date String
	 * @return {@link Date}
	 */
	public static Date parseCookieExpiryDate(String cookieExpiry) {
		try {
			return cookieExpirySdf.parse(cookieExpiry);
		} catch (ParseException e) {
			_logger.warn("Error in parsing cookie expiry date", e);
		}
		return null;
	}

}
