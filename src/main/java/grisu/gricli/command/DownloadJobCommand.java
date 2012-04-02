package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.NoSuchJobException;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.JobnameCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.constants.Constants;
import grisu.model.FileManager;
import grisu.model.dto.DtoActionStatus;
import grisu.model.status.StatusObject;
import grisu.utils.ServiceInterfaceUtils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.python.google.common.base.Strings;

@SuppressWarnings("restriction")
public class DownloadJobCommand implements GricliCommand {
	public static String calculateTargetDir(String dir, String currentDir) throws GricliRuntimeException {

		String normalDirName = null;
		if (StringUtils.isBlank(dir)) {
			normalDirName = StringUtils.replace(currentDir, "~", System.getProperty("user.home"));
		} else {
			if (dir.startsWith("~")) {
				normalDirName = StringUtils.replaceOnce(dir, "~",
						System.getProperty("user.home"));
			} else {
				normalDirName = dir;
			}
			File targetDir = new File(normalDirName);
			if (targetDir.exists()) {
				targetDir.mkdirs();
				if (!targetDir.exists()) {
					throw new GricliRuntimeException("Can't create target dir "
							+ dir);
				}
			}
			normalDirName = targetDir.getAbsolutePath();

		}
		return normalDirName;
	}
	protected final String jobFilter;
	protected final String target;
	protected final String async;

	protected boolean clean;

	@SyntaxDescription(command = { "download", "job" }, arguments = { "jobname" })
	@AutoComplete(completors = { JobnameCompletor.class })
	public DownloadJobCommand(String jobFilter) {
		this.jobFilter = jobFilter;
		this.target = null;
		this.async = null;
		this.clean = false;
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
		this.clean = false;
	}

	@SyntaxDescription(command = { "download", "job" }, arguments = {
			"jobname", "target", "async" })
	@AutoComplete(completors = { JobnameCompletor.class,
			LocalFolderCompletor.class })
	public DownloadJobCommand(String jobFilter, String targetDir, String async) {

		this.jobFilter = jobFilter;
		this.target = targetDir;
		this.async = async;
		this.clean = false;
	}

	private void downloadJob(GricliEnvironment env, ServiceInterface si,
			String jobname, String dst) throws GricliRuntimeException {

		try {
			final String jobdir = si.getJobProperty(jobname,
					Constants.JOBDIRECTORY_KEY);

			final FileManager fm = env.getGrisuRegistry().getFileManager();

			if (async == null) {
				env.printMessage("Downloading job " + jobname + " to " + dst
						+ File.separator + jobname);
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


	public void execute(final GricliEnvironment env)
			throws GricliRuntimeException {

		if ((async != null) && !"&".equals(async)) {
			throw new GricliRuntimeException(
					"Last token needs to be \"&\" or local directory.");
		}

		boolean hasError = false;

		final ServiceInterface si = env.getServiceInterface();
		final List<String> jobnames = ServiceInterfaceUtils.filterJobNames(si,
				jobFilter);

		String normalDirName = null;

		try {

			normalDirName = calculateTargetDir(target, env.dir.get().toString());
			// check whether one of the target dirs already exits..
			for (final String jobname : jobnames) {
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
			for (final String jobname : jobnames) {

				try {
					downloadJob(env, si, jobname, normalDirName);

					if (clean) {
						CleanJobCommand c = new CleanJobCommand(jobname);
						env.printMessage("Cleaning job '" + jobname + "'...");
						c.execute(env);
					}

				} catch (final GricliRuntimeException ex) {
					hasError = true;
					env.printError(ex.getMessage());
				}
			}
			return;
		} else {
			final String normalDirNameFinal = normalDirName;
			int factor = 2;
			if (clean) {
				factor = 3;
			}

			String handle = "download_";
			Calendar cal = Calendar.getInstance();

			if ( clean ) {
				handle = handle + "and_clean_";
			}

			if (jobnames.size() == 1) {
				handle =  handle + jobnames.get(0);
			} else {
				handle = handle + jobnames.size() + "_jobs_"
						+ Strings
						.padStart(Integer.toString(cal
								.get(Calendar.HOUR_OF_DAY)), 2, '0')
								+ "_"
								+ Strings.padStart(Integer.toString(cal.get(Calendar.MINUTE)), 2, '0');
			}
			final DtoActionStatus status = new DtoActionStatus(handle,
					jobnames.size() * factor);
			if (jobnames.size() == 1) {
				status.setDescription("Downloading job '" + jobnames.get(0)
						+ "'");
			} else {
				status.setDescription("Downloading " + jobnames.size()
						+ " jobs");
			}
			Thread t = new Thread() {
				@Override
				public void run() {
					for (final String jobname : jobnames) {

						status.addElement("Starting download of job '"
								+ jobname + "'");
						try {
							downloadJob(env, si, jobname, normalDirNameFinal);
							// if (!clean) {
							// env.addNotification("Download of job " + jobname
							// + " to " + normalDirNameFinal
							// + " finished.");
							// }
							status.addElement("Job '"+jobname+"' downloaded");
						} catch (final GricliRuntimeException ex) {
							env.addNotification("Download of job "+jobname+" failed: "+ex.getLocalizedMessage());
							status.addElement("Job '" + jobname
									+ "' failed to download");
							status.addElement("Skipping cleaning of job '"
									+ jobname + "'");
							status.setFailed(true);
							continue;
						}
						try {

							if (clean) {
								CleanJobCommand c = new CleanJobCommand(jobname);
								c.setSilent();
								c.execute(env);
								// env.addNotification("Downloading and cleaning of job '"
								// + jobname + "' finished.");
							}
						} catch (final Exception ex) {
							env.addNotification("Cleaning of job '" + jobname
									+ "' failed: " + ex.getLocalizedMessage());
							status.addElement("Job '" + jobname
									+ "' failed to be cleaned");
							status.setFailed(true);
						}
					}
					status.setFinished(true);
				}
			};
			t.setName("download_job_async_"+new Date().getTime());
			StatusObject so = new StatusObject(status);
			so.setShortDesc(handle);
			env.addTaskToMonitor(so);
			t.start();
			if (clean) {
				env.printMessage("Downloading and cleaning jobs in background...");
			} else {
				env.printMessage("Downloading jobs in background...");
			}
			return;

		}
	}


}
