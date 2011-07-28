package grisu.gricli.environment;

import grisu.gricli.GricliSetValueException;

public class IntVar extends ScalarVar<Integer> {
	
	public IntVar(String name, Integer value) {
		super(name, value);
	}
	
	public IntVar(String name, Integer value, boolean nullable){
		super(name,value,nullable);
	}

	@Override
	protected Integer fromString(String arg) throws GricliSetValueException {
		try {
			return Integer.parseInt(arg);
		} catch (NumberFormatException ex){
			throw new GricliSetValueException(getName(), arg, "not an integer");
		}
	}

}
