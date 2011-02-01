package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginException;
import grisu.frontend.control.login.LoginManager;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;


public class InteractiveLoginCommand implements GricliCommand {
	private final String backend;

	public InteractiveLoginCommand(String backend) {
		this.backend = backend;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		try {
			ServiceInterface si = LoginManager.loginCommandline(backend);
			env.setServiceInterface(si);
			return env;
		} catch (LoginException ex) {
			throw new GricliRuntimeException(ex);
		}
	}

}
