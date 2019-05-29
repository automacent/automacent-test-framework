package com.automacent.fwk.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Util class related to date formatting
 * 
 * @author sighil.sivadas
 */
public class DateUtils {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss,SSS");

	/**
	 * Get date in the date format yyyy-MMM-dd HH:mm:ss,SSS
	 * 
	 * @return String representing date
	 */
	public static String getDate() {
		return sdf.format(new Date());
	}
}
