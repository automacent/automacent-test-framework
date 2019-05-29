package com.automacent.fwk.utils;

import java.io.File;
import java.io.IOException;

import com.automacent.fwk.reporting.Logger;

/**
 * Util class for file management
 * 
 * @author sighil.sivadas
 */
public class FileUtils {
	private static final Logger _logger = Logger.getLogger(FileUtils.class);

	/**
	 * Get the temporary (%temp%) folder path
	 * 
	 * @return Temporary folder path
	 */
	private static String getTempPath() {
		try {
			File temp = File.createTempFile("temp-file-name", ".tmp");
			String absolutePath = temp.getAbsolutePath();
			String tempFilePath = absolutePath.substring(0, absolutePath.lastIndexOf(File.separator));
			_logger.debug("Temp file path : " + tempFilePath);
			return tempFilePath;
		} catch (IOException e) {
			_logger.warn("Error getting temp file", e);
			return null;
		}
	}

	/**
	 * Recursievly delete all the files and folders
	 * 
	 * @param files
	 *            {@link File} objects
	 */
	private static void deleteFiles(File[] files) {
		for (File tempFile : files) {
			_logger.trace("Deleting file: " + tempFile);
			if (tempFile.isFile()) {
				try {
					tempFile.delete();
				} catch (SecurityException e) {
				}
			} else if (tempFile.isDirectory()) {
				try {
					deleteFiles(tempFile.listFiles());
				} catch (Exception e) {
					_logger.warn("Error deleting temp files");
				}
				if (tempFile.list().length == 0)
					try {
						tempFile.delete();
					} catch (SecurityException e) {

					}
			}
		}
	}

	/*
	 * Clean the temporary folder in the system
	 */
	public static void cleanTempDirectory() {
		_logger.info("Cleaning temp directory");
		String tempPath = getTempPath();
		if (tempPath == null) {
			_logger.warn("Unable to get temp file path");
			return;
		}

		File tempPathFile = new File(tempPath);
		File[] tempFiles = tempPathFile.listFiles();
		try {
			deleteFiles(tempFiles);
		} catch (Exception e) {
			_logger.warn("Error deleting temp files", e);
		}
	}
}
