package com.automacent.fwk.rest;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.automacent.fwk.utils.JacksonUtils;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Custom Object holding response obtained by REST API calls. The class provides
 * responses in the following formats
 * <ul>
 * <li>String</li>
 * <li>{@link LinkedHashMap}&lt;String, Object&gt;</li>
 * <li>{@link JsonNode}</li>
 * </ul>
 * 
 * @author sighil.sivadas
 *
 */
public class Response {
	private Map<String, Object> responseBodyAsMap = new LinkedHashMap<>();
	private String responseBodyAsString;
	private JsonNode responseBodyAsJsonNode;
	private HttpStatus status;

	public Response(Map<String, Object> jsonMap, HttpStatus status) {
		super();
		this.responseBodyAsMap = jsonMap;
		this.responseBodyAsString = JacksonUtils.getString(jsonMap);
		this.responseBodyAsJsonNode = JacksonUtils.getJsonNode(jsonMap);
		this.status = status;
	}

	public Response(String jsonString, HttpStatus status) {
		super();
		this.responseBodyAsString = jsonString;
		this.responseBodyAsMap = JacksonUtils.getJsonMap(jsonString);
		this.responseBodyAsJsonNode = JacksonUtils.getJsonNode(jsonString);
		this.status = status;
	}

	public Response(JsonNode jsonNode, HttpStatus status) {
		super();
		this.responseBodyAsJsonNode = jsonNode;
		this.responseBodyAsMap = JacksonUtils.getJsonMap(jsonNode);
		this.responseBodyAsString = JacksonUtils.getString(jsonNode);
		this.status = status;
	}

	/**
	 * Get response as {@link LinkedHashMap}&lt;String, Object&gt;
	 * 
	 * @return {@link LinkedHashMap}&lt;String, Object&gt;
	 */
	public Map<String, Object> getResponseBodyAsMap() {
		return responseBodyAsMap;
	}

	/**
	 * Get response as JSON String
	 * 
	 * @return JSON String
	 */
	public String getResponseBodyAsString() {
		return responseBodyAsString;
	}

	/**
	 * Get response as {@link JsonNode}
	 * 
	 * @return {@link JsonNode}
	 */
	public JsonNode getResponseBodyAsJsonNode() {
		return responseBodyAsJsonNode;
	}

	/**
	 * Get {@link HttpStatus}
	 * 
	 * @return {@link HttpStatus}
	 */
	public HttpStatus getStatus() {
		return status;
	}
}
