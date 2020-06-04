package com.automacent.fwk.execution;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.automacent.fwk.annotations.Repeat;
import com.automacent.fwk.core.BaseTest;
import com.automacent.fwk.enums.RepeatMode;
import com.automacent.fwk.exceptions.TestDurationExceededException;
import com.automacent.fwk.reporting.ExecutionLogManager;
import com.automacent.fwk.reporting.Logger;
import com.automacent.fwk.reporting.ReportingTools;
import com.automacent.fwk.utils.ThreadUtils;

/**
 * This class manages the iterations in of test methods annotated with the
 * {@link Repeat} annotation.
 * 
 * @author sighil.sivadas
 */
public class IterationManager {
	private static final Logger _logger = Logger.getLogger(IterationManager.class);

	protected IterationManager() {
		startTimeInMilliSeconds = new Date().getTime();
	}

	private static HashMap<Long, IterationManager> timeKeepersMap = new HashMap<Long, IterationManager>();

	/**
	 * Get the {@link IterationManager} instance for the test. Using the instance
	 * various time keeping operations can be carried out and the iterations can be
	 * tracked.
	 * 
	 * @return TimeKeeper
	 */
	public static IterationManager getManager() {
		if (!timeKeepersMap.containsKey(ThreadUtils.getThreadId()))
			timeKeepersMap.put(ThreadUtils.getThreadId(), new IterationManager());
		return timeKeepersMap.get(ThreadUtils.getThreadId());
	}

	private int iteration = 0;
	private long startTimeInMilliSeconds;
	private long elapsedTimeInMilliSeconds = 0;
	private boolean executeRecoveryScenarios = false;
	private Map<Integer, String> errorMap = new HashMap<>();

	/**
	 * Get the current iteration count
	 * 
	 * @return 0 if no iteration is run else iteration count
	 */
	public int getIteration() {
		return iteration;
	}

	/**
	 * Get the elapsed time recorded before the last iteration is executed
	 * 
	 * @return elapsed time
	 */
	public long getElapsedTimeInMilliSeconds() {
		return elapsedTimeInMilliSeconds == 0 ? new Date().getTime() - startTimeInMilliSeconds
				: elapsedTimeInMilliSeconds;
	}

	/**
	 * Used as a check point to check whether recovery scenarios has to be executed.
	 * 
	 * @return true if recovery scenarios has to be executed
	 */
	public boolean isExecuteRecoveryScenarios() {
		return executeRecoveryScenarios;
	}

	/**
	 * Set the flag to enable execution of recovery scenarios.
	 * 
	 * @param executeRecoveryScenarios
	 */
	public void setExecuteRecoveryScenarios(boolean executeRecoveryScenarios) {
		this.executeRecoveryScenarios = executeRecoveryScenarios;
	}

	/**
	 * Add Test failures in each iteration to a map. These errors will be output
	 * when all the iterations are completed Add {@link Throwable}
	 * 
	 * @param e
	 *            {@link Throwable}
	 */
	public void addError(Throwable e) {
		errorMap.put(iteration, e.getMessage());
	}

	/**
	 * Get the Error Map containing <Iteration number, Error>
	 * 
	 * @return Error map
	 */
	public Map<Integer, String> getErrorMap() {
		return errorMap;
	}

	/**
	 * Check if the current iteration is failed
	 * 
	 * @return True if iteration is failed
	 */
	public boolean isIterationFailed() {
		return isIterationFailed(getIteration());
	}

	/**
	 * Check if provided iteration is failed
	 * 
	 * @param iteration
	 *            iteration number
	 * @return True if iteration is failed
	 */
	public boolean isIterationFailed(int iteration) {
		return errorMap.containsKey(iteration);
	}

	/**
	 * Log the start of iteration to the test and increment iteration count
	 */
	public void startIteration() {
		long testDurationInMilliSeconds = TimeUnit.SECONDS
				.toMillis(BaseTest.getTestObject().getTestDurationInSeconds());
		elapsedTimeInMilliSeconds = new Date().getTime() - startTimeInMilliSeconds;
		++iteration;
		ExecutionLogManager.logIterationStart(iteration, elapsedTimeInMilliSeconds, testDurationInMilliSeconds);
	}

