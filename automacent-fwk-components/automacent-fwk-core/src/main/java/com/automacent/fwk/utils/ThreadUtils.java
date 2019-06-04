package com.automacent.fwk.utils;

import com.automacent.fwk.execution.IterationManager;
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
		int currentSleepTime = sleepTimeInSeconds;
		if (currentSleepTime > 10)
			_logger.info(String.format("Sleeping for %s seconds", sleepTimeInSeconds));

		try {
			do {
				int scanInterval = currentSleepTime < 30 ? currentSleepTime : 30;
				_logger.debug(String.format("Sleeping for %s seconds of %s seconds remaining", scanInterval,
						currentSleepTime));
				Thread.sleep(scanInterval * 1000);
				IterationManager.getManager().checkIfTestDurationExceeded();
			} while ((currentSleepTime = currentSleepTime - 30) > 0);
		} catch (InterruptedException e) {
			_logger.warn("Thread.sleep interuppted", e);
		}
	}
}
