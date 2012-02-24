package grisu.gricli.completors;

import grisu.gricli.Gricli;
import grisu.gricli.LoginRequiredException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.constants.Constants;
import grisu.model.info.ApplicationInformation;
import grisu.model.job.JobSubmissionObjectImpl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jline.Completor;
import jline.SimpleCompletor;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueCompletor implements Completor {

	private final Logger myLogger = LoggerFactory
			.getLogger(QueueCompletor.class);

	public int complete(String s, int i, List l) {

		final GricliEnvironment env = Gricli.completionCache.getEnvironment();
		final String app = env.application.get();
		// String queue = env.get("queue");
		final String fqan = env.group.get();

		if (StringUtils.isBlank(fqan)) {
			return new SimpleCompletor(Gricli.completionCache.getAllQueues()
					.toArray(new String[] {})).complete(s, i, l);
		}

		// if (StringUtils.isBlank(app)
		// || Constants.GENERIC_APPLICATION_NAME.equals(app)) {
		// // no versions here
		// return new SimpleCompletor(
		// Gricli.completionCache.getAllQueuesForFqan(fqan)).complete(
		// s, i, l);
		//
		// } else {
		final ApplicationInformation ai = env.getGrisuRegistry()
				.getApplicationInformation(app);

		JobSubmissionObjectImpl job;
		try {
			job = env.getJob();
		} catch (LoginRequiredException e) {
			myLogger.debug("Can't complete queues: " + e.getLocalizedMessage());
			return -1;
		}

		final Set<String> queues = ai.getQueues(
				job.getJobSubmissionPropertyMap(), fqan);
		// .getAvailableSubmissionLocationsForFqan(fqan);
		final List<String> q = new LinkedList<String>(queues);
		Collections.sort(q);
		q.add(0, Constants.NO_SUBMISSION_LOCATION_INDICATOR_STRING);
		return new SimpleCompletor(q.toArray(new String[] {})).complete(s,
				i, l);

		// }

	}

}
