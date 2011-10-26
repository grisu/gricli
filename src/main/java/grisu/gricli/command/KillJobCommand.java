package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.BatchJobException;
import grisu.control.exceptions.NoSuchJobException;
import grisu.frontend.view.cli.CliHelpers;
import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.JobnameCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.gricli.util.ServiceInterfaceUtils;
import grisu.model.dto.DtoStringList;
import grisu.model.status.ActionStatusEvent;
import grisu.model.status.StatusObject;

import java.util.List;

public class KillJobCommand implements GricliCommand, StatusObject.Listener {
	private final String jobFilter;
	private final boolean clean;

	@SyntaxDescription(command = { "kill", "jobs" })
	@AutoComplete(completors = { JobnameCompletor.class })
	public KillJobCommand() {
		this("*", true);
	}

	@SyntaxDescription(command = { "kill", "job" }, arguments = { "jobname" })
	@AutoComplete(completors = { JobnameCompletor.class })
	public KillJobCommand(String jobFilter) {
		this(jobFilter, false);
	}

	public KillJobCommand(String jobFilter, boolean clean) {
		this.jobFilter = jobFilter;
		this.clean = clean;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		final ServiceInterface si = env.getServiceInterface();

		final List<String> jobnames = ServiceInterfaceUtils.filterJobNames(si,
				jobFilter);

		if (jobnames.size() == 1) {
			final String j = jobnames.get(0);
			try {
				if (clean) {
					env.printMessage("cleaning job " + j);
				} else {
					env.printMessage("killing job " + j);
				}
				final String handle = si.kill(j, clean);
				StatusObject so = null;
				try {
					so = StatusObject.waitForActionToFinish(si, handle, 2,
							true, true);
				} catch (final Exception e) {
					throw new GricliRuntimeException(e.getLocalizedMessage());
				}
				if (so.getStatus().isFailed()) {
					env.printError("Killing of job failed: "
							+ so.getStatus().getErrorCause());
				}
			} catch (final NoSuchJobException ex) {
				env.printError("job " + j + " does not exist");
			} catch (final BatchJobException e) {
				env.printError("Error: " + e.getLocalizedMessage());
			}

		} else {
			final int no = jobnames.size();
			if (clean) {
				env.printMessage("Cleaning " + no + " jobs...");
			} else {
				env.printMessage("Killing " + no + " jobs...");
			}
			final String handle = si.killJobs(
					DtoStringList.fromStringColletion(jobnames), clean);
			final StatusObject so = new StatusObject(si, handle);
			try {
				so.addListener(this);
				so.waitForActionToFinish(2, false, true);
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
		}

		if (clean) {
			Gricli.completionCache.refreshJobnames();
			env.printMessage("Job(s) cleaned...                                 ");
		} else {
			env.printMessage("Job(s) killed...                                  ");
		}

		return env;
	}

	public void statusMessage(ActionStatusEvent event) {

		final int current = event.getActionStatus().getCurrentElements() / 2;
		final int total = event.getActionStatus().getTotalElements() / 2;

		CliHelpers.setProgress(current, total);
	}

}
