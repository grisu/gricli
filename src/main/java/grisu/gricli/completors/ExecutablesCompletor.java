package grisu.gricli.completors;

import grisu.gricli.Gricli;
import grisu.gricli.environment.GricliEnvironment;

import java.util.List;

import jline.Completor;

public class ExecutablesCompletor implements Completor {

	public int complete(String s, int i, List list) {

		GricliEnvironment env = Gricli.completionCache.getEnvironment();

		// env.getGrisuRegistry().getResourceInformation().get

		// TODO Auto-generated method stub
		return -1;
	}

}
