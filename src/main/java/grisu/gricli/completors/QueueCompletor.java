package grisu.gricli.completors;

import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.command.PrintQueuesCommand;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.constants.Constants;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jline.Completor;
import jline.SimpleCompletor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueCompletor implements Completor {

	private final Logger myLogger = LoggerFactory
			.getLogger(QueueCompletor.class);

	public int complete(String s, int i, List l) {

		final GricliEnvironment env = Gricli.completionCache.getEnvironment();

		Set<String> queues;
		try {
			queues = PrintQueuesCommand.calculateAllAvailableQueues(env);
		} catch (GricliRuntimeException e) {
			myLogger.error("Can't get autocompletion for queues: {}",
					e.getLocalizedMessage(), e);
			return -1;
		}
		// .getAvailableSubmissionLocationsForFqan(fqan);
		final List<String> q = new LinkedList<String>(queues);
		Collections.sort(q);
		q.add(0, Constants.NO_SUBMISSION_LOCATION_INDICATOR_STRING);
		return new SimpleCompletor(q.toArray(new String[] {})).complete(s,
				i, l);

		// }

	}

}
