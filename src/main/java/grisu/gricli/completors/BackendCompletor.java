package grisu.gricli.completors;

import grisu.frontend.control.login.LoginManager;

import java.util.List;

import jline.Completor;
import jline.SimpleCompletor;

public class BackendCompletor implements Completor {
	SimpleCompletor sc;

	public BackendCompletor() {
		sc = new SimpleCompletor(LoginManager.SERVICEALIASES.keySet()
				.toArray(
				new String[] {}));
	}

	@SuppressWarnings("unchecked")
	public int complete(String s, int i, List l) {
		return sc.complete(s, i, l);
	}

}
