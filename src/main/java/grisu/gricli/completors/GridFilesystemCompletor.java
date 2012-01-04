package grisu.gricli.completors;

import grisu.gricli.Gricli;

import java.util.List;

import jline.Completor;
import jline.FileNameCompletor;
import jline.MultiCompletor;
import jline.SimpleCompletor;

public class GridFilesystemCompletor implements Completor {

	public int complete(String s, int index, List l) {
		final FileNameCompletor fc = new FileNameCompletor();
		final String[] fqans = Gricli.completionCache.getAllFqans();
		final String[] gridUrls = new String[fqans.length];
		int i = 0;
		for (final String fqan : fqans) {
			gridUrls[i] = "grid://Groups" + fqan;
			i++;
		}
		final SimpleCompletor sc = new SimpleCompletor(gridUrls);
		final Completor c = new MultiCompletor(new Completor[] { fc, sc });
		return c.complete(s, index, l);
	}

}
