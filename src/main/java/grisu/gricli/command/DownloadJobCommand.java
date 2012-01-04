package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.NoSuchJobException;
import grisu.frontend.control.clientexceptions.FileTransactionException;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.JobnameCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.constants.Constants;
import grisu.model.FileManager;
import grisu.utils.ServiceInterfaceUtils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class DownloadJobCommand implements GricliCommand {
	private final String jobFilter;
	private final String target;
	@SyntaxDescription(command = { "download", "job" }, arguments = { "jobname" })
	@AutoComplete(completors = { JobnameCompletor.class })
	public DownloadJobCommand(String jobFilter) {
		this.jobFilter = jobFilter;
		this.target = null;
	}

	@SyntaxDescription(command = { "download", "job" }, arguments = {
			"jobname", "target" })
	@AutoComplete(completors = { JobnameCompletor.class,
			LocalFolderCompletor.class })
	public DownloadJobCommand(String jobFilter, String targetDir){
		this.jobFilter = jobFilter;
		this.target = targetDir;
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
		String normalDirName = null;
		if (StringUtils.isBlank(target)) {
			normalDirName = StringUtils.replace(env.dir.get()
					.toString(), "~", System.getProperty("user.home"));
		} else {
			File targetDir = new File(target);
			if (targetDir.exists()) {
				targetDir.mkdirs();
				if (!targetDir.exists()) {
					throw new GricliRuntimeException("Can't create target dir "
							+ target);
				}
			}
			normalDirName = targetDir.getAbsolutePath();

		}

		// check whether one of the target dirs already exits..
		for (final String jobname : ServiceInterfaceUtils.filterJobNames(si,
				jobFilter)) {
			File targetTemp = new File(target, jobname);
			if (targetTemp.exists()) {
				throw new GricliRuntimeException("Can't download job '"
						+ jobname + "': target dir '"
						+ targetTemp.getAbsolutePath() + "' already exists.");
			}
		}

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
