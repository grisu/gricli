package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.jcommons.constants.Constants;

/**
 * add value to  list
 */
public class ClearCacheCommand implements GricliCommand {
		


	@SyntaxDescription(command={"user","clearCache"},
			arguments={},
			help="clears grisu filesystem cache.")
	public ClearCacheCommand() {

	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		env.getServiceInterface().setUserProperty(Constants.CLEAR_MOUNTPOINT_CACHE, null);
		return env;
	}

}
