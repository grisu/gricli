package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginException;
import grisu.frontend.control.login.LoginManager;
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

			// try {
			// Credential c = Credential.load();
			//
			// if (!c.isValid()) {
			// throw new CredentialException(
			// "No valid session found and interactive login not available for scripted execution. Please login manually and try again.");
			// }
			// } catch (CredentialException ce) {
			// throw new GricliRuntimeException(ce.getLocalizedMessage());
			// }

			final ServiceInterface serviceInterface = LoginManager
					.loginCommandlineLocalProxy(siUrl);

			return InteractiveLoginCommand.login(env, serviceInterface);

		} catch (final LoginException ex) {
			throw new GricliRuntimeException(ex);
		}
	}

}
