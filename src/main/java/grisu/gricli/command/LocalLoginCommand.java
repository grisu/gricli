package grisu.gricli.command;


import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginException;
import grisu.frontend.control.login.LoginManager;
import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.BackendCompletor;
import grisu.gricli.completors.CompletionCache;
import grisu.gricli.completors.CompletionCacheImpl;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.constants.Constants;
import grisu.model.GrisuRegistryManager;

import java.io.File;

import org.apache.commons.lang.StringUtils;

public class LocalLoginCommand implements
GricliCommand {

	public final static String GRICLI_LOGIN_SCRIPT_ENV_NAME = "GRICLI_LOGIN_SCRIPT";

	private String siUrl;

	@SyntaxDescription(command={"login"}, arguments={"backend"})
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

			CompletionCache cc = new CompletionCacheImpl(env);
			GrisuRegistryManager.getDefault(serviceInterface).set(
					Gricli.COMPLETION_CACHE_REGISTRY_KEY, cc);

			Gricli.completionCache = cc;

			new ChdirCommand(System.getProperty("user.dir")).execute(env);

			new SetCommand("application", Constants.GENERIC_APPLICATION_NAME)
			.execute(env);

			String value = System.getenv(GRICLI_LOGIN_SCRIPT_ENV_NAME);
			if (StringUtils.isNotBlank(value)) {
				File script = new File(value);
				if (script.canExecute()) {
					new ExecCommand(script.getPath()).execute(env);
				}
			}

			return env;
		} catch (LoginException ex) {
			throw new GricliRuntimeException(ex);
		}
	}

}
