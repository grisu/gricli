package grisu.gricli.completors;

import java.util.List;

import jline.Completor;
import jline.MultiCompletor;

public class ViewCompletor implements Completor {
	final JobnameCompletor jnc = new JobnameCompletor();
	final FileCompletor fc = new FileCompletor();
	final Completor c = new MultiCompletor(new Completor[] { jnc, fc });

	public int complete(String arg0, int arg1, List arg2) {
		fc.setDisplayLocalFile(false);
		return c.complete(arg0, arg1, arg2);
	}

}
