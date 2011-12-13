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
import grisu.jcommons.view.cli.CliHelpers;
import grisu.model.GrisuRegistryManager;

import java.io.File;

import org.apache.commons.lang.StringUtils;

public class InteractiveLoginCommand implements GricliCommand {
	public static GricliEnvironment login(GricliEnvironment env,
			ServiceInterface si) throws GricliRuntimeException {

		CliHelpers.setIndeterminateProgress("Preparing gricli environment...",
				true);
		env.setServiceInterface(si);

		String[] fqans = null;
		try {
			// setting up completion cache, loads some stuff in the background
			// too...
			final CompletionCache cc = new CompletionCacheImpl(env);
			GrisuRegistryManager.getDefault(si).set(
					Gricli.COMPLETION_CACHE_REGISTRY_KEY, cc);
			Gricli.completionCache = cc;

			// initiate env so all is set to use proper current dir
			new ChdirCommand(System.getProperty("user.dir")).execute(env);

			// inititate env so all is set to use generic app as default
			env.application.set(Constants.GENERIC_APPLICATION_NAME);

			// setting last used values
			final String value = System
					.getenv(LocalLoginCommand.GRICLI_LOGIN_SCRIPT_ENV_NAME);
			if (StringUtils.isNotBlank(value)) {
				final File script = new File(value);
				if (script.canExecute()) {
					new ExecCommand(script.getPath()).execute(env);
				}
			}

			// load # of active and finished jobs
			// CliHelpers.setIndeterminateProgress("Loading jobs...", true);
			// StatusCommand sc = new StatusCommand();
			// sc.execute(env);
			// CliHelpers.setIndeterminateProgress(false);
			CliHelpers.setIndeterminateProgress(
					"Checking group memberships...", true);
			fqans = cc.getAllFqans();

		} finally {
			CliHelpers.setIndeterminateProgress(false);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			env.printMessage("Logged in.\n");
		}
		if ((fqans != null) && (fqans.length == 0)) {
			env.printMessage("You don't seem to be a member of any supported groups so you probably won't be able to access any resources. Please contact support.");
		}
		return env;
	}

	private final String backend;
	private final String username;
	private final String idp;
	private final boolean x509;

	@SyntaxDescription(command = { "ilogin" }, arguments = { "backend" })
	@AutoComplete(completors = { BackendCompletor.class })
	public InteractiveLoginCommand(String backend) {
		this(backend, false, null, null);
	}

	public InteractiveLoginCommand(String backend, boolean x509,
			String username, String idp) {
		this.backend = backend;
		this.username = username;
		this.idp = idp;
		this.x509 = x509;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {

		ServiceInterface si;
		try {
			if (StringUtils.isBlank(username) && !x509) {
				si = LoginManager.loginCommandline(backend, true,
						LoginManager.DEFAULT_PROXY_LIFETIME_IN_HOURS,
						Gricli.MINIMUM_PROXY_LIFETIME_BEFORE_RENEW_REQUEST);

			} else {
				if (x509) {
					si = LoginManager.loginCommandlineX509cert(backend,
							LoginManager.DEFAULT_PROXY_LIFETIME_IN_HOURS, true);
				} else if (StringUtils.isBlank(idp)
						&& StringUtils.isNotBlank(username)) {
					si = LoginManager.loginCommandlineMyProxy(backend,
							username,
							LoginManager.DEFAULT_PROXY_LIFETIME_IN_HOURS, true);

				} else if (StringUtils.isNotBlank(username)) {
					si = LoginManager.loginCommandlineShibboleth(backend,
							username, idp, true);
				} else {
					throw new GricliRuntimeException(
							"Could not determine which login method to use.");
				}
			}
		} catch (final LoginException ex) {
			throw new GricliRuntimeException(ex);
		}


		return login(env, si);

	}

}
