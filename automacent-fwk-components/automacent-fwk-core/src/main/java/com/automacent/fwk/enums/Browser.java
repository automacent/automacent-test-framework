package com.automacent.fwk.enums;

/**
 * EUM describing the different browsers supported by the framework
 * 
 * @author sighil.sivadas
 */
public enum Browser {
	IE, CHROME, FF;

	public static Browser getDefault() {
		return CHROME;
	}

}
