package com.automacent.fwk.utils;

/**
 * Enum utility class
 * 
 * @author sighil.sivadas
 */
public class EnumUtils {

	/**
	 * Get the corresponding ENUM value from a String value
	 * 
	 * @param <T>       Generic class of type Enum
	 * @param enumClass ENUM class
	 * @param name      String value for which corresponding ENUM values has to be
	 *                  found
	 * @return ENUM value
	 */
	public static <T extends Enum<T>> T getEnumFromString(Class<T> enumClass, String name) {
		if (enumClass != null && name != null) {
			try {
				return Enum.valueOf(enumClass, name.trim());
			} catch (IllegalArgumentException ex) {
			}
		}
		return null;
	}

}