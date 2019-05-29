package com.automacent.fwk.utils;

import java.util.concurrent.TimeUnit;

import com.automacent.fwk.reporting.Logger;

/**
 * Thread utility class
 * 
 * @author sighil.sivadas
 *
 */
public class ThreadUtils {
	private static final Logger _logger = Logger.getLogger(ThreadUtils.class);

	public static Long getThreadId() {
		return Thread.currentThread().getId();
	}

	/**
	 * When called execution will sleep for the specified time
	 * 
	 * @param sleepTimeInSeconds
	 */
	public static void sleepFor(int sleepTimeInSeconds) {
		try {
			if (sleepTimeInSeconds > 30)
				_logger.info("Sleeping for " + sleepTimeInSeconds + " seconds");
			_logger.debug("Sleeping for " + sleepTimeInSeconds + " seconds");
			Thread.sleep(TimeUnit.SECONDS.toMillis(sleepTimeInSeconds));
		} catch (InterruptedException e) {
			_logger.warn("Thread.sleep interuppted", e);
		}
	}
}
