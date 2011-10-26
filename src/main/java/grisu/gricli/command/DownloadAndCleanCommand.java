package grisu.gricli.command;

import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.JobnameCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.gricli.util.ServiceInterfaceUtils;

public class DownloadAndCleanCommand implements GricliCommand {

	private final String jobFilter;

	@SyntaxDescription(command = { "downloadclean", "job" }, arguments = { "jobname" })
	@AutoComplete(completors = { JobnameCompletor.class })
	public DownloadAndCleanCommand(String jobFilter) {
		this.jobFilter = jobFilter;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {

		for (final String jobname : ServiceInterfaceUtils.filterJobNames(
				env.getServiceInterface(), this.jobFilter)) {
			try {
				final DownloadJobCommand download = new DownloadJobCommand(
						jobname);
				env = download.execute(env);
				final CleanJobCommand clean = new CleanJobCommand(jobname);
				env = clean.execute(env);
				Gricli.completionCache.refreshJobnames();

			} catch (final GricliRuntimeException ex) {
				env.printError(ex.getMessage());
			}

		}

		return env;
	}

}
