package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

import java.io.File;

public class LogoutCommand implements
GricliCommand {


	@SyntaxDescription(command={"destroy","proxy"})
	public LogoutCommand() {
		super();
	}

	public GricliEnvironment execute(GricliEnvironment env)
	throws GricliRuntimeException {
		env.getServiceInterface().logout();
		String proxy = org.globus.common.CoGProperties.getDefault().getProxyFile();
		new File(proxy).delete();
		return env;
	}
}
