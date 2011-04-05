package grisu.gricli.command;


import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginException;
import grisu.frontend.control.login.LoginManager;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.BackendCompletor;
import grisu.gricli.completors.CompletionCache;


public class LocalLoginCommand implements GricliCommand {
	private String siUrl;

	@SyntaxDescription(command={"login"})
	@AutoComplete(completors={BackendCompletor.class})
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
			CompletionCache.jobnames = serviceInterface.getAllJobnames(null).asSortedSet();
			CompletionCache.fqans = serviceInterface.getFqans().asSortedSet();
			return env;
		} catch (LoginException ex) {
			throw new GricliRuntimeException(ex);
		}
	}

}
