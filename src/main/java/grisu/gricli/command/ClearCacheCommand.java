package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.constants.Constants;

/**
 * add value to list
 */
public class ClearCacheCommand implements GricliCommand {

	@SyntaxDescription(command = { "user", "clearCache" }, arguments = {})
	public ClearCacheCommand() {

	}

	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {
		env.getServiceInterface().setUserProperty(
				Constants.CLEAR_MOUNTPOINT_CACHE, null);

	}

}
