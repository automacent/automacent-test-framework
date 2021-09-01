package com.automacent.fwk.utils;

import java.util.Date;

import com.automacent.fwk.enums.MethodType;
import com.automacent.fwk.enums.TestStatus;
import com.automacent.fwk.exceptions.LauncherForceCompletedException;
import com.automacent.fwk.execution.IterationManager;
import com.automacent.fwk.launcher.LauncherClientManager;
import com.automacent.fwk.launcher.LauncherHeartBeat;
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
	 * Get the current thread name
	 * 
	 * @return current thread name
	 */
	public static String getThreadName() {
		return Thread.currentThread().getName();
	}

	/**
	 * When called execution will sleep for the specified time
	 * 
	 * @param sleepTimeInSeconds Sleep time in seconds
	 */
	public static void sleepFor(int sleepTimeInSeconds) {
		long startTime = new Date().getTime();
		LauncherClientManager.getManager().logStart("sleep", MethodType.SLEEP);
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
				LauncherHeartBeat.getManager().ping();
			} while ((currentSleepTime = currentSleepTime - 30) > 0);
			LauncherClientManager.getManager().logEnd("sleep", MethodType.SLEEP, TestStatus.PASS,
					new Date().getTime() - startTime, null);
		} catch (InterruptedException e) {
			LauncherClientManager.getManager().logEnd("sleep", MethodType.SLEEP, TestStatus.FAIL,
					new Date().getTime() - startTime, e);
			_logger.warn("Thread.sleep interuppted", e);
		} catch (LauncherForceCompletedException e) {
			throw e;
		} catch (Exception e) {
			LauncherClientManager.getManager().logEnd("sleep", MethodType.SLEEP, TestStatus.FAIL,
					new Date().getTime() - startTime, e);
		}
	}

}
