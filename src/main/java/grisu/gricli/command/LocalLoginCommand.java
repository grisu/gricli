package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginException;
import grisu.frontend.control.login.LoginManager;
import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.BackendCompletor;
import grisu.gricli.environment.GricliEnvironment;

public class LocalLoginCommand implements GricliCommand {

	public final static String GRICLI_LOGIN_SCRIPT_ENV_NAME = "GRICLI_LOGIN_SCRIPT";

	private String siUrl;

	@SyntaxDescription(command = { "login" }, arguments = { "backend" })
	@AutoComplete(completors = { BackendCompletor.class })
	public LocalLoginCommand(String siUrl) {
		this.siUrl = siUrl;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		try {
			if (siUrl == null) {
				siUrl = env.getServiceInterfaceUrl();
			}
			final ServiceInterface serviceInterface = LoginManager
					.loginCommandline(siUrl, true,
							LoginManager.DEFAULT_PROXY_LIFETIME_IN_HOURS,
							Gricli.MINIMUM_PROXY_LIFETIME_BEFORE_RENEW_REQUEST);

			return InteractiveLoginCommand.login(env, serviceInterface);

		} catch (final LoginException ex) {
			throw new GricliRuntimeException(ex);
		}
	}

}
