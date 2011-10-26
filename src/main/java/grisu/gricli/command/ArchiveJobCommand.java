package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.JobPropertiesException;
import grisu.control.exceptions.NoSuchJobException;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.frontend.view.cli.CliHelpers;
import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.JobnameCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.gricli.util.ServiceInterfaceUtils;
import grisu.jcommons.constants.Constants;
import grisu.model.status.StatusObject;

import java.util.List;

public class ArchiveJobCommand implements GricliCommand {
	private final String jobFilter;

	@SyntaxDescription(command = { "archive", "job" }, arguments = { "jobname" })
	@AutoComplete(completors = { JobnameCompletor.class })
	public ArchiveJobCommand(String jobFilter) {
		this.jobFilter = jobFilter;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		final ServiceInterface si = env.getServiceInterface();
		si.setUserProperty(Constants.DEFAULT_JOB_ARCHIVE_LOCATION, null);
		String jobname = null;
		try {
			final List<String> jobnames = ServiceInterfaceUtils.filterJobNames(
					si, jobFilter);
			if (jobnames.size() == 0) {
				env.printError("No valid jobname specified.");
				return env;
			}
			for (final String j : jobnames) {

				CliHelpers.setIndeterminateProgress("Archiving job " + j
						+ "...", true);
				jobname = j;
				final String handle = si.archiveJob(j, null);
				StatusObject so = null;
				try {
					so = StatusObject.waitForActionToFinish(si, handle, 2,
							true, true);
					CliHelpers.setIndeterminateProgress("Job archived to: "
							+ handle, false);
				} catch (final Exception e) {
					CliHelpers.setIndeterminateProgress("Archiving failed; "
							+ e.getLocalizedMessage(), false);
					throw new GricliRuntimeException(e.getLocalizedMessage());
				}
				if (so.getStatus().isFailed()) {
					env.printError("Archiving of job failed: "
							+ so.getStatus().getErrorCause());
				}
			}
		} catch (final RemoteFileSystemException ex) {
			CliHelpers.setIndeterminateProgress(
					"Archiving failed; " + ex.getLocalizedMessage(), false);
			throw new GricliRuntimeException(ex);
		} catch (final NoSuchJobException ex) {
			CliHelpers.setIndeterminateProgress(
					"Archiving failed; " + ex.getLocalizedMessage(), false);
			throw new GricliRuntimeException("job " + jobname
					+ " does not exist");
		} catch (final JobPropertiesException ex) {
			CliHelpers.setIndeterminateProgress(
					"Archiving failed; " + ex.getLocalizedMessage(), false);
			throw new GricliRuntimeException(ex);
		} finally {
			Gricli.completionCache.refreshJobnames();
		}

		return env;
	}

}