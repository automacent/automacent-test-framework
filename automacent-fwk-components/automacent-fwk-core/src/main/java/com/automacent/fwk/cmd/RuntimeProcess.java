package com.automacent.fwk.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.automacent.fwk.reporting.Logger;
import com.automacent.fwk.utils.ThreadUtils;

/**
 * Wrapper class for {@link Runtime} mainly for handling {@link Process}
 * operations
 * 
 * @author sighil.sivadas
 */
public class RuntimeProcess {

	private static final Logger _logger = Logger.getLogger(RuntimeProcess.class);

	private static HashMap<Long, RuntimeProcess> runtimeProcessMap = new HashMap<Long, RuntimeProcess>();

	public static RuntimeProcess getExecutor() {
		if (!runtimeProcessMap.containsKey(ThreadUtils.getThreadId()))
			runtimeProcessMap.put(ThreadUtils.getThreadId(), new RuntimeProcess());
		return runtimeProcessMap.get(ThreadUtils.getThreadId());
	}

	private RuntimeProcess() {

	}

	String output = "";
	String error = "";

	/**
	 * @return output string
	 */
	public String getOutput() {
		return output;
	}

	/**
	 * @return error string
	 */
	public String getError() {
		return error;
	}

	/**
	 * Construct success message from process.getInputStream();
	 * 
	 * @param process
	 *            {@link Process} executed
	 * @return output message
	 * @throws IOException
	 */
	private String getStdOutput(Process process) throws IOException {
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = null;
		String input = " {";
		while ((line = stdInput.readLine()) != null)
			if (!line.equals(""))
				input += line + " ";
		return input + "}";
	}

	/**
	 * Construct error message from process.getErrorStream();
	 * 
	 * @param process
	 *            {@link Process} executed
	 * @return error message
	 * @throws IOException
	 */
	private String getStdError(Process process) throws IOException {
		BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		String line = null;
		String error = " {";
		while ((line = stdError.readLine()) != null)
			if (!line.equals(""))
				error += line + " ";
		return error + "}";
	}

	/**
	 * Executes the provided command and displays the success/error message. This
	 * method does not fail on failed command execution.
	 * 
	 * @param command
	 *            Command to be executed
	 * @throws IOException
	 *             thrown on unsuccessful completion
	 */
	public void executeWithoutExitCodeCheck(String command) throws IOException {
		_logger.info("Executing windows batch command [" + command + "]");
		Process process;
		try {
			process = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			_logger.error("Error getting process", e);
			throw e;
		}

		try {
			output = getStdOutput(process);
		} catch (IOException e) {
			_logger.error("Error getting output string", e);
			throw e;
		}

		try {
			error = getStdError(process);
		} catch (IOException e) {
			_logger.error("Error getting error string", e);
			throw e;
		}

		if (!output.equals(" {}"))
			_logger.info("Output:" + output);

		if (!error.equals(" {}"))
			_logger.info("Output:" + error);
	}
}
