package grisu.gricli.completors;

import java.util.List;
import java.util.SortedSet;

import jline.Completor;
import jline.FileNameCompletor;
import jline.MultiCompletor;
import jline.SimpleCompletor;

public class GridFilesystemCompletor implements Completor{

	public int complete(String s, int index, List l) {
		FileNameCompletor fc = new FileNameCompletor();
		SortedSet<String> fqans = CompletionCache.fqans;
		String[] gridUrls = new String[fqans.size()];
		int i = 0;
		for (String fqan: fqans){
			gridUrls[i] = "grid://Groups" + fqan;
			i++;
		}
		SimpleCompletor sc = new SimpleCompletor(gridUrls);
		Completor c = new MultiCompletor(new Completor[] {fc,sc});
		return c.complete(s,index,l);
	}

}
