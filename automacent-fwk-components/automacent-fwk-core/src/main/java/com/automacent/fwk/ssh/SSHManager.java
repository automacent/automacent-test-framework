package com.automacent.fwk.ssh;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.automacent.fwk.reporting.Logger;
import com.automacent.fwk.utils.ThreadUtils;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * 
 * SSH client to execute remote commands using the Exec channel.
 * 
 * To use the SSHManager, the manager should be initiated by calling
 * {@link SSHManager#SSHManager()} and the SSH session must be opened using
 * {@link SSHManager#openSSHSession()}. After completion of execution
 * {@link SSHManager#closeSSHSession()} must be invoked to close the SSH session
 * 
 * @author sighil.sivadas
 *
 */
public class SSHManager {

	private static final Logger _logger = Logger.getLogger(SSHManager.class);

	private String username;
	private String host;
	private String password;
	private int port;
	private JSch jSch;

	public SSHManager(String username, String host, String password) {
		new SSHManager(username, host, password, 22);
	}

	public SSHManager(String username, String host, String password, int port) {
		this.username = username;
		this.host = host;
		this.password = password;
		this.port = port;
		jSch = new JSch();
	}

	private Session session;

	/**
	 * Open a SSH session
	 * 
	 * @return true if successful
	 */
	public boolean openSSHSession() {
		try {
			session = jSch.getSession(username, host, port);
		} catch (JSchException e) {
			_logger.fatal("Creating SSH session unsuccessful", e);
			return false;
		}
		session.setPassword(password);
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		try {
			session.connect();
			_logger.debug("SSH Session established");
		} catch (JSchException e) {
			_logger.fatal("SSH Session could not be establish", e);
			return false;
		}
		return true;
	}

	/**
	 * Close the SSH Session
	 * 
	 * @return true if successful
	 */
	public boolean closeSSHSession() {
		try {
			session.disconnect();
			_logger.debug("Session disconnected");
		} catch (NullPointerException e) {
			_logger.warn("Error closing the session. Check if the session is active/initialized", e);
			return false;
		}
		return true;
	}

	// Exec channel -------------------------------------------------

	private BufferedReader outputReader;
	private String output;
	private BufferedReader errorReader;
	private String error;

	private ChannelExec execChannel;

	/**
	 * Get the output of the last command executed
	 * 
	 * @return Output of last command executed or "" if no output found
	 */
	public String getOutput() {
		return output;
	}

	/**
	 * Set the output. Used in the {@link SSHManager#executeCommand()} method to
	 * save the output from the Buffered Reader
	 * 
	 * @param output
	 */
	private void setOutput(String output) {
		this.output = output;
	}

	/**
	 * Get the error of the last command executed
	 * 
	 * @return error or "" if no error found
	 */
	public String getError() {
		return error;
	}

	/**
	 * Set the error. Used in the {@link SSHManager#executeCommand()} method to save
	 * the error from the Buffered Reader
	 * 
	 * @param error
	 */
	private void setError(String error) {
		this.error = error;
	}

	/**
	 * Fetch the output from the Buffered reader and save it to variable output.
	 * Used in {@link SSHManager#executeCommand()}
	 */
	private void fetchOutputMessageFromStream() {
		String message = "";

		try {
			String msg = null;
			while ((msg = outputReader.readLine()) != null) {
				if (!message.equals(""))
					message += "\n";
				message += msg;
			}
		} catch (IOException e) {
			_logger.warn("Error reading the output stream", e);
		}
		setOutput(message);
	}

	/**
	 * Fetch the error message from the Buffered reader and save it to variable
	 * error. Used in {@link SSHManager#executeCommand()}
	 */
	private void fetchErrorMessageFromStream() {
		String message = "";
		try {
			String msg = null;
			while ((msg = errorReader.readLine()) != null) {
				if (!message.equals(""))
					message += "\n";
				message += msg;
			}
		} catch (IOException e) {
			_logger.warn("Error reading the error output stream", e);
		}
		setError(message);
	}

	/**
	 * Check if executing the command returned any error message to check if the
	 * command executed successfully
	 * 
	 * @return true is command executed successfully
	 */
	private boolean isCommandExecutedSuccessfully() {
		return getError() == null || getError().isEmpty() ? true : false;
	}

	/**
	 * Open Exec channel. Used in the {@link SSHManager#executeCommand()}
	 * 
	 * @return true if successful
	 */
	private boolean openChannelExec() {
		try {
			execChannel = (ChannelExec) session.openChannel("exec");
			_logger.debug("Exec Channel opened");
		} catch (JSchException e) {
			_logger.error("Error opening channel", e);
			return false;
		}
		ThreadUtils.sleepFor(2);
		return true;
	}

	/**
	 * Close the Exec channel
	 * 
	 * @return true is successful
	 */
	private boolean closeChannelExec() {
		try {
			execChannel.disconnect();
			_logger.debug("Exec channel closed");
		} catch (NullPointerException e) {
			_logger.warn("Error closing the channel. Check if the channnel is active/initialized", e);
			return false;
		}
		ThreadUtils.sleepFor(2);
		return true;
	}

	/**
	 * Execute a command on the remote host opened on the SSH session using the Exec
	 * channel.
	 *
	 * This method will open the Exec channel and close it on completion. The SSH
	 * session should be opened before invoking this method using
	 * {@link SSHManager#openSSHSession()} method
	 * 
	 * @param command
	 *            Command to be executed
	 * @return true if execution is successful and did not return any error
	 */
	public boolean executeCommand(String command) {
		setOutput("");
		setError("");

		if (!openChannelExec()) {
			_logger.error("Command [" + command + "] execution not successful -  Channel cannot be opened");
			return false;
		}

		try {
			outputReader = new BufferedReader(new InputStreamReader(execChannel.getInputStream()));
			errorReader = new BufferedReader(new InputStreamReader(execChannel.getErrStream()));
		} catch (IOException e) {
			_logger.warn("Error setting the Buffered Readers", e);
		}

		execChannel.setCommand(command);

		try {
			execChannel.connect();
		} catch (JSchException e) {
			_logger.error("Command [" + command + "] execution not successful", e);
			return false;
		}

		fetchOutputMessageFromStream();
		fetchErrorMessageFromStream();

		if (isCommandExecutedSuccessfully()) {
			_logger.info("Command [" + command + "] executed");
		} else {
			_logger.error("Command [" + command + "] execution not successful" + getError());
		}

		closeChannelExec();

		return true;
	}

	// Shell channel ------------------------------------------------

	private ChannelShell shellChannel;

	/**
	 * Open Shell channel. Used in the {@link SSHManager#executeShellCommand()}
	 * 
	 * @return true if successful
	 */
	public boolean openShellChannel() {
		try {
			shellChannel = (ChannelShell) session.openChannel("shell");
			shellChannel.connect();
			_logger.debug("Shell Channel opened");
		} catch (JSchException e) {
			_logger.error("Error opening shell channel", e);
			return false;
		}

		return true;
	}

	/**
	 * Close the Shell channel
	 * 
	 * @return true is successful
	 * @throws InterruptedException
	 */
	public boolean closeShellChannel() {
		try {
			shellChannel.disconnect();
			_logger.debug("Shell channel closed");
		} catch (NullPointerException e) {
			_logger.error("Error closing the channel", e);
			return false;
		}
		return true;
	}

	/**
	 * Execute a command on the remote host opened on the SSH session using the
	 * Shell channel.
	 *
	 * The SSH session and Shell channel must should be opened before invoking this
	 * method using {@link SSHManager#openSSHSession()} and
	 * {@link SSHManager#openShellChannel()}.
	 * 
	 * The method will execute the command and return instance of
	 * {@link BufferedReader} so that the calling method can access the output of
	 * execution. The calling method must close the Shell channel using
	 * {@link SSHManager#closeShellChannel()}
	 * 
	 * @param command
	 *            Command to be executed
	 * @return {@link BufferedReader} from which the calling method can access the
	 *         output
	 */
	public BufferedReader executeShellCommand(String command) {
		InputStream commandInput = new ByteArrayInputStream((command + "\n").getBytes());
		shellChannel.setInputStream(commandInput);
		BufferedReader outputReader = null;
		try {
			outputReader = new BufferedReader(new InputStreamReader(shellChannel.getInputStream()));
		} catch (IOException e) {
			_logger.error("Error setting the Buffered Readers", e);
		}
		return outputReader;
	}

}
