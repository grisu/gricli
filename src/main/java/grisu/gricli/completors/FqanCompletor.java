package grisu.gricli.completors;

import java.util.List;

import jline.Completor;
import jline.SimpleCompletor;

public class FqanCompletor implements Completor {

	public int complete(String s, int i, List l) {
		return new SimpleCompletor(CompletionCache.fqans.toArray(new String[] {})).complete(s, i, l);
	}

}
