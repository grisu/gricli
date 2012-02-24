package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.VarCompletor;
import grisu.gricli.completors.VarValueCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.gricli.environment.GricliVar;
import grisu.jcommons.constants.Constants;
import grisu.jcommons.constants.JobSubmissionProperty;
import grisu.model.info.ApplicationInformation;
import grisu.model.job.JobSubmissionObjectImpl;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.python.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

			JobSubmissionObjectImpl j = env.getJob();
			Map<JobSubmissionProperty, String> props = Maps.newHashMap(j
					.getJobSubmissionPropertyMap());

			final String fqan = env.group.get();
			final String app = env.application.get();
			final ApplicationInformation ai = env.getGrisuRegistry()
					.getApplicationInformation(app);
			final Set<String> allQueues = ai.getQueues(props, fqan);

			if (!allQueues.contains(values[0])) {
				throw new GricliRuntimeException("Queue '" + values[0]
								+ "' not a valid queuename or queue not available for the currently specified job parameters.");
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


			JobSubmissionObjectImpl j = env.getJob();
			Map<JobSubmissionProperty, String> props = Maps.newHashMap(j
					.getJobSubmissionPropertyMap());

			final String fqan = env.group.get();
			final String app = env.application.get();
			final ApplicationInformation ai = env.getGrisuRegistry()
					.getApplicationInformation(app);
			final Set<String> queues = ai.getQueues(props, fqan);


			if ((queues == null) || !queues.contains(queue)) {

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
