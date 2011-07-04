package grisu.gricli.completors;

import java.util.List;

import jline.Completor;
import jline.SimpleCompletor;


public class JobnameCompletor implements Completor {

	@SuppressWarnings("unchecked")
	public int complete(String s, int i, List l) {
		return new SimpleCompletor(CompletionCache.singleton.getJobnames()
				.toArray(new String[] {})).complete(s, i, l);
	}
}
