package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.NoSuchJobException;
import grisu.frontend.control.clientexceptions.FileTransactionException;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.JobnameCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.gricli.util.ServiceInterfaceUtils;
import grisu.jcommons.constants.Constants;
import grisu.model.FileManager;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class DownloadJobCommand implements GricliCommand {
	private final String jobFilter;

	@SyntaxDescription(command = { "download", "job" }, arguments = { "jobname" })
	@AutoComplete(completors = { JobnameCompletor.class })
	public DownloadJobCommand(String jobFilter) {
		this.jobFilter = jobFilter;
	}

	private void downloadJob(GricliEnvironment env, ServiceInterface si,
			String jobname, String dst) throws GricliRuntimeException {

		try {
			final String jobdir = si.getJobProperty(jobname,
					Constants.JOBDIRECTORY_KEY);

			final FileManager fm = env.getGrisuRegistry().getFileManager();

			env.printMessage("Downloading job " + jobname + " to " + dst
					+ File.separator + jobname);

			fm.downloadUrl(jobdir, new File(dst), false);

		} catch (final NoSuchJobException nsje) {
			throw new GricliRuntimeException("Job " + jobname
					+ " does not exist");
		} catch (final IOException e) {
			throw new GricliRuntimeException(e.getLocalizedMessage());
		} catch (final FileTransactionException e) {
			throw new GricliRuntimeException(e.getLocalizedMessage());
		}

	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {

		boolean hasError = false;

		final ServiceInterface si = env.getServiceInterface();
		final String normalDirName = StringUtils.replace(env.dir.get()
				.toString(), "~", System.getProperty("user.home"));
		for (final String jobname : ServiceInterfaceUtils.filterJobNames(si,
				jobFilter)) {

			try {
				downloadJob(env, si, jobname, normalDirName);
			} catch (final GricliRuntimeException ex) {
				hasError = true;
				env.printError(ex.getMessage());
			}
		}

		return env;

	}

}
