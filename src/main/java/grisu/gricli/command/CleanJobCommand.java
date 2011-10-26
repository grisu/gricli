package grisu.gricli.command;

import grisu.gricli.completors.JobnameCompletor;

public class CleanJobCommand extends KillJobCommand {

	@SyntaxDescription(command = { "clean", "jobs" })
	@AutoComplete(completors = { JobnameCompletor.class })
	public CleanJobCommand() {
		super("*", true);
	}

	@SyntaxDescription(command = { "clean", "job" }, arguments = { "jobname" })
	@AutoComplete(completors = { JobnameCompletor.class })
	public CleanJobCommand(String jobFilter) {
		super(jobFilter, true);
	}
}
