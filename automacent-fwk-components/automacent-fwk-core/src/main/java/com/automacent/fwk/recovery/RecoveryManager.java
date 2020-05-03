package com.automacent.fwk.recovery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.automacent.fwk.annotations.StepsAndPagesProcessor;
import com.automacent.fwk.enums.MethodType;
import com.automacent.fwk.enums.TestStatus;
import com.automacent.fwk.exceptions.RecoveryFailedException;
import com.automacent.fwk.execution.IterationManager;
import com.automacent.fwk.launcher.LauncherClientManager;
import com.automacent.fwk.reporting.Logger;
import com.automacent.fwk.utils.LoggingUtils;

/**
 * This class manages the Recovery Scenarios for the test. If a test fails and
 * retry has to be performed, Recovery Scenarios will help in resetting the test
 * bed to the state before the test started
 * 
 * @author sighil.sivadas
 *
 */
public class RecoveryManager {
	private static final Logger _logger = Logger.getLogger(LauncherClientManager.class);

	public RecoveryManager(String recoveryClasses) {
		addRecoveryClasses(recoveryClasses);
	}

	private List<Class<?>> recoveryClasses = new ArrayList<>();

	private List<Class<?>> getRecoveryClasses() {
		return recoveryClasses;
	}

	/**
	 * Get the Recovery Classes String parameter and find the equivalent Class and
	 * save it in a list
	 * 
	 * @param recoveryClasses
	 *            Comma seperated fully qualified class names of Recovery Classes
	 */
	private void addRecoveryClasses(String recoveryClasses) {
		if (!recoveryClasses.trim().isEmpty()) {
			String recoveryClassArray[] = recoveryClasses.split(",");
			for (String recoveryClass : recoveryClassArray)
				try {
					Class<?> clazz = Class.forName(recoveryClass);
					if (WebRecovery.class.isAssignableFrom(clazz)) {
						this.recoveryClasses.add(clazz);
					} else {
						_logger.warn(String.format(
								"Provided Recovery client class %s is not  sub class of com.automacent.fwk.recovery.WebRecovery",
								recoveryClass));
					}
				} catch (ClassNotFoundException e) {
					_logger.warn(
							String.format("Provided Recovery class %s is not found in the classpath", recoveryClass));
				}
			if (getRecoveryClasses().isEmpty())
				_logger.info("No Valid Recovery classes specified");
			else
				_logger.info(String.format("Recovery classes added %s", getRecoveryClasses()));
		} else {
			_logger.info("No Recovery classes specified");
		}
	}

	/**
	 * Execute all the Recovery classes set for the test
	 */
	public void executeRecoveryScenarios() {
		if (IterationManager.getManager().isExecuteRecoveryScenarios()
				&& IterationManager.getManager().getIteration() > 1) {
			for (Class<?> recoveryClass : getRecoveryClasses()) {
				_logger.info(String.format("Starting recovery steps specified in %s", recoveryClass.getName()));
				long startTime = new Date().getTime();
				String recoveryClassName = LoggingUtils.addSpaceToCamelCaseString(recoveryClass.getSimpleName())
						.toLowerCase();
				try {
					LauncherClientManager.getManager().logStart(recoveryClassName, MethodType.RECOVERY);
					WebRecovery recovery = (WebRecovery) recoveryClass.newInstance();
					StepsAndPagesProcessor.processAnnotation(recovery);
					recovery.checkRecoveryParameters();
					recovery.recover();
					LauncherClientManager.getManager().logEnd(recoveryClassName, MethodType.RECOVERY,
							TestStatus.PASS, new Date().getTime() - startTime, null);
				} catch (Throwable e) {
					LauncherClientManager.getManager().logEnd(recoveryClassName, MethodType.RECOVERY,
							TestStatus.FAIL, new Date().getTime() - startTime, e);
					throw new RecoveryFailedException(e);
				}
				_logger.info(String.format("Recovery steps specified in %s completed", recoveryClass));
			}
		}
		IterationManager.getManager().setExecuteRecoveryScenarios(false);
	}
}