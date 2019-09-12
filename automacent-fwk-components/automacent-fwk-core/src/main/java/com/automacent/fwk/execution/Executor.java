package com.automacent.fwk.execution;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.testng.ITestNGListener;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.automacent.fwk.core.BaseTest;
import com.automacent.fwk.exceptions.SetupFailedFatalException;
import com.automacent.fwk.listeners.AutomacentListener;
import com.automacent.fwk.reporting.Logger;

/**
 * Class for preparing test suites for execution. The test suites should be
 * generated from testNG XML file or from environment parameters
 * 
 * @author sighil.sivadas
 *
 */
class Executor {
	private static final Logger _logger = Logger.getLogger(Executor.class);

	class TestDefinition {

		private String testName;
		private Class<?> testClass;
		private Map<String, String> parameters = new HashMap<>();

		public String getTestName() {
			return testName;
		}

		public Class<?> getTestClass() {
			return testClass;
		}

		public Map<String, String> getParameters() {
			return parameters;
		}

		public TestDefinition(String testName) {
			this.testName = testName;
			findTestClass();
			generateTestParameters();
		}

		/**
		 * Find the test class from System property
		 */
		private void findTestClass() {
			String testClassString = System.getProperty(String.format("%s.testclass", testName));
			if (testClassString == null)
				throw new SetupFailedFatalException(
						String.format("No fully qualified tests specified via property %s.testclass", getTestName()));
			try {
				testClass = Class.forName(testClassString);
			} catch (ClassNotFoundException e) {
				throw new SetupFailedFatalException(String.format("Test Class %s not found", testClassString), e);
			}

			if (!BaseTest.class.isAssignableFrom(testClass))
				throw new SetupFailedFatalException(
						String.format("Test class %s is not child of BaseTest", testClassString));
		}

