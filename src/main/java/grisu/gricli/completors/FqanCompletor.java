package grisu.gricli.completors;

import grisu.gricli.Gricli;

import java.util.List;

import jline.Completor;
import jline.SimpleCompletor;

public class FqanCompletor implements Completor {

	public int complete(String s, int i, List l) {
		return new SimpleCompletor(Gricli.completionCache.getAllFqans())
				.complete(s, i, l);
	}

}
