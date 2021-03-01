package com.automacent.fwk.launcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.ITestContext;

import com.automacent.fwk.annotations.Step;
import com.automacent.fwk.enums.MethodType;
import com.automacent.fwk.enums.TestStatus;
import com.automacent.fwk.exceptions.LauncherForceCompletedException;
import com.automacent.fwk.reporting.Logger;
import com.automacent.fwk.utils.ThreadUtils;

/**
 * This class manages the Launcher clients and is responsible for invoking all
 * the specified Launcher clients
 * 
 * @author sighil.sivadas
 *
 */
public class LauncherClientManager implements ILauncherClient {

	private static final Logger _logger = Logger.getLogger(LauncherClientManager.class);

	private static LauncherClientManager launcherClientManager;

	/**
	 * Get singleton instance of {@link LauncherClientManager}
	 * 
	 * @return {@link LauncherClientManager}
	 */
	public static LauncherClientManager getManager() {
		if (launcherClientManager == null) {
			launcherClientManager = new LauncherClientManager();
			_logger.info("New instance of Launhcer client manager created");
		}
		return launcherClientManager;
	}

	private boolean isEnabled = false;

	/**
	 * Enable all launcher clients
	 */
	@Override
	public void enableClient() {
		if (isEnabled)
			for (ILauncherClient launcherClient : getLauncherClients())
				launcherClient.enableClient();
	}

	/**
	 * Disable all launcher clients
	 */
	@Override
	public void disableClient() {
		if (isEnabled)
			for (ILauncherClient launcherClient : getLauncherClients())
				launcherClient.disableClient();
	}

	private Map<Class<?>, Map<Long, ILauncherClient>> launcherClientMasterMap = new HashMap<>();

	public Map<Class<?>, Map<Long, ILauncherClient>> getLauncherClientMasterMap() {
		return launcherClientMasterMap;
	}

	/**
	 * Get the comma separated launcher client classes and map it to Java classes
	 * 
	 * @param launcherClientClasses Comma separated launcher client classes
	 */
	public void generateLauncherClientMasterMap(String launcherClientClasses) {
		if (!launcherClientClasses.trim().isEmpty()) {
			String launcherClients[] = launcherClientClasses.split(",");
			for (String launcherClient : launcherClients)
				try {
					Class<?> clazz = Class.forName(launcherClient);
					// TODO Consider changing the assignable class to interface
					if (AbstractLauncherClient.class.isAssignableFrom(clazz)) {
						this.launcherClientMasterMap.put(clazz, new HashMap<>());
					} else
						_logger.warn(String.format(
								"Provided Launcher client class %s is not an sub class of com.automacent.fwk.launcher.AbstractLauncherClient",
								launcherClient));
				} catch (ClassNotFoundException e) {
					_logger.warn(String.format("Provided Launcher client class %s is not found in the classpath",
							launcherClient));
				}
			if (getLauncherClientMasterMap().isEmpty()) {
				_logger.warn("Launcher clients provided are not usable. Launcher clients service will be disabled");
			} else {
				isEnabled = true;
				_logger.info(String.format("Launcher clients set up %s", launcherClientClasses));
			}
		} else {
			_logger.warn("No Launcher clients specified. Launcher clients service will be disabled");
		}
	}

	/**
	 * Get list of launcher client instances
	 * 
	 * @return List of launcher client instances
	 */
	public List<ILauncherClient> getLauncherClients() {
		List<ILauncherClient> activeLauncherClientList = new ArrayList<>();

		Set<Class<?>> launcherClientClasses = getLauncherClientMasterMap().keySet();
		for (Class<?> launcherClientClass : launcherClientClasses) {
			Map<Long, ILauncherClient> launcherClientMap = getLauncherClientMasterMap().get(launcherClientClass);
			ILauncherClient launcherClient = launcherClientMap.get(ThreadUtils.getThreadId());
			try {
				if (launcherClient == null)
					launcherClient = (ILauncherClient) launcherClientClass.newInstance();
				launcherClientMap.put(ThreadUtils.getThreadId(), launcherClient);
				activeLauncherClientList.add(launcherClient);
			} catch (InstantiationException | IllegalAccessException e) {
				_logger.warn(String.format("Error initializing launcher client class %s.",
						launcherClientClass.getName()), e);
			}

		}

		if (!activeLauncherClientList.isEmpty())
			isEnabled = true;
		return activeLauncherClientList;
	}

	/**
	 * Mark start of XML test on all launcher client
	 * 
	 * @param testContext TestNG {@link ITestContext}
	 */
	public void startTest(ITestContext testContext) {
		if (isEnabled)
			for (ILauncherClient launcherClient : getLauncherClients())
				launcherClient.startTest(testContext);
	}

	/**
	 * Mark success of test/iteration on all launcher clients
	 * 
	 * @param methodName Name of test method
	 * @param methodType {@link MethodType}
	 * @param iteration  Iteration number
	 * @param duration   Duration of execution of method
	 */
	public void logSuccess(String methodName, MethodType methodType, int iteration, long duration) {
		if (isEnabled && !methodName.toLowerCase().startsWith("automacentinternal"))
			for (ILauncherClient launcherClient : getLauncherClients())
				launcherClient.logSuccess(methodName, methodType, iteration, duration);
	}

	/**
	 * Mark failure of test/iteration on all launcher clients
	 * 
	 * @param methodName Name of test method
	 * @param methodType {@link MethodType}
	 * @param iteration  Iteration number
	 * @param e          {@link Throwable} resulting in failure
	 * @param duration   Duration of execution of method
	 */
	public void logFailure(String methodName, MethodType methodType, int iteration, Throwable e, long duration) {
		if (isEnabled && !methodName.toLowerCase().startsWith("automacentinternal"))
			for (ILauncherClient launcherClient : getLauncherClients())
				launcherClient.logFailure(methodName, methodType, iteration, e, duration);
	}

	/**
	 * Mark completion of test on all launcher client
	 */
	public void stopTest() {
		if (isEnabled)
			for (ILauncherClient launcherClient : getLauncherClients())
				launcherClient.stopTest();
	}

	/**
	 * Send heart beat to the launcher server
	 *
	 * @throws LauncherForceCompletedException when test instance status is not
	 *                                         RUNNING
	 */
	public void ping() throws LauncherForceCompletedException {
		if (isEnabled)
			for (ILauncherClient launcherClient : getLauncherClients())
				launcherClient.ping();
	}

	/**
	 * Log start of {@link MethodType}
	 * 
	 * @param method     Name of the method
	 * @param methodType {@link MethodType}
	 */
	public void logStart(String method, MethodType methodType) {
		if (isEnabled && !method.toLowerCase().startsWith("automacentinternal"))
			for (ILauncherClient launcherClient : getLauncherClients())
				launcherClient.logStart(method, methodType);
	}

	/**
	 * Log {@link Step} completion and duration
	 * 
	 * @param methodWithArguments Method name with arguments
	 * @param methodType          {@link MethodType}
	 * @param testStatus          {@link TestStatus}
	 * 
	 * @param duration            Duration in milliseconds
	 * @param t                   {@link Throwable}
	 */
	public void logEnd(String methodWithArguments, MethodType methodType, TestStatus testStatus, long duration,
			Throwable t) {
		if (isEnabled && !methodWithArguments.toLowerCase().startsWith("automacentinternal"))
			for (ILauncherClient launcherClient : getLauncherClients())
				launcherClient.logEnd(methodWithArguments, methodType, testStatus, duration, t);
	}
}