		/**
		 * Extract the list of parameters for the test from System.properties
		 */
		private void generateTestParameters() {
			Properties properties = System.getProperties();
			String key = null;
			String prefix = String.format("%s.", testName);
			for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();) {
				key = (String) e.nextElement();
				if (key.startsWith(prefix))
					parameters.put(key.split(prefix)[1], properties.getProperty(key));
			}
		}
	}

	List<XmlSuite> xmlSuites = new ArrayList<XmlSuite>();
	List<TestDefinition> testDefinitions = new ArrayList<>();

	List<String> testNGSuites = new ArrayList<>();

	/**
	 * Get tests from System property <b>automacent.tests</b>
	 */
	private void getTests() {
		String testClassProperty = System.getProperty("automacent.tests");
		if (testClassProperty != null) {
			String[] testClasses = testClassProperty.split(",");
			for (String testClass : testClasses) {
				try {
					testDefinitions.add(new TestDefinition(testClass));
				} catch (Exception e) {
					_logger.warn(String.format("Error while setting up provided test class %s", testClass), e);
				}
			}
		}
		if (testDefinitions.isEmpty())
			throw new SetupFailedFatalException("No valid test cases specified to run via property automacent.tests");
	}

	/**
	 * Get listeners from System property <b>automacent.listeners</b>
	 * 
	 * @return List of fully qualified name of Listener classes
	 */
	private List<String> getListeners() {
		String listenerStrings[] = System.getProperty("automacent.listeners", AutomacentListener.class.getName())
				.split(",");
		Set<String> listeners = new HashSet<>(Arrays.asList(listenerStrings));
		listeners.add(AutomacentListener.class.getName());
		_logger.info(String.format("Setting Listeners %s", listeners.toString()));
		return new ArrayList<String>(listeners);
	}

	/**
	 * Get listeners from System property <b>automacent.listeners</b>
	 * 
	 * @return List of Listener classes
	 */
	@SuppressWarnings("unchecked")
	private List<Class<? extends ITestNGListener>> getListenerClasses() {
		String listenerStrings[] = System.getProperty("automacent.listeners", AutomacentListener.class.getName())
				.split(",");
		Set<Class<? extends ITestNGListener>> listeners = new HashSet<>();

		for (String listener : listenerStrings) {
			try {
				Class<?> clazz = Class.forName(listener);
				if (!ITestNGListener.class.isAssignableFrom(clazz))
					_logger.warn(String.format("Provided listener class %s does not extend ITestNGListener", listener));
				else
					listeners.add((Class<? extends ITestNGListener>) clazz);
			} catch (Exception e) {
				_logger.warn(String.format("Provided listener class %s not found", listener), e);
			}
		}

		listeners.add(AutomacentListener.class);
		_logger.info(String.format("Setting Listeners %s", listeners.toString()));
		return new ArrayList<Class<? extends ITestNGListener>>(listeners);
	}

	/**
	 * Get Global Parameters from environment variables
	 * 
	 * @return map of parameters - key, value
	 */
	private Map<String, String> getGlobalParametersFromEnvironmentParameters() {
		Map<String, String> globalParameters = new HashMap<>();

		globalParameters.put("runName", System.getProperty("automacent.runName", ""));
		globalParameters.put("batchNumber", System.getProperty("automacent.batchNumber", ""));
		globalParameters.put("launcherClients", System.getProperty("automacent.launcherClients", ""));

		globalParameters.put("retryMode", System.getProperty("automacent.retryMode", "OFF"));
		globalParameters.put("recoveryClasses", System.getProperty("automacent.recoveryClasses", ""));

		globalParameters.put("timeoutInSeconds", System.getProperty("automacent.timeoutInSeconds", "20"));
		globalParameters.put("slowdownDurationInSeconds",
				System.getProperty("automacent.slowdownDurationInSeconds", "1"));

		globalParameters.put("repeatMode", System.getProperty("automacent.repeatMode", "OFF"));
		globalParameters.put("testDurationInSeconds", System.getProperty("automacent.testDurationInSeconds", "0"));
		globalParameters.put("invocationCount", System.getProperty("automacent.invocationCount", "0"));
		globalParameters.put("delayBetweenIterationInSeconds",
				System.getProperty("automacent.delayBetweenIterationInSeconds", "0"));

		// Selenium Base Test Parameters

		globalParameters.put("ieDriverLocation", System.getProperty("automacent.ieDriverLocation", ""));
		globalParameters.put("chromeDriverLocation", System.getProperty("automacent.chromeDriverLocation", ""));
		globalParameters.put("geckoDriverLocation", System.getProperty("automacent.geckoDriverLocation", ""));
		globalParameters.put("scriptTimeoutInSeconds", System.getProperty("automacent.scriptTimeoutInSeconds", "300"));
		globalParameters.put("pageLoadTimeoutInSeconds",
				System.getProperty("automacent.pageLoadTimeoutInSeconds", "300"));
		globalParameters.put("socketTimeoutInSeconds", System.getProperty("automacent.socketTimeoutInSeconds", "300"));
		globalParameters.put("browser", System.getProperty("automacent.browser", "CHROME"));

		if (System.getProperty("automacent.baseUrl", null) != null)
			globalParameters.put("baseUrl", System.getProperty("automacent.baseUrl"));

		globalParameters.put("screenshotType", System.getProperty("automacent.screenshotType", "BROWSER_SCREENSHOT"));
		globalParameters.put("screenshotMode", System.getProperty("automacent.screenshotMode", "ON_FAILURE"));
		globalParameters.put("screenshotModeForIteration",
				System.getProperty("automacent.screenshotModeForIteration", "LAST_ITERATION"));

		return globalParameters;
	}

	private String getOutputDirectory() {
		String outputDirectory = System.getProperty("automacent.reportdir", null);
		if (outputDirectory == null) {
			outputDirectory = "." + File.separator + "report";
			System.setProperty("automacent.reportdir", outputDirectory);
		}
		return outputDirectory;
	}

	/**
	 * Generate Test Suite from {@link TestDefinition} objects
	 */
	public void generateXmlSuite() {
		getTests();
		XmlSuite suite = new XmlSuite();
		suite.setName(String.format("Automacent-XML-Suite"));
		suite.setListeners(new ArrayList<String>(getListeners()));
		suite.setParameters(getGlobalParametersFromEnvironmentParameters());
		for (TestDefinition testDefinition : testDefinitions) {
			_logger.info(String.format("Adding Test %s [%s]", testDefinition.getTestName(),
					testDefinition.getTestClass().getName()));
			_logger.info(String.format("Adding Test Parameters %s", testDefinition.getParameters()));
			XmlTest test = new XmlTest(suite);
			test.setName(testDefinition.getTestName());
			test.setParameters(testDefinition.getParameters());
			List<XmlClass> classes = new ArrayList<XmlClass>();
			classes.add(new XmlClass(testDefinition.getTestClass()));
			test.setXmlClasses(classes);
		}
		xmlSuites.add(suite);
	}

	public void generateTestNGFileSuites() {
		String testSuitesProperty = System.getProperty("automacent.testsuites", null);
		if (testSuitesProperty == null)
			throw new SetupFailedFatalException(
					"Property automacent.testsuites is not set. There is no TestNG xml file to run");
		String[] testSuites = testSuitesProperty.split(",");
		for (String testSuite : testSuites) {
			testNGSuites.add(testSuite);
		}
	}

	/**
	 * Run XML suites
	 */
	public void runXMLSuite() {
		TestNG tng = new TestNG();
		tng.setOutputDirectory(getOutputDirectory());
		tng.setXmlSuites(xmlSuites);
		tng.run();
	}

	/**
	 * Run testNG.xml suite
	 */
	public void runTestNGFileSuites() {
		TestNG tng = new TestNG();
		tng.setListenerClasses(getListenerClasses());
		tng.setOutputDirectory(getOutputDirectory());
		tng.setTestSuites(testNGSuites);
		tng.run();
	}
}
