package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

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
