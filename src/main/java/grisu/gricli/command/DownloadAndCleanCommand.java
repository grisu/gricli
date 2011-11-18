package grisu.gricli.command;

import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.JobnameCompletor;
import grisu.gricli.environment.GricliEnvironment;

public class DownloadAndCleanCommand implements GricliCommand {

	private final String jobFilter;
	private final String target;

	@SyntaxDescription(command = { "downloadclean", "job" }, arguments = { "jobname" })
	@AutoComplete(completors = { JobnameCompletor.class })
	public DownloadAndCleanCommand(String jobFilter) {
		this.jobFilter = jobFilter;
		this.target = null;
	}

	@SyntaxDescription(command = { "downloadclean", "job" }, arguments = {
			"jobname", "target" })
	@AutoComplete(completors = { JobnameCompletor.class,
			LocalFolderCompletor.class })
	public DownloadAndCleanCommand(String jobFilter, String targetDir) {
		this.jobFilter = jobFilter;
		this.target = targetDir;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {

		//		for (final String jobname : ServiceInterfaceUtils.filterJobNames(
		//				env.getServiceInterface(), this.jobFilter)) {
		try {
			final DownloadJobCommand download = new DownloadJobCommand(
					jobFilter, target);
			env = download.execute(env);
			final CleanJobCommand clean = new CleanJobCommand(jobFilter);
			env = clean.execute(env);
			Gricli.completionCache.refreshJobnames();

		} catch (final GricliRuntimeException ex) {
			env.printError(ex.getMessage());
		}

		//		}

		return env;
	}

}
