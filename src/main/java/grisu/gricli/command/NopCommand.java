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

	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {

	}

}
