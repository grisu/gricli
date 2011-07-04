package grisu.gricli.completors;

import grisu.gricli.Gricli;
import grisu.gricli.GricliEnvironment;
import grisu.jcommons.constants.Constants;
import grisu.model.info.ApplicationInformation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jline.Completor;
import jline.SimpleCompletor;

import org.apache.commons.lang.StringUtils;

public class ApplicationVersionCompletor implements Completor {

	private final SimpleCompletor anyCompletor = new SimpleCompletor(
			new String(Constants.NO_VERSION_INDICATOR_STRING));

	public ApplicationVersionCompletor() {
	}

	public int complete(String arg0, int arg1, List arg2) {

		GricliEnvironment env = Gricli.completionCache.getEnvironment();
		String app = env.get(Constants.APPLICATIONNAME_KEY);
		// String queue = env.get("queue");
		String fqan = env.get("group");

		if (StringUtils.isBlank(fqan)) {
			// return anyCompletor.complete(arg0, arg1, arg2);
			return -1;
		}
		if (StringUtils.isBlank(app)||Constants.GENERIC_APPLICATION_NAME.equals(app)) {
			// no versions here
			// return anyCompletor.complete(arg0, arg1, arg2);
			return -1;
		} else {
			ApplicationInformation ai = env.getGrisuRegistry()
					.getApplicationInformation(app);

			Set<String> versions = ai.getAllAvailableVersionsForFqan(fqan);
			List<String> v = new LinkedList<String>(versions);
			Collections.sort(v);
			v.add(0, Constants.NO_VERSION_INDICATOR_STRING);
			return new SimpleCompletor(v.toArray(new String[] {}))
			.complete(arg0, arg1, arg2);

		}

	}

}
