package grisu.gricli.completors;

import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.command.PrintQueuesCommand;
import grisu.gricli.environment.GricliEnvironment;
import grisu.model.info.dto.JobQueueMatch;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jline.Completor;
import jline.SimpleCompletor;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Functions;
import com.google.common.collect.Collections2;

public class QueueCompletorNoAuto implements Completor {

	private final Logger myLogger = LoggerFactory
			.getLogger(QueueCompletorNoAuto.class);

	public int complete(String s, int i, List l) {

		final GricliEnvironment env = Gricli.completionCache.getEnvironment();

		List<JobQueueMatch> queues;
		try {
			queues = PrintQueuesCommand.calculateAllAvailableQueues(env);
		} catch (GricliRuntimeException e) {
			myLogger.error("Can't get autocompletion for queues: {}",
					e.getLocalizedMessage(), e);
			return -1;
		}
		// .getAvailableSubmissionLocationsForFqan(fqan);
		LinkedList<String> sublocs = null;
		sublocs = Lists.newLinkedList(Collections2.transform(queues,
				Functions.toStringFunction()));

		// final List<String> q = new LinkedList<String>(queues);
		Collections.sort(sublocs);
		return new SimpleCompletor(sublocs.toArray(new String[] {})).complete(
				s, i, l);

		// }

	}

}
