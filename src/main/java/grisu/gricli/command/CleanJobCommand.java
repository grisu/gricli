package grisu.gricli.command;

import grisu.gricli.completors.JobnameCompletor;

public class CleanJobCommand extends KillJobCommand {

	// @SyntaxDescription(command = { "clean", "jobs" })
	// @AutoComplete(completors = { JobnameCompletor.class })
	public CleanJobCommand() {
		super();
	}

	@SyntaxDescription(command = { "clean", "job" }, arguments = { "jobnames" })
	@AutoComplete(completors = { JobnameCompletor.class })
	public CleanJobCommand(String... jobnames) {
		super(true, jobnames);
	}
}
