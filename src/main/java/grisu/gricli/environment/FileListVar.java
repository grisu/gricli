package grisu.gricli.environment;

import grisu.gricli.GricliSetValueException;

import java.util.ArrayList;
import java.util.List;

public class FileListVar extends GricliVar<List<String>> {

	public FileListVar(String name) {
		super(name);
		try {
			set(new ArrayList<String>());
		} catch (final GricliSetValueException e) {
			// never happens
		}
	}

	@Override
	protected List<String> fromStrings(String[] args)
			throws GricliSetValueException {
		if (args[0] == null) {
			return new ArrayList<String>();
		}
		final ArrayList<String> result = new ArrayList<String>();
		for (final String arg : args) {
			if (arg != null) {
				result.add(arg);
			}
		}
		return result;
	}

}
