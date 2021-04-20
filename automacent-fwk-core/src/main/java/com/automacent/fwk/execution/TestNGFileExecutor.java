package com.automacent.fwk.execution;

import com.automacent.fwk.reporting.Logger;

/**
 * Executor to run tests from the command line using testNG XML file
 * <ul>
 * <li>Comma seperated TestNG.xml file paths must be specified as system
 * property with key <b>automacent.testsuites</b></li>
 * <li>New TestNG Listeners if any can be specified as system property with key
 * <b>automacent.listeners</b></li>
 * <li>Default output directory is .\reports. Change should be specified as
 * system property with key <b>automacent.reportdir</b></li>
 * </ul>
 * 
 * Eg: Consider we have to run testNG.xml file
 * 
 * <pre>
 * java -automacent.testsuites=testNG.xml 
 * 			-cp fat-h5-system-test-0.0.3-SNAPSHOT.jar com.automacent.fwk.execution.TestNGFileExecutor
 * </pre>
 * 
 * @author sighil.sivadas
 */
public class TestNGFileExecutor {
	private static final Logger _logger = Logger.getLogger(TestNGFileExecutor.class);

	public static void main(String[] args) {
		_logger.info("Constructing test suite");
		Executor mapper = new Executor();
		mapper.generateTestNGFileSuites();
		mapper.runTestNGFileSuites();
	}
}
