package grisu.gricli.environment;

import grisu.gricli.GricliSetValueException;

public class StringVar extends ScalarVar<String> {

	public StringVar(String name, String value) {
		super(name, value);
	}

	public StringVar(String name, String value, boolean nullable) {
		super(name, value, nullable);
	}

	@Override
	protected String fromString(String arg) throws GricliSetValueException {
		return arg;
	}

}
