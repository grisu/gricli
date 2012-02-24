package grisu.gricli.environment;

import grisu.gricli.GricliSetValueException;

import java.util.Hashtable;

import org.apache.commons.lang.StringUtils;

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
	public Hashtable<String, String> fromStrings(String[] args)
			throws GricliSetValueException {
		if (StringUtils.isNotBlank(args[0])) {
			throw new GricliSetValueException(getName(), "",
					"environment global cannot be set. please use 'unset' or 'add'  commands");
		} else {
			return new Hashtable<String, String>();
		}
	}
}
