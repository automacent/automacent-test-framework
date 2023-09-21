package com.automacent.fwk.launcher;

import java.util.Arrays;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.automacent.fwk.reporting.Logger;

/**
 * 
 * Rest client for updating results to Launcher DB. All Launcher clients
 * Intending to update results to Launcher DB must extend this Class.
 * 
 * @author sighil.sivadas
 *
 */
public abstract class AbstractLauncherClient implements ILauncherClient {

	private static final Logger _logger = Logger.getLogger(AbstractLauncherClient.class);

	public AbstractLauncherClient() {
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

	/*
	 * Prepare HTTP Headers.
	 */
	protected HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		return headers;
	}

	private long testInstanceId = 0l;

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
	 * @param testInstanceId Test instance id from logger application
	 */
	protected void setTestInstanceId(String testInstanceId) {
		try {
			this.testInstanceId = Long.parseLong(testInstanceId);
		} catch (Exception e) {
		}
	}
}