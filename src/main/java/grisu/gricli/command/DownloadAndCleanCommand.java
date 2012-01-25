package grisu.gricli.command;

import grisu.gricli.completors.JobnameCompletor;

public class DownloadAndCleanCommand extends DownloadJobCommand implements
GricliCommand {


	@SyntaxDescription(command = { "downloadclean", "job" }, arguments = { "jobname" })
	@AutoComplete(completors = { JobnameCompletor.class })
	public DownloadAndCleanCommand(String jobFilter) {
		super(jobFilter);
		clean = true;
	}

	@SyntaxDescription(command = { "downloadclean", "job" }, arguments = {
			"jobname", "target" })
	@AutoComplete(completors = { JobnameCompletor.class,
			LocalFolderCompletor.class })
	public DownloadAndCleanCommand(String jobFilter, String targetDir) {
		super(jobFilter, targetDir);
		clean = true;
	}

	@SyntaxDescription(command = { "downloadclean", "job" }, arguments = {
			"jobname", "target", "async" })
	@AutoComplete(completors = { JobnameCompletor.class,
			LocalFolderCompletor.class })
	public DownloadAndCleanCommand(String jobFilter, String targetDir,
			String async) {

		super(jobFilter, targetDir, async);
		clean = true;
	}



}
