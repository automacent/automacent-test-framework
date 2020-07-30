package com.automacent.fwk.utils;

import java.util.Base64;

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

	/**
	 * Encode String using Base64
	 * 
	 * @param plainString String to be encoded
	 * @return Encoded string
	 */
	public static String encodeStringToBase64(String plainString) {
		return Base64.getEncoder()
				.encodeToString(plainString.getBytes());
	}

	/**
	 * Decode Base64 encoded string
	 * 
	 * @param encodedString String to be decoded
	 * @return Decoded String
	 */
	public static String decodeBase64String(String encodedString) {
		return new String(Base64.getDecoder().decode(encodedString));
	}
}
