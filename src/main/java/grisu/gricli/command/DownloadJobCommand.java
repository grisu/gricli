package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.NoSuchJobException;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.JobnameCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.constants.Constants;
import grisu.model.FileManager;
import grisu.utils.ServiceInterfaceUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class DownloadJobCommand implements GricliCommand {
	private final String jobFilter;
	private final String target;
	private final String async;
	private boolean silent = false;

	@SyntaxDescription(command = { "download", "job" }, arguments = { "jobname" })
	@AutoComplete(completors = { JobnameCompletor.class })
	public DownloadJobCommand(String jobFilter) {
		this.jobFilter = jobFilter;
		this.target = null;
		this.async = null;
	}

	@SyntaxDescription(command = { "download", "job" }, arguments = {
			"jobname", "target" })
	@AutoComplete(completors = { JobnameCompletor.class,
			LocalFolderCompletor.class })
	public DownloadJobCommand(String jobFilter, String targetDir){
		this.jobFilter = jobFilter;
		if ("&".equals(targetDir)) {
			this.async = "&";
			this.target = null;
		} else {
			this.async = null;
			this.target = targetDir;
		}
	}

	@SyntaxDescription(command = { "download", "job" }, arguments = {
			"jobname", "target", "async" })
	@AutoComplete(completors = { JobnameCompletor.class,
			LocalFolderCompletor.class })
	public DownloadJobCommand(String jobFilter, String targetDir, String async) {

		this.jobFilter = jobFilter;
		this.target = targetDir;
		this.async = async;
	}

	private void downloadJob(GricliEnvironment env, ServiceInterface si,
			String jobname, String dst) throws GricliRuntimeException {

		try {
			final String jobdir = si.getJobProperty(jobname,
					Constants.JOBDIRECTORY_KEY);

			final FileManager fm = env.getGrisuRegistry().getFileManager();

			if (async == null) {
				if (!silent) {
					env.printMessage("Downloading job " + jobname + " to " + dst
							+ File.separator + jobname);
				}
			}

			fm.downloadUrl(jobdir, new File(dst), false);

		} catch (final NoSuchJobException nsje) {
			throw new GricliRuntimeException("Job " + jobname
					+ " does not exist");
		} catch (final IOException e) {
			throw new GricliRuntimeException(e.getLocalizedMessage());
		} catch (final Exception e) {
			throw new GricliRuntimeException(e.getLocalizedMessage());
		}

	}


	public GricliEnvironment execute(final GricliEnvironment env)
			throws GricliRuntimeException {

		if ((async != null) && !"&".equals(async)) {
			throw new GricliRuntimeException(
					"Last token needs to be \"&\" or local directory.");
		}

		boolean hasError = false;

		final ServiceInterface si = env.getServiceInterface();
		String normalDirName = null;
		try {
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

		} catch (RuntimeException re) {
			throw new GricliRuntimeException(re);
		}
		if ( async == null ) {
			for (final String jobname : ServiceInterfaceUtils.filterJobNames(si,
					jobFilter)) {

				try {
					downloadJob(env, si, jobname, normalDirName);
				} catch (final GricliRuntimeException ex) {
					hasError = true;
					if (!silent) {
						env.printError(ex.getMessage());
					}
				}
			}
			return env;
		} else {
			final String normalDirNameFinal = normalDirName;
			Thread t = new Thread() {
				@Override
				public void run() {
					for (final String jobname : ServiceInterfaceUtils.filterJobNames(si,
							jobFilter)) {

						try {
							downloadJob(env, si, jobname, normalDirNameFinal);
							env.addNotification("Download of job " + jobname
									+ " to " + normalDirNameFinal
									+ " finished.");
						} catch (final GricliRuntimeException ex) {
							env.addNotification("Download of job "+jobname+" failed: "+ex.getLocalizedMessage());
						}
					}
				}
			};
			t.setName("download_job_async_"+new Date().getTime());
			t.start();
			env.printMessage("Downloading jobs in background...");
			return env;

		}
	}

	public void setSilent() {
		this.silent = true;
	}

}
