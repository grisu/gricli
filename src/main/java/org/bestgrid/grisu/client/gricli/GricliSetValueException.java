package org.bestgrid.grisu.client.gricli;

public class GricliSetValueException extends GricliRuntimeException {
	private final String var;
	private final String value;
	private final String reason;

	public GricliSetValueException(String var, String value, String reason) {
		super();
		this.var = var;
		this.value = value;
		this.reason = reason;
	}

	public String getVar() {
		return this.var;
	}

	public String getValue() {
		return this.value;
	}

	public String getReason() {
		return this.reason;
	}
}
