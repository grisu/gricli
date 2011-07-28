package grisu.gricli.completors;

import grisu.gricli.Gricli;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.constants.Constants;
import grisu.model.info.ApplicationInformation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jline.Completor;
import jline.SimpleCompletor;

import org.apache.commons.lang.StringUtils;

public class QueueCompletor implements Completor {

	public int complete(String s, int i, List l) {

		GricliEnvironment env = Gricli.completionCache.getEnvironment();
		String app = env.application.get();
		// String queue = env.get("queue");
		String fqan = env.group.get();

		if (StringUtils.isBlank(fqan)) {
			return new SimpleCompletor(Gricli.completionCache.getAllQueues()
					.toArray(new String[] {})).complete(s, i, l);
		}

		if (StringUtils.isBlank(app)
				|| Constants.GENERIC_APPLICATION_NAME.equals(app)) {
			// no versions here
			return new SimpleCompletor(
					Gricli.completionCache.getAllQueuesForFqan(fqan)).complete(
					s, i, l);

		} else {
			ApplicationInformation ai = env.getGrisuRegistry()
					.getApplicationInformation(app);

			Set<String> queues = ai
					.getAvailableSubmissionLocationsForFqan(fqan);
			List<String> q = new LinkedList<String>(queues);
			Collections.sort(q);
			q.add(0, Constants.NO_SUBMISSION_LOCATION_INDICATOR_STRING);
			return new SimpleCompletor(q.toArray(new String[] {})).complete(
					s,
					i, l);

		}

	}

}
