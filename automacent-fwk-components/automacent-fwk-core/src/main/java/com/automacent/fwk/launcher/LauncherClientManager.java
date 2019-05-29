package com.automacent.fwk.launcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.IInvokedMethod;
import org.testng.ITestContext;
import org.testng.ITestResult;

import com.automacent.fwk.core.TestObject;
import com.automacent.fwk.enums.MethodType;
import com.automacent.fwk.reporting.Logger;
import com.automacent.fwk.utils.ThreadUtils;

/**
 * This class manages the Launcher clients and is resposible for invoking all
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

	private List<Class<?>> launcherClientClasses = new ArrayList<>();

	/**
	 * Get launcher client classes
	 * 
	 * @return {@link List} of launcher client classes
	 */
	private List<Class<?>> getLauncherClientClasses() {
		return launcherClientClasses;
	}

	/**
	 * Get the comma seperated launcher client classes and map it to Java classes
	 * 
	 * @param launcherClientClasses
	 */
	public void addLauncherClientClasses(String launcherClientClasses) {
		if (!launcherClientClasses.trim().isEmpty()) {
			String launcherClients[] = launcherClientClasses.split(",");
			for (String launcherClient : launcherClients)
				try {
					Class<?> clazz = Class.forName(launcherClient);
					if (AbstractLauncherClient.class.isAssignableFrom(clazz))
						this.launcherClientClasses.add(clazz);
					else
						_logger.warn(String.format(
								"Provided Launcher client class %s is not an sub class of com.automacent.fwk.launcher.LauncherClient",
								launcherClient));
				} catch (ClassNotFoundException e) {
					_logger.warn(String.format("Provided Launcher client class %s is not found in the classpath",
							launcherClient));
				}
			if (getLauncherClientClasses().isEmpty()) {
				_logger.warn("Launcher clients provided are not usable. Launcher clients service will be disabled");
			} else {
				isEnabled = true;
				_logger.info(String.format("Launcher clients set up %s", launcherClientClasses));
			}
		} else {
			_logger.warn("No Launcher clients specified. Launcher clients service will be disabled");
		}
	}

	private Map<Long, List<ILauncherClient>> threadMap = new HashMap<>();

	/**
	 * Get list of launcher client instances
	 * 
	 * @return List of launcher client instances
	 */
	public List<ILauncherClient> getLauncherClients() {
		List<ILauncherClient> launcherClientList = threadMap.get(ThreadUtils.getThreadId());
		if (launcherClientList == null) {
			launcherClientList = new ArrayList<>();
			for (Class<?> launcherClientClass : getLauncherClientClasses()) {
				try {
					launcherClientList.add((ILauncherClient) launcherClientClass.newInstance());
				} catch (InstantiationException | IllegalAccessException e) {
					_logger.warn(String.format("Error initializing launcher client class %s.",
							launcherClientClass.getName()), e);
				}
			}
			threadMap.put(ThreadUtils.getThreadId(), launcherClientList);
		}

		if (!launcherClientList.isEmpty()) {
			isEnabled = true;
		}
		return launcherClientList;
	}

	private boolean isEnabled = false;

	/**
	 * Enable all launcher clients
	 */
	@Override
	public void enableClient() {
		if (isEnabled) {
			for (ILauncherClient launcherClient : getLauncherClients()) {
				launcherClient.enableClient();
			}
		}
	}

	/**
	 * Disable all launcher clients
	 */
	@Override
	public void disableClient() {
		if (isEnabled) {
			for (ILauncherClient launcherClient : getLauncherClients()) {
				launcherClient.disableClient();
			}
		}
	}

	/**
	 * Mark start of test on all launcher clients
	 * 
	 * @param testObject
	 * @param invokedMethod
	 * @param testResult
	 * @param testContext
	 */
	@Override
	public void startTest(TestObject testObject, IInvokedMethod invokedMethod, ITestResult testResult,
			ITestContext testContext) {
		if (isEnabled) {
			for (ILauncherClient launcherClient : getLauncherClients()) {
				launcherClient.startTest(testObject, invokedMethod, testResult, testContext);
			}
		}
	}

	/**
	 * Mark success of test/iteration on all launcher clients
	 * 
	 * @param methodName
	 *            Name of test method
	 * @param methodType
	 *            {@link MethodType}
	 * @param iteration
	 *            Iteration number
	 * @param duration
	 *            Duration of execution of method
	 */
	public void logSuccess(String methodName, MethodType methodType, int iteration, long duration) {
		if (isEnabled) {
			for (ILauncherClient launcherClient : getLauncherClients()) {
				launcherClient.logSuccess(methodName, methodType, iteration, duration);
			}
		}
	}

	/**
	 * Mark failure of test/iteration on all launcher clients
	 * 
	 * @param methodName
	 *            Name of test method
	 * @param methodType
	 *            {@link MethodType}
	 * @param iteration
	 *            Iteration number
	 * @param e
	 *            {@link Throwable} resulting in failure
	 * @param duration
	 *            Duration of execution of method
	 */
	public void logFailure(String methodName, MethodType methodType, int iteration, Throwable e, long duration) {
		if (isEnabled) {
			for (ILauncherClient launcherClient : getLauncherClients()) {
				launcherClient.logFailure(methodName, methodType, iteration, e, duration);
			}
		}
	}

	/**
	 * Mark completion of test on all launcher client
	 */
	public void stopTest() {
		if (isEnabled) {
			for (ILauncherClient launcherClient : getLauncherClients()) {
				launcherClient.stopTest();
			}
		}
	}

}
