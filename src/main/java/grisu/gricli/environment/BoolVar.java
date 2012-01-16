package grisu.gricli.environment;

import grisu.gricli.GricliSetValueException;

import org.apache.commons.lang.StringUtils;

public class BoolVar extends ScalarVar<Boolean> {

	public BoolVar(String name, Boolean value) {
		super(name, value);
	}

	@Override
	protected Boolean fromString(String arg) throws GricliSetValueException {

		if (StringUtils.isBlank(arg)) {
			arg = "false";
		}

		if ("true".equals(arg)) {
			return true;
		} else if ("false".equals(arg)) {
			return false;
		} else {
			throw new GricliSetValueException(getName(), arg,
					"must be either true or false");
		}
	}

}
