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


public class LocalLoginCommand implements
GricliCommand {
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

			// CompletionCache.jobnames =
			// serviceInterface.getAllJobnames(null).asSortedSet();
			// CompletionCache.fqans =
			// serviceInterface.getFqans().asSortedSet();
			// CompletionCache.queues = serviceInterface
			// .getAllSubmissionLocations().asSubmissionLocationStrings();
			// CompletionCache.sites = serviceInterface.getAllSites().asArray();
			return env;
		} catch (LoginException ex) {
			throw new GricliRuntimeException(ex);
		}
	}

}
