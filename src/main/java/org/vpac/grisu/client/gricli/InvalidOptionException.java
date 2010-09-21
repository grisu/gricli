package org.vpac.grisu.client.gricli;

public class InvalidOptionException extends RuntimeException {

	private String option = null;
	private String value = null;

	public InvalidOptionException(String option, String value) {
		this.option = option;
		this.value = value;
	}

	public String getOption() {
		return option;
	}

	public String getValue() {
		return value;
	}

}
