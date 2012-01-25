package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.JobPropertiesException;
import grisu.control.exceptions.NoSuchJobException;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.JobnameCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.constants.Constants;
import grisu.jcommons.view.cli.CliHelpers;
import grisu.model.status.StatusObject;
import grisu.utils.ServiceInterfaceUtils;

import java.util.List;
import java.util.Map;

import org.python.google.common.collect.Lists;
import org.python.google.common.collect.Maps;

public class ArchiveJobCommand implements GricliCommand {
	private final String[] jobFilters;

	private boolean async = false;

	@SyntaxDescription(command = { "archive", "job" }, arguments = { "jobnames" })
	@AutoComplete(completors = { JobnameCompletor.class })
	public ArchiveJobCommand(String... jobFilters) {
		this.jobFilters = jobFilters;
	}


	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {
		final ServiceInterface si = env.getServiceInterface();
		si.setUserProperty(Constants.DEFAULT_JOB_ARCHIVE_LOCATION, null);
		String jobname = null;
		try {

			if ((jobFilters.length > 0)
					&& jobFilters[jobFilters.length - 1].equals("&")) {
				async = true;
			}

			List<String> jobnames = Lists.newArrayList();

			for (String jobFilter : jobFilters) {

				if (!jobFilter.equals("&")) {
					final List<String> jobnamesTemp = ServiceInterfaceUtils
							.filterJobNames(si, jobFilter);
					jobnames.addAll(jobnamesTemp);
				}
			}
			if (jobnames.size() == 0) {
				env.printError("No valid jobname specified.");
				return;
			}

			Map<String, String> handles = Maps.newLinkedHashMap();

			try {
				for (final String j : jobnames) {

					CliHelpers.setIndeterminateProgress("Start archiving job "
							+ j
							+ "...", true);
					jobname = j;
					final String handle = si.archiveJob(j, null);
					handles.put(handle, jobname);

				}
			} finally {
				CliHelpers.setIndeterminateProgress(false);
			}

			for (String handle : handles.keySet()) {
				StatusObject so = null;
				try {

					so = new StatusObject(si, handle);
					so.setShortDesc("archving_job_" + handles.get(handle));
				} catch (Exception e1) {
					CliHelpers.setIndeterminateProgress("Archiving failed; "
							+ e1.getLocalizedMessage(), false);
					throw new GricliRuntimeException(e1.getLocalizedMessage());
				}
				if (async) {
					env.addTaskToMonitor(
							"Archiving of job " + handles.get(handle), so);
				} else {
					CliHelpers.setIndeterminateProgress(
							"Waiting for archiving of job "
									+ handles.get(handle) + "...", true);

					try {
						so.waitForActionToFinish(
								GricliEnvironment.STATUS_RECHECK_INTERVALL,
								true);
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
			}

			if (async) {
				env.printMessage("Archiving tasks submitted, running in background...");
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

	}

}