package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.LoginRequiredException;
import grisu.gricli.environment.GricliEnvironment;

import java.io.File;

public class LogoutCommand implements GricliCommand {

	@SyntaxDescription(command = { "destroy", "proxy" })
	public LogoutCommand() {
		super();
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		try {
			env.getServiceInterface().logout();
		} catch (final LoginRequiredException l) {
			// do nothing as login session does not exist.
		}
		final String proxy = org.globus.common.CoGProperties.getDefault()
				.getProxyFile();
		new File(proxy).delete();
		return env;
	}
}
