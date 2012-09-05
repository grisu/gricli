package grisu.gricli.completors;

import java.util.List;

import jline.Completor;

public class GridResourcePropertyCompletor implements Completor {

	public int complete(String arg0, int arg1, List arg2) {
		// return new SimpleCompletor(
		// PrintQueuesCommand.getGridResourcePropertyNames()).complete(
		// arg0, arg1, arg2);
		return arg1;
	}

}
