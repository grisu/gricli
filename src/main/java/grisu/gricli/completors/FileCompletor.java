package grisu.gricli.completors;

import java.util.List;

import jline.Completor;
import jline.SimpleCompletor;

public class FileCompletor implements Completor {

	public int complete(String s, int i, List l) {
		return new SimpleCompletor(new String[] {})
		.complete(s, i, l);
	}

}
