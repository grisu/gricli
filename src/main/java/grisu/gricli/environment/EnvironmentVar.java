package grisu.gricli.environment;

import grisu.gricli.GricliSetValueException;

import java.util.Hashtable;

public class EnvironmentVar extends GricliVar<Hashtable<String, String>> {

	public EnvironmentVar(String name) {
		super(name);
		try {
			set(new Hashtable<String, String>());
		} catch (final GricliSetValueException e) {
			// never happens
		}
	}

	@Override
	protected Hashtable<String, String> fromStrings(String[] args)
			throws GricliSetValueException {
		if (args != null && args[0] != null) {
			throw new GricliSetValueException(getName(), "",
					"environment global cannot be set. please use 'unset' or 'add'  commands");
		} else {
			return new Hashtable<String, String>();
		}
	}
}
