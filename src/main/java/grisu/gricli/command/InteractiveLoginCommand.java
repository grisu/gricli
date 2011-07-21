package grisu.gricli.command;


import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginException;
import grisu.frontend.control.login.LoginManager;
import grisu.gricli.Gricli;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.BackendCompletor;
import grisu.gricli.completors.CompletionCache;
import grisu.gricli.completors.CompletionCacheImpl;
import grisu.model.GrisuRegistryManager;

import java.io.File;

import org.apache.commons.lang.StringUtils;

public class InteractiveLoginCommand implements
GricliCommand {
	private final String backend;

	@SyntaxDescription(command={"ilogin"},arguments={"backend"})
	@AutoComplete(completors={BackendCompletor.class})
	public InteractiveLoginCommand(String backend) {
		this.backend = backend;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		try {
			ServiceInterface si = LoginManager.loginCommandline(backend);
			env.setServiceInterface(si);

			CompletionCache cc = new CompletionCacheImpl(env);
			GrisuRegistryManager.getDefault(si).set(
					Gricli.COMPLETION_CACHE_REGISTRY_KEY, cc);

			Gricli.completionCache = cc;

			new ChdirCommand(System.getProperty("user.dir")).execute(env);

			String value = System
					.getenv(LocalLoginCommand.GRICLI_LOGIN_SCRIPT_ENV_NAME);
			if (StringUtils
					.isNotBlank(LocalLoginCommand.GRICLI_LOGIN_SCRIPT_ENV_NAME)) {
				File script = new File(value);
				if (script.canExecute()) {
					new ExecCommand(script.getPath()).execute(env);
				}
			}

			// CompletionCache.jobnames = si.getAllJobnames(null).asSortedSet();
			// CompletionCache.fqans = si.getFqans().asSortedSet();
			// CompletionCache.queues = si.getAllSubmissionLocations()
			// .asSubmissionLocationStrings();
			// CompletionCache.sites = si.getAllSites().asArray();

			return env;
		} catch (LoginException ex) {
			throw new GricliRuntimeException(ex);
		}
	}

}
