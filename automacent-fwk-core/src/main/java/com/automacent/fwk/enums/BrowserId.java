package com.automacent.fwk.enums;

/**
 * ENUM describing the concurrent browsers ids to be used in a single test
 * 
 * @author sighil.sivadas
 */
public enum BrowserId {
	ALPHA, BRAVO, CHARLIE, DELTA, ECHO, FOXTROT;

	public static BrowserId getDefault() {
		return ALPHA;
	}
}
