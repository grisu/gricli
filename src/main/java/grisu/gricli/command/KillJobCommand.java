package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.JobnameCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.view.cli.CliHelpers;
import grisu.model.dto.DtoStringList;
import grisu.model.status.ActionStatusEvent;
import grisu.model.status.StatusObject;

import java.util.Arrays;

public class KillJobCommand implements GricliCommand, StatusObject.Listener {

	private final String[] jobnames;
	private final boolean clean;

	private boolean deprecated = false;

	private final boolean async;

	// @SyntaxDescription(command = { "kill", "jobs" })
	// @AutoComplete(completors = { JobnameCompletor.class })
	public KillJobCommand() {
		this.deprecated = true;
		this.jobnames = null;
		this.async = false;
		this.clean = false;
	}

	public KillJobCommand(boolean clean, String... jobs) {

		if ((jobs != null) && (jobs.length > 0)
				&& jobs[jobs.length - 1].equals("&")) {
			this.jobnames = Arrays.copyOfRange(jobs, 0, jobs.length - 1);
			this.async = true;
		} else {
			this.jobnames = jobs;
			this.async = false;
		}
		this.clean = clean;
	}

	@SyntaxDescription(command = { "kill", "job" }, arguments = { "jobnames" })
	@AutoComplete(completors = { JobnameCompletor.class })
	public KillJobCommand(String... jobnames) {
		this(false, jobnames);
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {

		String cmd = "kill";
		if (clean) {
			cmd = "clean";
		}

		if (deprecated) {

			env.printMessage("\""
					+ cmd
					+ " jobs\" command is depreacted. Please use \""
					+ cmd
					+ " job [arg]\" instead. For more information on usage please type"
					+
					" \"help "
					+ cmd + "\" job");
			return env;
		}

		if ((jobnames == null) || (jobnames.length == 0)) {
			env.printError("Can't execute " + cmd
					+ " command. Please provide at least one jobname.");
			return env;

		}

		final ServiceInterface si = env.getServiceInterface();

		String handle = si.killJobs(DtoStringList.fromStringArray(jobnames),
				clean);


		final StatusObject so = new StatusObject(si, handle);

		final int no = so.getStatus().getTotalElements() / 2;

		String jobsString = "jobs";
		if (no == 1) {
			jobsString = "job";
		} else if (no == 0) {
			env.printMessage("No jobname matched provided argument(s). Nothing to do...");
			return env;
		}

		if (async) {
			if (clean) {
				env.addTaskToMonitor("Cleaning of " + no + " " + jobsString, so);
				env.printMessage("Cleaning of " + no + " " + jobsString
						+ " kicked off. Running in background...");
			} else {
				env.addTaskToMonitor("Killing of " + no + " " + jobsString, so);
				env.printMessage("Killing of " + no + " " + jobsString
						+ " kicked off. Running in background...");
			}
		} else {

			if (clean) {
				env.printMessage("Cleaning " + no + " " + jobsString + "...");
			} else {
				env.printMessage("Killing " + no + " " + jobsString + "...");
			}

			try {
				so.addListener(this);
				so.waitForActionToFinish(2, false);
				so.removeListener(this);
				CliHelpers.setProgress(no, no);
			} catch (final Exception e) {
				throw new GricliRuntimeException(e.getLocalizedMessage());
			}
			if (so.getStatus().isFailed()) {
				env.printError("Killing of job(s) failed: "
						+ so.getStatus().getErrorCause());
			}
			CliHelpers.writeToTerminal("");


			if (clean) {
				Gricli.completionCache.refreshJobnames();
				env.printMessage("Job(s) cleaned...                                 ");
			} else {
				env.printMessage("Job(s) killed...                                  ");
			}

		}
		return env;
	}

	public void statusMessage(ActionStatusEvent event) {

		final int current = event.getActionStatus().getCurrentElements() / 2;
		final int total = event.getActionStatus().getTotalElements() / 2;

		CliHelpers.setProgress(current, total);
	}

}
