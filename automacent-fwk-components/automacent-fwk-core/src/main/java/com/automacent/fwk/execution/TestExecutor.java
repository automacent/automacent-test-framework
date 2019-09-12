package com.automacent.fwk.execution;

import com.automacent.fwk.reporting.Logger;

/**
 * Executor to run tests from the command line using environment variables to
 * specify test and test parameters
 * <ul>
 * <li>Comma seperated Test Names must be specified as system property with key
 * <b>automacent.tests</b></li>
 * <li>Fully Qualified Test class should be specified as system property with
 * key <b>{testName}.testclass</b></li>
 * <li>Test Parameters should be specified as system property with key
 * <b>{testName}.{ParameterName}</b></li>
 * </ul>
 * <li>New TestNG Listeners, if any, can be specified as system property with
 * key <b>automacent.listeners</b></li>
 * <li>Default output directory is .\reports. Change should be specified as
 * system property with key <b>automacent.reportdir</b></li>
 * </ul>
 * 
 * Eg: Consider we have to run test LoginTest with test class
 * com.automacent.tests.LoginTest and parameters username and password. To run
 * tests via command line following command can be used
 * 
 * <pre>
 * java -Dautomacent.tests=LoginTest -DLoginTest.testclass=com.automacent.tests.LoginTest 
 * 			-DLoginTest.userName=admin -DLoginTest.password=password
 * 				-cp test.jar com.automacent.fwk.execution.TestExecutor
 * </pre>
 * 
 * @author sighil.sivadas
 */
public class TestExecutor {

	private static final Logger _logger = Logger.getLogger(TestExecutor.class);

	public static void main(String[] args) {
		_logger.info("Constructing test suite");
		Executor mapper = new Executor();
		mapper.generateXmlSuite();
		mapper.runXMLSuite();
	}
}
