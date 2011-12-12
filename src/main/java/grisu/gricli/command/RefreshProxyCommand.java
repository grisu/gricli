package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import grith.jgrith.Credential;

public class RefreshProxyCommand implements GricliCommand {

	@SyntaxDescription(command = { "renew", "session" })
	public RefreshProxyCommand() {

	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {

		Credential cred = env.getGrisuRegistry().getCredential();

		// String[] remaining = WalltimeUtils
		// .convertSecondsInHumanReadableString(cred
		// .getRemainingLifetime());
		// env.printMessage("Old session lifetime: " + remaining[0] + " "
		// + remaining[1]);

		cred.refresh();

		// remaining = WalltimeUtils.convertSecondsInHumanReadableString(cred
		// .getRemainingLifetime());
		// env.printMessage("New session lifetime: " + remaining[0] + " "
		// + remaining[1]);

		env.resetCredentialExpiry();

		return env;

	}

}
