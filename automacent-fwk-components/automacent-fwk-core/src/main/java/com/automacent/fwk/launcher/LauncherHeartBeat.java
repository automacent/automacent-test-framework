package com.automacent.fwk.launcher;

import java.util.Date;
import java.util.HashMap;

import com.automacent.fwk.exceptions.LauncherForceCompletedException;
import com.automacent.fwk.reporting.Logger;
import com.automacent.fwk.utils.ThreadUtils;

/**
 * This class manages sending of heart beat to the launcher server
 * 
 * @author sighil.sivadas
 */
public class LauncherHeartBeat {
	private static final Logger _logger = Logger.getLogger(LauncherHeartBeat.class);

	protected LauncherHeartBeat() {
		pingCounter = new Date().getTime();
	}

	boolean isTestInstanceStopped = false;

	private static HashMap<Long, LauncherHeartBeat> heartBeatMap = new HashMap<Long, LauncherHeartBeat>();

	/**
	 * Get the {@link LauncherHeartBeat} instance for the test.
	 * 
	 * @return {@link LauncherHeartBeat}
	 */
	public static LauncherHeartBeat getManager() {
		if (!heartBeatMap.containsKey(ThreadUtils.getThreadId()))
			heartBeatMap.put(ThreadUtils.getThreadId(), new LauncherHeartBeat());
		return heartBeatMap.get(ThreadUtils.getThreadId());
	}

	private long pingCounter;

	/**
	 * Send heart beat to the launcher server
	 *
	 * @throws LauncherForceCompletedException
	 *             when test instance status is not RUNNING
	 */
	public void ping() throws LauncherForceCompletedException {
		if (isTestInstanceStopped)
			return;
		if (new Date().getTime() - pingCounter > 60 * 2 * 1000) {
			pingCounter = new Date().getTime();
			_logger.debug("Sending heart beat to launcher");
			try {
				LauncherClientManager.getManager().ping();
			} catch (LauncherForceCompletedException e) {
				isTestInstanceStopped = true;
				throw e;
			}
		}

	}
}
