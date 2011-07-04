package grisu.gricli.completors;

import grisu.gricli.Gricli;

import java.util.List;

import jline.Completor;
import jline.SimpleCompletor;

public class SiteCompletor implements Completor {

	public int complete(String s, int i, List l) {
		return new SimpleCompletor(Gricli.completionCache.getAllSites()
				.toArray(new String[] {})).complete(s, i, l);
	}

}
