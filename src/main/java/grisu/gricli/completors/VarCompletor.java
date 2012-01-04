package grisu.gricli.completors;

import grisu.gricli.Gricli;

import java.util.List;

import jline.Completor;
import jline.SimpleCompletor;

public class VarCompletor implements Completor {

	public VarCompletor() {
	}

	@SuppressWarnings("unchecked")
	public int complete(String s, int i, List l) {
		final SimpleCompletor sc = new SimpleCompletor(Gricli.completionCache
				.getEnvironment().getVariableNames().toArray(new String[] {}));
		return sc.complete(s, i, l);
	}

}
