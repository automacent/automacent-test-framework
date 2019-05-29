package com.automacent.fwk.enums;

/**
 * Enum describing the Method Type. Based on this, the framework determines the
 * type of method that is being executed and helps in managing the control flow
 * and logging
 * 
 * @author sighil.sivadas
 */
public enum MethodType {
	BEFORE, TEST, AFTER, ACTION, STEP, RECOVERY;
}
