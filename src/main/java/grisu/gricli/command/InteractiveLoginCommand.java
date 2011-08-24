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

public class InteractiveLoginCommand implements
GricliCommand {
	public static GricliEnvironment login(GricliEnvironment env,
			ServiceInterface si) throws GricliRuntimeException {

		env.setServiceInterface(si);

		// setting up completion cache, loads some stuff in the background
		// too...
		CompletionCache cc = new CompletionCacheImpl(env);
		GrisuRegistryManager.getDefault(si).set(
				Gricli.COMPLETION_CACHE_REGISTRY_KEY, cc);
		Gricli.completionCache = cc;

		// initiate env so all is set to use proper current dir
		new ChdirCommand(System.getProperty("user.dir")).execute(env);

		// inititate env so all is set to use generic app as default
		new SetCommand("application", Constants.GENERIC_APPLICATION_NAME)
		.execute(env);

		// setting last used values
		String value = System
				.getenv(LocalLoginCommand.GRICLI_LOGIN_SCRIPT_ENV_NAME);
		if (StringUtils
				.isNotBlank(value)) {
			File script = new File(value);
			if (script.canExecute()) {
				new ExecCommand(script.getPath()).execute(env);
			}
		}

		// load # of active and finished jobs
		// CliHelpers.setIndeterminateProgress("Loading jobs...", true);
		StatusCommand sc = new StatusCommand();
		sc.execute(env);
		// CliHelpers.setIndeterminateProgress(false);

		return env;
	}

	private final String backend;

	@SyntaxDescription(command={"ilogin"},arguments={"backend"})
	@AutoComplete(completors={BackendCompletor.class})
	public InteractiveLoginCommand(String backend) {
		this.backend = backend;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {

		ServiceInterface si;
		try {
			si = LoginManager.loginCommandline(backend);
		} catch (LoginException ex) {
			throw new GricliRuntimeException(ex);
		}

		return login(env, si);

	}

}
