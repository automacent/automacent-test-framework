package com.automacent.fwk.enums;

/**
 * 
 * ENUM describing colors used in the Reports.
 * 
 * @author sighil.sivadas
 */
public enum Color {

	RED("#D20000"), BLACK("black"), ORANGE("orange");

	String colorValue;

	private Color(String colorValue) {
		this.colorValue = colorValue;
	}

	public String getColorValue() {
		return colorValue;
	}

}
