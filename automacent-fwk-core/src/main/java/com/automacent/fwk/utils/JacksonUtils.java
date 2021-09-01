package com.automacent.fwk.utils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.automacent.fwk.reporting.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utils class for Jackson JSON manipulations
 * 
 * @author sighil.sivadas
 */
public class JacksonUtils {

	private static final Logger _logger = Logger.getLogger(JacksonUtils.class);

	private static ObjectMapper mapper = new ObjectMapper();

	/**
	 * Convert String to JSON pretty print
	 * 
	 * @param jsonString JSON String to be converted
	 * @return Pretty String
	 */
	public static String getPrettyString(String jsonString) {
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonString);
		} catch (JsonProcessingException e) {
			_logger.warn(String.format("Error parsing Json String as pretty %s ", jsonString), e);
			return "";
		}
	}

	/**
	 * Convert {@link JsonNode} to String
	 * 
	 * @param jsonNode {@link JsonNode} to be converted
	 * @return JSON String
	 */
	public static String getString(JsonNode jsonNode) {
		try {
			return mapper.writeValueAsString(jsonNode);
		} catch (JsonProcessingException e) {
			_logger.warn(String.format("Error parsing Json Node as String %s ", jsonNode), e);
			return "";
		}
	}

	/**
	 * Convert {@link Map}&lt;String, Object&gt; to String
	 * 
	 * @param jsonMap {@link Map}&lt;String, Object&gt; to be converted
	 * @return JSON String
	 */
	public static String getString(Map<String, Object> jsonMap) {
		return jsonMap.toString();
	}

	/**
	 * 
	 * Convert JSON String to {@link LinkedHashMap}&lt;String, Object&gt;
	 * 
	 * @param jsonString JSON String to be converted
	 * @return {@link LinkedHashMap}&lt;String, Object&gt;
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getJsonMap(String jsonString) {
		try {
			return (LinkedHashMap<String, Object>) mapper.readValue(jsonString, LinkedHashMap.class);
		} catch (IOException e) {
			_logger.warn(String.format("Error parsing String as JSON Node %s ", jsonString), e);
			return null;
		}
	}

	/**
	 * Convert {@link JsonNode} to {@link LinkedHashMap}&lt;String, Object&gt;
	 * 
	 * @param jsonNode {@link JsonNode} to be converted
	 * @return {@link LinkedHashMap}&lt;String, Object&gt;
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getJsonMap(JsonNode jsonNode) {
		return (LinkedHashMap<String, Object>) mapper.convertValue(jsonNode, Map.class);
	}

	/**
	 * Convert JSON String to {@link JsonNode}
	 * 
	 * @param jsonString JSON String to be converted
	 * @return {@link JsonNode}
	 */
	public static JsonNode getJsonNode(String jsonString) {
		try {
			return mapper.readTree(jsonString);
		} catch (IOException e) {
			_logger.warn(String.format("Error parsing String as JSON Node %s ", jsonString), e);
			return null;
		}
	}

	/**
	 * Convert {@link Map}&lt;String, Object&gt; to {@link JsonNode}
	 * 
	 * @param jsonMap {@link Map}&lt;String, Object&gt; to be converted
	 * @return {@link JsonNode}
	 */
	public static JsonNode getJsonNode(Map<String, Object> jsonMap) {
		return mapper.valueToTree(jsonMap);
	}
}