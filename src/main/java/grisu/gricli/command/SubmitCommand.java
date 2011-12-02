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
import grisu.jcommons.view.cli.CliHelpers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubmitCommand implements GricliCommand, PropertyChangeListener {

	private final String[] args;

	private String submitHandle;

	private final Logger myLogger = LoggerFactory
			.getLogger(SubmitCommand.class);

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

		if (!isAsync()) {
			CliHelpers.setIndeterminateProgress("Creating job on backend...",
					true);
		}
		try {

			final JobObject job = env.getJob();

			job.setCommandline(getCommandline());

			job.createJob(env.group.get(), Constants.UNIQUE_NUMBER_METHOD);
			return job;
		} catch (final JobPropertiesException ex) {
			CliHelpers.setIndeterminateProgress(false);
			throw new GricliRuntimeException("job property not valid: "
					+ ex.getMessage(), ex);
		}

	}

	public GricliEnvironment execute(final GricliEnvironment env)
			throws GricliRuntimeException {

		final JobObject job = createJob(env);

		final String jobname = job.getJobname();
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
			env.printMessage("Submitting job in background, jobname: "
					+ jobname);
		} else {
			submit(job, true);
			env.printMessage("Job submitted, jobname " + jobname);
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

	public synchronized void propertyChange(PropertyChangeEvent evt) {

		try {

			String oldValue = null;
			if (evt.getOldValue() != null) {
				try {
					oldValue = (String) evt.getOldValue();
				} catch (final Exception e) {
				}
			}
			String newValue = null;
			if (evt.getNewValue() != null) {
				try {
					newValue = (String) evt.getNewValue();
				} catch (final Exception e) {
					try {
						newValue = ((Integer) evt.getNewValue()).toString();
					} catch (final Exception e1) {
					}
				}
			}

			final String propName = evt.getPropertyName();

			String text = null;

			if ("submissionLog".equals(propName)) {
				final List<String> log = (List<String>) evt.getNewValue();
				text = log.get(log.size() - 1);
				if (text.startsWith("Submission site is:")
						|| text.startsWith("Submission queue is: ")
						|| text.startsWith("Job directory url is")) {
					return;
				}
			} else if (Constants.STATUS_STRING.equals(propName)) {
				return;
			} else if ("statusString".equals(propName)) {
				return;
				// text = "New status: " + newValue;
			} else if (StringUtils.isBlank(oldValue)
					&& StringUtils.isNotBlank(newValue)) {
				text = "Set " + propName + ": " + newValue;
			} else if (StringUtils.isNotBlank(oldValue)
					&& StringUtils.isNotBlank(newValue)) {
				text = "Changed value for " + propName + ": " + oldValue
						+ " -> " + newValue;
			}
			if (StringUtils.isNotBlank(text)) {
				CliHelpers.setIndeterminateProgress(text, true);
			}
		} catch (final Exception e) {
			myLogger.error(e.getLocalizedMessage(), e);
		}



	}

	private void submit(JobObject job, boolean wait)
			throws GricliRuntimeException {

		if (wait) {
			job.addPropertyChangeListener(this);
			CliHelpers.setIndeterminateProgress(
					"Submitting job " + job.getJobname(), true);
		}

		try {
			submitHandle = job.submitJob(null, wait);
			if (wait) {
				job.removePropertyChangeListener(this);
				CliHelpers.setIndeterminateProgress(false);
			}
		} catch (final JobSubmissionException e) {
			if (wait) {
				job.removePropertyChangeListener(this);
				CliHelpers.setIndeterminateProgress(false);
			}
			throw new GricliRuntimeException("fail to submit job: "
					+ e.getMessage(), e);
		} catch (final InterruptedException e) {
			if (wait) {
				job.removePropertyChangeListener(this);
				CliHelpers.setIndeterminateProgress(false);
			}
			throw new GricliRuntimeException("jobmission was interrupted: "
					+ e.getMessage(), e);
		}
	}

}
