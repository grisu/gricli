package grisu.gricli.command;

import grisu.control.JobConstants;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.NoSuchJobException;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.JobPropertiesCompletor;
import grisu.gricli.completors.JobnameCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.gricli.util.ServiceInterfaceUtils;
import grisu.jcommons.constants.Constants;
import grisu.jcommons.constants.JobSubmissionProperty;
import grisu.model.dto.DtoJob;
import grisu.utils.WalltimeUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.python.google.common.collect.Maps;

public class PrintJobCommand implements GricliCommand {
	private final String jobname;
	private final String attribute;

	@SyntaxDescription(command = { "print", "jobs" })
	public PrintJobCommand() {
		this("*", "status");
	}

	@SyntaxDescription(command = { "print", "job" }, arguments = { "jobname" })
	@AutoComplete(completors = { JobnameCompletor.class })
	public PrintJobCommand(String jobname) {
		this(jobname, null);
	}

	@SyntaxDescription(command = { "print", "job" }, arguments = { "jobname",
			"attribute" })
	@AutoComplete(completors = { JobnameCompletor.class,
			JobPropertiesCompletor.class })
	public PrintJobCommand(String jobname, String attribute) {
		this.jobname = jobname;
		this.attribute = attribute;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		final ServiceInterface si = env.getServiceInterface();
		final List<String> jobNames = ServiceInterfaceUtils.filterJobNames(si,
				jobname);
		for (final String j : jobNames) {
			try {
				if (attribute != null) {
					if ((jobNames.size() > 1) || "*".equals(this.jobname)) {
						printJobAttribute(env, si, j, attribute, true);
					} else {
						printJobAttribute(env, si, j, attribute, false);
					}
				} else {
					printJob(env, si, j);
				}
			} catch (final NoSuchJobException ex) {
				throw new GricliRuntimeException("job " + j + " does not exist");
			}
		}

		return env;
	}

	private String formatAttribute(String aName, String aVal) {

		if (Constants.SUBMISSION_TIME_KEY.equals(aName)) {
			final Date d = new Date(Long.parseLong(aVal));
			return DateFormat.getInstance().format(d);
		} else if (Constants.MEMORY_IN_B_KEY.equals(aName)) {
			double memory = Long.parseLong(aVal);
			memory = memory / 1024.0 / 1024.0 / 1024.0;
			return String.format("%.2f GB", memory);

		} else if (Constants.WALLTIME_IN_MINUTES_KEY.equals(aName)) {
			final String[] strings = WalltimeUtils
					.convertSecondsInHumanReadableString(Integer.parseInt(aVal) * 60);
			return StringUtils.join(strings, " ");
		} else {
			return aVal;
		}
	}

	private void printJob(GricliEnvironment env, ServiceInterface si, String j)
			throws NoSuchJobException {
		final DtoJob job = si.getJob(j);
		env.printMessage("Printing details for job " + jobname + "/n");
		// env.printMessage("status: "
		// + JobConstants.translateStatus(si.getJobStatus(jobname)));
		final Map<String, String> props = job.propertiesAsMap();
		final Map<String, String> result = Maps.newTreeMap();

		result.put(Constants.STATUS_STRING,
				JobConstants.translateStatus(job.getStatus()));

		for (final String key : props.keySet()) {

			String valName = JobSubmissionProperty.getPrettyName(key);

			if (StringUtils.isBlank(valName)) {
				if (Constants.FQAN_KEY.equals(key)) {
					valName = "group";
				} else {
					valName = key;
				}
			}

			result.put(valName, formatAttribute(key, props.get(key)));

		}

		for (final String key : result.keySet()) {
			env.printError(key + " : " + result.get(key));
		}

		// String table = OutputHelpers.getTable(result);
		// env.printMessage(table);

	}

	private void printJobAttribute(GricliEnvironment env, ServiceInterface si,
			String j, String attribute, boolean displayJobName)
			throws NoSuchJobException {
		DtoJob job = null;
		try {
			job = si.getJob(j);
		} catch (final Exception e) {
			env.printError("Can't get job " + j + ": "
					+ e.getLocalizedMessage());
			return;
		}

		final JobSubmissionProperty p = JobSubmissionProperty
				.fromPrettyName(attribute);
		String prop = null;
		if (p != null) {
			prop = p.toString();
		}
		if (StringUtils.isBlank(prop)) {
			if ("group".equals(attribute)) {
				prop = Constants.FQAN_KEY;
			} else {
				prop = attribute;
			}
		}
		String msg = null;
		if ((Constants.STATUS_STRING.equals(prop))) {
			try {
				msg = JobConstants.translateStatus(si.getJobStatus(j));
			} catch (final Exception e) {
				msg = "n/a";
			}

		} else {
			msg = formatAttribute(prop, job.jobProperty(prop));
		}
		if (displayJobName) {
			env.printMessage(j + " : " + msg);
		} else {
			env.printMessage(msg);
		}
	}

}
