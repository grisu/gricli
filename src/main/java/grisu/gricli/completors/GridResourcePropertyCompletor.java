package grisu.gricli.completors;

import grisu.gricli.command.PrintQueuesCommand;

import java.util.List;

import jline.Completor;
import jline.SimpleCompletor;

public class GridResourcePropertyCompletor implements Completor {

	public int complete(String arg0, int arg1, List arg2) {
		return new SimpleCompletor(
				PrintQueuesCommand.getGridResourcePropertyNames()).complete(
				arg0, arg1, arg2);
	}

}
