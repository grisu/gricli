package grisu.gricli.command;

import grisu.control.exceptions.JobPropertiesException;
import grisu.control.exceptions.JobSubmissionException;
import grisu.frontend.model.job.JobObject;
import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.ExecutablesCompletor;
import grisu.gricli.completors.InputFileCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.constants.Constants;

import org.apache.commons.lang.StringEscapeUtils;

public class SubmitCommand implements GricliCommand {

	private final String[] args;

	private String submitHandle;

	@SyntaxDescription(command = { "submit" }, arguments = { "commandline" })
	@AutoComplete(completors = { ExecutablesCompletor.class,
			InputFileCompletor.class })
	public SubmitCommand(String... args) {
		this.args = args;
	}

	protected JobObject createJob(GricliEnvironment env)
			throws GricliRuntimeException {

		if (args.length == 0) {
			throw new GricliRuntimeException(
					"submit command requires at least one argument");
		}
		final JobObject job = env.getJob();

		job.setCommandline(getCommandline());

		try {
			job.createJob(env.group.get(), Constants.UNIQUE_NUMBER_METHOD);
			return job;
		} catch (final JobPropertiesException ex) {
			throw new GricliRuntimeException("job property not valid: "
					+ ex.getMessage(), ex);
		}

	}

	public GricliEnvironment execute(final GricliEnvironment env)
			throws GricliRuntimeException {
		final JobObject job = createJob(env);
		final String jobname = job.getJobname();
		env.printMessage(" job name is " + jobname);
		Gricli.completionCache.refreshJobnames();

		if (isAsync()) {
			new Thread() {
				@Override
				public void run() {
					try {
						submit(job, false);

						env.addTaskToMonitor("Job submission for job "
								+ jobname, submitHandle);
					} catch (final GricliRuntimeException ex) {/* do nothing */
					}
				}
			}.start();
		} else {
			submit(job, true);
		}

		return env;
	}

	public String getCommandline() {
		int length = this.args.length;
		final String last = this.args[this.args.length - 1];
		if ("&".equals(last)) {
			length--;
		}
		String cmd = "";
		for (int i = 0; i < length; i++) {
			String escaped = StringEscapeUtils.escapeJava(this.args[i]);
			// unscape forward slashes - bug in escapeJava
			escaped = escaped.replaceAll("\\\\\\/", "\\/");
			if (!this.args[i].equals(escaped) || escaped.contains(" ")) {
				escaped = "\"" + escaped + "\"";
			}
			cmd += " " + escaped;
		}
		return cmd.trim();
	}

	public boolean isAsync() {
		return "&".equals(this.args[this.args.length - 1]);
	}

	private void submit(JobObject job, boolean wait)
			throws GricliRuntimeException {
		try {
			submitHandle = job.submitJob(null, wait);
		} catch (final JobSubmissionException e) {
			throw new GricliRuntimeException("fail to submit job: "
					+ e.getMessage(), e);
		} catch (final InterruptedException e) {
			throw new GricliRuntimeException("jobmission was interrupted: "
					+ e.getMessage(), e);
		}
	}

}
