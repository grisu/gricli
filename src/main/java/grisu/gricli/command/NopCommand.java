package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;


/**
 * 
 * do nothing
 */
public class NopCommand implements GricliCommand {


	@SyntaxDescription(command={})
	public NopCommand(){
		super();
	}

	public GricliEnvironment execute(GricliEnvironment env)
	throws GricliRuntimeException {
		return env;
	}

}

