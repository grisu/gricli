package grisu.gricli.environment;

import grisu.gricli.GricliSetValueException;

public class PositiveIntVar extends IntVar {
	
	public PositiveIntVar(String name, Integer value, boolean nullable) {
		super(name, value, nullable);
	}

	public PositiveIntVar(String name, Integer value) {
		super(name, value);
	} 

	public void set(Integer value) throws GricliSetValueException {
		super.set(value);
		if (value != null && value <= 0){
			throw new GricliSetValueException(getName(),""+value, "cannot be negative");
		}
	}
}