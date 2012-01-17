package grisu.gricli.environment;

import grisu.gricli.GricliSetValueException;

import org.apache.commons.lang.StringUtils;

public class IntVar extends ScalarVar<Integer> {

	public IntVar(String name, Integer value) {
		super(name, value);
	}

	public IntVar(String name, Integer value, boolean nullable) {
		super(name, value, nullable);
	}

	@Override
	protected Integer fromString(String arg) throws GricliSetValueException {
		if (StringUtils.isBlank(arg)) {
			return null;
		}
		try {
			return Integer.parseInt(arg);
		} catch (final NumberFormatException ex) {
			throw new GricliSetValueException(getName(), arg, "not an integer");
		}
	}

}
