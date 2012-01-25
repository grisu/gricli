package grisu.gricli.completors;

import grisu.gricli.Gricli;
import grisu.model.status.StatusObject;

import java.util.List;
import java.util.Set;

import jline.Completor;
import jline.SimpleCompletor;

import com.google.common.collect.Sets;

public class TaskIdCompletor implements Completor {

	@SuppressWarnings("unchecked")
	public int complete(String s, int i, List l) {

		Set<String> sos = Sets.newTreeSet();
		for (StatusObject so : Gricli.completionCache.getEnvironment()
				.getActiveMonitors()) {
			sos.add(so.getShortDesc());
		}
		for (StatusObject so : Gricli.completionCache.getEnvironment()
				.getFinishedMonitors()) {
			sos.add(so.getShortDesc());
		}
		return new SimpleCompletor(sos
				.toArray(new String[] {})).complete(s, i, l);
	}
}
