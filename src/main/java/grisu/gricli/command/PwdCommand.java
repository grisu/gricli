package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

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
