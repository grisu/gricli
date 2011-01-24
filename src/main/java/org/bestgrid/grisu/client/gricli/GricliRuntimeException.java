package org.bestgrid.grisu.client.gricli;

/**
 * any error that occurs during execution of command
 */
public class GricliRuntimeException extends GricliException {
	public GricliRuntimeException(String msg) {
		super(msg);
	}

	public GricliRuntimeException() {
		super();
	}

	public GricliRuntimeException(Exception ex) {
		super(ex);
	}
}
