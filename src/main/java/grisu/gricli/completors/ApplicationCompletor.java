package grisu.gricli.completors;

import grisu.gricli.Gricli;

import java.util.List;

import jline.Completor;
import jline.SimpleCompletor;

public class ApplicationCompletor implements Completor {

	public int complete(String s, int i, List l) {

		return new SimpleCompletor(Gricli.completionCache.getAllApplications())
				.complete(s, i, l);

	}

}