	/**
	 * Check if the test has to iterate again. This will depend on the Time
	 * Remaining in case of {@link RepeatMode#TEST_DURATION} and Count Remaining in
	 * case of {@link RepeatMode#INVOCATION_COUNT}
	 * 
	 * @return True if another ietartion is required
	 */
	public boolean isIterationRemaining() {
		RepeatMode repeatMode = BaseTest.getTestObject().getRepeatMode();
		if ((repeatMode == RepeatMode.TEST_DURATION && IterationManager.getManager().isTimeRemaining())
				|| (repeatMode == RepeatMode.INVOCATION_COUNT && IterationManager.getManager().isCountRemaining()))
			return true;
		return false;
	}

	/**
	 * Check if time is remaining for the test execution by comparing the elapsed
	 * time and test duration. This will set the elapsed time variable for the
	 * current test / execution thread
	 * 
	 * @return True if time remaining
	 */
	private boolean isTimeRemaining() {
		boolean timeRemaining = false;
		long testDurationInMilliSeconds = TimeUnit.SECONDS
				.toMillis(BaseTest.getTestObject().getTestDurationInSeconds());
		elapsedTimeInMilliSeconds = new Date().getTime() - startTimeInMilliSeconds;
		if (testDurationInMilliSeconds > elapsedTimeInMilliSeconds) {
			ReportingTools.wipeScreenshotDirectory();
			timeRemaining = true;
		}
		_logger.debug(
				String.format("Is time remaining - testDurationInMilliSeconds[%s], elapsedTimeInMilliSeconds[%s]? %s",
						testDurationInMilliSeconds, elapsedTimeInMilliSeconds, timeRemaining));
		return timeRemaining;
	}

	/**
	 * Check if count is remaining for the test execution by comparing the iteration
	 * and invocation count.
	 * 
	 * @return True if count remaining
	 */
	private boolean isCountRemaining() {
		boolean countRemaining = false;
		int invocationCount = (int) BaseTest.getTestObject().getInvocationCount();
		if (invocationCount > iteration) {
			ReportingTools.wipeScreenshotDirectory();
			countRemaining = true;
		}
		_logger.debug(String.format("Is count remaining  - invocationCount[%s], iteration[%s]? %s", invocationCount,
				iteration, countRemaining));
		return countRemaining;
	}

	/**
	 * Check if set test duration time has exceeded by checking the elapsed time and
	 * test duration. This will <b>NOT</b> set the elapsed time variable for the
	 * current test / execution thread.
	 * 
	 * @return True if set test duration elapsed
	 */
	public boolean isTestDurationElapsed() {
		long testDurationInMilliSeconds = TimeUnit.SECONDS
				.toMillis(BaseTest.getTestObject().getTestDurationInSeconds());
		long elapsedTime = new Date().getTime() - startTimeInMilliSeconds;
		if (testDurationInMilliSeconds > elapsedTime) {
			return false;
		}
		_logger.debug("Elapsed time has exceeded set test duration");
		return true;
	}

	/**
	 * Log the stop of iteration to the test
	 */
	public void stopIteration() {
		long testDurationInMilliSeconds = TimeUnit.SECONDS
				.toMillis(BaseTest.getTestObject().getTestDurationInSeconds());
		elapsedTimeInMilliSeconds = new Date().getTime() - startTimeInMilliSeconds;
		ExecutionLogManager.logIterationEnd(iteration, elapsedTimeInMilliSeconds, testDurationInMilliSeconds);
	}

	/**
	 * Sleep for the set delayBetweenIterationInSeconds parameter set for the test
	 */
	public void sleepBetweenIteration() {
		if (getIteration() > 1) {
			int sleepTimeInSeconds = (int) BaseTest.getTestObject().getDelayBetweenIterationInSeconds();
			_logger.info("Test will sleep for " + sleepTimeInSeconds + " seconds as the set value between iteration");
			ThreadUtils.sleepFor(sleepTimeInSeconds);
		}
	}

	/**
	 * Check if test duration is exceeded. This method comes into picture when the
	 * method under execution is a test and {@link RepeatMode} is
	 * {@link RepeatMode#TEST_DURATION}
	 * 
	 * throws TestDurationExceededException
	 */
	public void checkIfTestDurationExceeded() {
		if (BaseTest.getTestObject().getRepeatMode() == RepeatMode.TEST_DURATION
				&& IterationManager.getManager().isTestDurationElapsed()
				&& ExceptionManager.isMethodUnderExecutionATest())
			throw new TestDurationExceededException();
	}
}
