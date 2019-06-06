package com.automacent.fwk.execution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.automacent.fwk.core.BaseTest;
import com.automacent.fwk.exceptions.SetupFailedFatalException;
import com.automacent.fwk.listeners.AutomacentListener;
import com.automacent.fwk.reporting.Logger;
import com.automacent.fwk.utils.DateUtils;

/**
 * Executor to run tests from the command line.
 * <ul>
 * <li>Comma seperated Test Names must be specified as system property with key
 * <b>automacent.tests</b></li>
 * <li>Fully Qualified Test class should be specified as system property with
 * key <b>{testName}.testclass</b></li>
 * <li>Test Parameters should be specified as system property with key
 * <b>{testName}.{ParameterName}</b></li>
 * </ul>
 * 
 * Eg: Consider we have to run test LoginTest with test class
 * com.automacent.tests.LoginTest and parameters username and password. To run
 * tests via command line following command can be used
 * 
 * <pre>
 * java -Dautomacent.tests=LoginTest -DLoginTest.testclass=com.automacent.tests.LoginTest 
 * 				-cp test.jar com.automacent.fwk.execution.TestExecutor
 * </pre>
 * 
 * @author sighil.sivadas
 */
public class TestExecutor {

	private static final Logger _logger = Logger.getLogger(TestNgCompiler.class);

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
			generateParameters();
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
		private void generateParameters() {
			Properties properties = System.getProperties();
			String key = null;
			String prefix = String.format("%s.", testName);
			for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();) {
				key = (String) e.nextElement();
				if (key.startsWith(prefix)) {
					parameters.put(key, properties.getProperty(key));
				}
			}
		}

	}

	List<XmlSuite> suites = new ArrayList<XmlSuite>();

	List<TestDefinition> testDefinitions = new ArrayList<>();

	private TestExecutor() {
	}

	/**
	 * Get tests from System property <b>automacebt.tests</b>
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
	 * Generate Test Suite from {@link TestDefinition} objects
	 */
	private void generateTestSuite() {
		XmlSuite suite = new XmlSuite();
		suite.setName(String.format("Suite%s", DateUtils.getDateFormattedForSuiteName()));
		String listenerStrings[] = System.getProperty("automacent.listners", AutomacentListener.class.getName())
				.split(",");
		Set<String> listeners = new HashSet<>(Arrays.asList(listenerStrings));
		listeners.add(AutomacentListener.class.getName());
		_logger.info(String.format("Setting Listeners %s", listeners.toString()));
		suite.setListeners(new ArrayList<String>(listeners));
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

		suites.add(suite);
	}

	/**
	 * Run TestNG suites
	 */
	private void run() {
		TestNG tng = new TestNG();
		tng.setXmlSuites(suites);
		tng.run();
	}

	public static void main(String[] args) {
		TestExecutor executor = new TestExecutor();
		executor.getTests();
		executor.generateTestSuite();
		executor.run();
	}
}
