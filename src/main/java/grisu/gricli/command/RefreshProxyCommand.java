package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.utils.WalltimeUtils;
import grith.jgrith.cred.Cred;

public class RefreshProxyCommand implements GricliCommand {

	@SyntaxDescription(command = { "renew", "session" })
	public RefreshProxyCommand() {

	}

	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {

		Cred cred = env.getGrisuRegistry().getCredential();

		String[] remaining = WalltimeUtils
				.convertSecondsInHumanReadableString(cred
						.getRemainingLifetime());
		env.printMessage("Old session lifetime: " + remaining[0] + " "
				+ remaining[1]);

		boolean refreshed = cred.refresh();

		if (refreshed) {

			remaining = WalltimeUtils.convertSecondsInHumanReadableString(cred
					.getRemainingLifetime());
			env.printMessage("New session lifetime: " + remaining[0] + " "
					+ remaining[1]);

		} else {
			env.printMessage("Session could not be refreshed...");
		}


	}

}
