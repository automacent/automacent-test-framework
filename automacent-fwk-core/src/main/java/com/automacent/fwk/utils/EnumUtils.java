package com.automacent.fwk.utils;

import java.util.Arrays;

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

	/**
	 * Get the {@link Enum} names as Array of String
	 * 
	 * @param enumClass {@link Enum} class
	 * @return String[] of {@link Enum} names
	 */
	public static String[] getEnumNames(Class<? extends Enum<?>> enumClass) {
		return enumClass == null ? new String[] {}
				: Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}

}