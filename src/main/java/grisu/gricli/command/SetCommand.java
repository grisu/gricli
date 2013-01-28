package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.VarCompletor;
import grisu.gricli.completors.VarValueCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.gricli.environment.GricliVar;
import grisu.jcommons.constants.Constants;
import grisu.jcommons.constants.JobSubmissionProperty;
import grisu.model.info.ApplicationInformation;
import grisu.model.info.dto.DtoProperty;
import grisu.model.info.dto.JobQueueMatch;
import grisu.model.info.dto.Queue;
import grisu.model.job.JobDescription;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class SetCommand implements GricliCommand {

	static final Logger myLogger = LoggerFactory.getLogger(SetCommand.class
			.getName());

	private final String global;
	private final String[] values;

	@SyntaxDescription(command = { "unset" }, arguments = { "var" })
	@AutoComplete(completors = { VarCompletor.class })
	public SetCommand(String global) {
		this(global, null);
	}

	@SyntaxDescription(command = { "set" }, arguments = { "var", "value" })
	@AutoComplete(completors = { VarCompletor.class, VarValueCompletor.class })
	public SetCommand(String global, String value) {
		this.global = global;
		this.values = new String[] { value };
	}

	public void execute(final GricliEnvironment env)
			throws GricliRuntimeException {

		validate(env);

		env.getVariable(global).set(values);

	}

	private void validate(GricliEnvironment env) throws GricliRuntimeException {

		if (Constants.QUEUE_KEY.equals(global)) {
			if (StringUtils.isBlank(values[0])
					|| Constants.NO_SUBMISSION_LOCATION_INDICATOR_STRING
					.equals(values[0])) {
				return;
			}

			JobDescription j = env.getJob();
			Map<JobSubmissionProperty, String> props = Maps.newHashMap(j
					.getJobSubmissionPropertyMap());

			final String fqan = env.group.get();
			final String app = env.application.get();
			final ApplicationInformation ai = env.getGrisuRegistry()
					.getApplicationInformation(app);
			final List<JobQueueMatch> allQueues = ai.getMatches(props, fqan);

			JobQueueMatch match = JobQueueMatch.getMatch(allQueues, values[0]);

			if (match == null) {
				throw new GricliRuntimeException("Queue '" + values[0]
						+ "' not a valid queuename.");
			}

			if (!match.isValid()) {
				String message = "\nQueue '" + values[0]
						+ "' not valid for current job setup:\n\n";
				for (DtoProperty prop : match.getPropertiesDetails()
						.getProperties()) {
					message = message
							+ ("\t" + prop.getKey() + ":\t" + prop.getValue() + "\n");
				}
				throw new GricliRuntimeException(message);

			}

		} else {

			GricliVar<String> queueVar = (GricliVar<String>) env
					.getVariable(Constants.QUEUE_KEY);
			String queue = queueVar.get();
			if (queue == null) {
				myLogger.debug("Queue not set, not checking whether global is valid in this context.");
				return;
			}

			GricliVar<?> var = env.getVariable(global);

			Object oldValue = var.get();

			env.getVariable(global).set(values);


			JobDescription j = env.getJob();
			Map<JobSubmissionProperty, String> props = Maps.newHashMap(j
					.getJobSubmissionPropertyMap());

			final String fqan = env.group.get();
			final String app = env.application.get();
			final ApplicationInformation ai = env.getGrisuRegistry()
					.getApplicationInformation(app);
			final List<Queue> queues = ai.getQueues(props, fqan);

			Queue q = Queue.getQueue(queues, queue);
			if (q == null) {

				env.getVariable(global).setValue(oldValue);

				throw new GricliRuntimeException(
						"Can't set global "
								+ global
								+ ": outside of specifications of currently set queue '"
								+ queue
								+ "'. Either change value or unset/change the queue.");
			}



		}

	}
}
