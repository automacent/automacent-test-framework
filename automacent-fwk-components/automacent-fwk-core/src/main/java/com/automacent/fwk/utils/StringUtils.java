package com.automacent.fwk.utils;

/**
 * String utility class
 * 
 * @author sighil.sivadas
 */
public class StringUtils {

	/**
	 * Get the corresponding ENUM value from a String value
	 * 
	 * @param <T>       Generic class of type Enum
	 * @param enumClass ENUM class
	 * @param string    String value for which corresponding ENUM values has to be
	 *                  found
	 * @return ENUM value
	 */
	public static <T extends Enum<T>> T getEnumFromString(Class<T> enumClass, String string) {
		if (enumClass != null && string != null) {
			try {
				return Enum.valueOf(enumClass, string.trim().toUpperCase());
			} catch (IllegalArgumentException ex) {
			}
		}
		return null;
	}
}
