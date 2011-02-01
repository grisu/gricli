package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginException;
import grisu.frontend.control.login.LoginManager;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;


public class LocalLoginCommand implements GricliCommand {
	private String siUrl;

	public LocalLoginCommand(String siUrl) {
		this.siUrl = siUrl;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		try {
			if (siUrl == null) {
				siUrl = env.getServiceInterfaceUrl();
			}
			ServiceInterface serviceInterface = LoginManager.login(siUrl);
			env.setServiceInterface(serviceInterface);
			return env;
		} catch (LoginException ex) {
			throw new GricliRuntimeException(ex);
		}
	}

}
