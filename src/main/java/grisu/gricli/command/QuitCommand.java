package grisu.gricli.command;

import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

public class QuitCommand implements GricliCommand {

	@SyntaxDescription(command = { "quit" })
	public QuitCommand() {
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		Gricli.shutdown(env);
		System.exit(0);
		return env;
	}

}
