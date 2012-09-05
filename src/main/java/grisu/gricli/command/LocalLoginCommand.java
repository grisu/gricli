package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.BackendCompletor;
import grisu.gricli.environment.GricliEnvironment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalLoginCommand implements GricliCommand {

	static final Logger myLogger = LoggerFactory
			.getLogger(LocalLoginCommand.class.getName());


	public final static String GRICLI_LOGIN_SCRIPT_ENV_NAME = "GRICLI_LOGIN_SCRIPT";

	private String siUrl;

	private final String credentialConfigFile;
	private final String nameOfCredential;

	@SyntaxDescription(command = { "login" }, arguments = { "backend" })
	@AutoComplete(completors = { BackendCompletor.class })
	public LocalLoginCommand(String siUrl) {
		this.siUrl = siUrl;
		this.credentialConfigFile = null;
		this.nameOfCredential = null;
	}

	public LocalLoginCommand(String siUrl, String credConfig,
			String nameOfCredential) {
		this.siUrl = siUrl;
		this.credentialConfigFile = credConfig;
		this.nameOfCredential = nameOfCredential;
	}

	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {
		// try {
		// if (siUrl == null) {
		// siUrl = env.getServiceInterfaceUrl();
		// }
		//
		// if (StringUtils.isBlank(credentialConfigFile)) {
		//
		// // try {
		// // Credential c = Credential.load();
		// //
		// // if (!c.isValid()) {
		// // throw new CredentialException(
		// //
		// "No valid session found and interactive login not available for scripted execution. Please login manually and try again.");
		// // }
		// // } catch (CredentialException ce) {
		// // throw new GricliRuntimeException(ce.getLocalizedMessage());
		// // }
		//
		// final ServiceInterface serviceInterface = LoginManagerNew
		// .loginCommandlineLocalProxy(siUrl);
		//
		// InteractiveLoginCommand.login(env, serviceInterface);
		// } else {
		//
		// Map<String, Credential> creds = CredentialLoader
		// .loadCredentials(credentialConfigFile);
		//
		// if ((creds == null) || (creds.size() == 0)) {
		// throw new GricliRuntimeException(
		// "Could not create credential from specified credential config file.");
		// }
		//
		// Credential cred = null;
		// if (StringUtils.isBlank(nameOfCredential)) {
		// myLogger.debug("No credential specified, using first one.");
		// cred = creds.values().iterator().next();
		// } else {
		// cred = creds.get(nameOfCredential);
		// if (creds == null) {
		// throw new GricliRuntimeException(
		// "No credential with name "
		// + nameOfCredential
		// + " was created using credential config file "
		// + credentialConfigFile);
		// }
		// }
		//
		// final ServiceInterface serviceInterface = LoginManagerNew
		// .login(
		// cred, siUrl, true);
		// InteractiveLoginCommand.login(env, serviceInterface);
		//
		// }
		//
		// } catch (final LoginException ex) {
		// throw new GricliRuntimeException(ex);
		// }
	}

}
