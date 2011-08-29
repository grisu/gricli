package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

public class PwdCommand implements GricliCommand {

	private PrintGlobalsCommand c;

	@SyntaxDescription(command = {"pwd"})
	public PwdCommand(){
		c = new PrintGlobalsCommand("dir");
	}
	
	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		return c.execute(env);
	}

}
