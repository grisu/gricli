package grisu.gricli.command;

import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.LoginRequiredException;
import grisu.gricli.completors.JobnameCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.utils.ServiceInterfaceUtils;

import java.util.Date;

public class DownloadAndCleanCommand implements GricliCommand {

	private final String jobFilter;
	private final String target;
	private final String async;

	@SyntaxDescription(command = { "downloadclean", "job" }, arguments = { "jobname" })
	@AutoComplete(completors = { JobnameCompletor.class })
	public DownloadAndCleanCommand(String jobFilter) {
		this.jobFilter = jobFilter;
		this.target = null;
		this.async = null;
	}

	@SyntaxDescription(command = { "downloadclean", "job" }, arguments = {
			"jobname", "target" })
	@AutoComplete(completors = { JobnameCompletor.class,
			LocalFolderCompletor.class })
	public DownloadAndCleanCommand(String jobFilter, String targetDir) {
		this.jobFilter = jobFilter;
		if ("&".equals(targetDir)) {
			this.async = "&";
			this.target = null;
		} else {
			this.async = null;
			this.target = targetDir;
		}
	}

	@SyntaxDescription(command = { "downloadclean", "job" }, arguments = {
			"jobname", "target", "async" })
	@AutoComplete(completors = { JobnameCompletor.class,
			LocalFolderCompletor.class })
	public DownloadAndCleanCommand(String jobFilter, String targetDir,
			String async) {

		this.jobFilter = jobFilter;
		this.target = targetDir;
		this.async = async;
	}

	public void execute(final GricliEnvironment env)
			throws GricliRuntimeException {

		if ((async != null) && !"&".equals(async)) {
			throw new GricliRuntimeException(
					"Last token needs to be \"&\" or local directory.");
		}

		//		for (final String jobname : ServiceInterfaceUtils.filterJobNames(
		//				env.getServiceInterface(), this.jobFilter)) {
		if (async == null) {
			try {
				final DownloadJobCommand download = new DownloadJobCommand(
						jobFilter, target);
				download.execute(env);
				final CleanJobCommand clean = new CleanJobCommand(jobFilter);
				clean.execute(env);
				Gricli.completionCache.refreshJobnames();

			} catch (final GricliRuntimeException ex) {
				env.printError(ex.getMessage());
			}
		} else {

			Thread t = new Thread() {
				@Override
				public void run() {

					try {
						for (final String jobname : ServiceInterfaceUtils
								.filterJobNames(env.getServiceInterface(),
										jobFilter)) {

						}
					} catch (LoginRequiredException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					try {
						final DownloadJobCommand download = new DownloadJobCommand(
								jobFilter, target);
						download.setSilent();
						download.execute(env);
						final CleanJobCommand clean = new CleanJobCommand(
								jobFilter);
						clean.setSilent();
						clean.execute(env);
						Gricli.completionCache.refreshJobnames();
						env.addNotification("Finished downloading and cleaning jobs.");
					} catch (final GricliRuntimeException ex) {
						env.addNotification("Download clean failed: "
								+ ex.getLocalizedMessage());
					}
				}
			};
			t.setName("download_clean_async_" + new Date().getTime());
			t.start();
			env.printMessage("Downloading and cleaning jobs in background...");
		}

	}

}
