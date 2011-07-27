package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

public class QuitCommand implements GricliCommand {

	@SyntaxDescription(command={"quit"})
	public QuitCommand(){
	}

	public GricliEnvironment execute(GricliEnvironment env)
	throws GricliRuntimeException {
		System.exit(0);
		return env;
	}

}
