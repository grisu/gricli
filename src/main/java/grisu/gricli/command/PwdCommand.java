package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

public class PwdCommand implements GricliCommand {

	private final PrintGlobalsCommand c;

	@SyntaxDescription(command = { "pwd" })
	public PwdCommand() {
		c = new PrintGlobalsCommand("dir");
	}

	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {
		c.execute(env);
	}

}
