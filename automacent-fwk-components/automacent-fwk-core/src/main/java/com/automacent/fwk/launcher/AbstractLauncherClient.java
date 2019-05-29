package com.automacent.fwk.launcher;

import java.util.Arrays;
import java.util.LinkedHashMap;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.automacent.fwk.reporting.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * Rest client for updating results to Launcher DB. All Launcher clients
 * intenting to update results to Launcher DB must extend this Class.
 * 
 * @author sighil.sivadas
 *
 */
public abstract class AbstractLauncherClient implements ILauncherClient {

	private static final Logger _logger = Logger.getLogger(AbstractLauncherClient.class);

	protected String host;
	private String username;
	private String password;

	public AbstractLauncherClient(String host, String username, String password) {
		this.host = host;
		this.username = username;
		this.password = password;
	}

	private boolean enabled = false;

	/**
	 * Checks if the {@link AbstractLauncherClient} is enabled.
	 * 
	 * @return true if enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Enable {@link AbstractLauncherClient}
	 */
	@Override
	public void enableClient() {
		enabled = true;
		_logger.debug(String.format("%s client enabled", this.getClass().getName()));
	}

	/**
	 * Disable {@link AbstractLauncherClient}
	 */
	@Override
	public void disableClient() {
		enabled = false;
		_logger.debug(String.format("%s client disabled", this.getClass().getName()));
	}

	private final String PASSWORD_GRANT_URL = "http://%s/oauth/token?grant_type=password&username=%s&password=%s";
	protected final String URL = "http://%s/%s?access_token=%s";

	/**
	 * Get OAUTH2 password grand URL
	 * 
	 * @return Password Grand URL
	 */
	public String getPasswordGrantURL() {
		return String.format(PASSWORD_GRANT_URL, host, username, password);
	}

	/*
	 * Prepare HTTP Headers.
	 */
	protected HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		return headers;
	}

	/**
	 * Add HTTP Authorization header, using Basic-Authentication to send
	 * client-credentials.
	 *
	 * @return {@link HttpHeaders} with Basic authentication
	 */
	protected HttpHeaders getHeadersWithClientCredentials() {
		String plainClientCredentials = "my-trusted-client:secret";
		String base64ClientCredentials = new String(
				java.util.Base64.getEncoder().encode(plainClientCredentials.getBytes()));
		HttpHeaders headers = getHeaders();
		headers.add("Authorization", "Basic " + base64ClientCredentials);
		return headers;
	}

	/**
	 * Send a POST request using password grant URL to get an access-token, which
	 * will then be send with each request.
	 * 
	 * @return Token object
	 */
	@SuppressWarnings({ "unchecked" })
	protected ObjectNode getToken() {
		RestTemplate restTemplate = new RestTemplate();

		HttpEntity<String> request = new HttpEntity<String>(getHeadersWithClientCredentials());
		ResponseEntity<Object> response = restTemplate.exchange(getPasswordGrantURL(), HttpMethod.POST, request,
				Object.class);
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) response.getBody();

		if (map != null) {
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode tokenInfo = mapper.createObjectNode();

			tokenInfo.put("accessToken", (String) map.get("access_token"));
			tokenInfo.put("tokenType", (String) map.get("token_type"));
			tokenInfo.put("refreshToken", (String) map.get("refresh_token"));
			tokenInfo.put("expiresIn", (Integer) map.get("expires_in"));
			tokenInfo.put("scope", (String) map.get("scope"));
			try {
				_logger.debug(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tokenInfo));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return tokenInfo;
		} else {
			_logger.error("No user exist");
		}
		return null;
	}

	private long testInstanceId;

	/**
	 * Get Test instance Id
	 * 
	 * @return Test instance Id
	 */
	public long getTestInstanceId() {
		return testInstanceId;
	}

	/**
	 * Set Test instance id
	 * 
	 * @param testInstanceId
	 */
	protected void setTestInstanceId(long testInstanceId) {
		this.testInstanceId = testInstanceId;
	}
}