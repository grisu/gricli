package grisu.gricli.environment;

import java.util.ArrayList;
import java.util.List;

import grisu.gricli.GricliSetValueException;

public class FileListVar extends GricliVar<List<String>> {

	public FileListVar(String name) {
		super(name);
		try {
			set(new ArrayList<String>());
		} catch (GricliSetValueException e){
			// never happens
		}
	}

	@Override
	protected List<String> fromStrings(String[] args)
			throws GricliSetValueException {
		ArrayList<String> result = new ArrayList<String>();
		for (String arg: args){
			if (arg!=null){
				result.add(arg);
			}
		}
		return result;
	}

}
