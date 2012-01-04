package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

/**
 * 
 * do nothing
 */
public class NopCommand implements GricliCommand {

	@SyntaxDescription(command = {})
	public NopCommand() {
		super();
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		return env;
	}

}
