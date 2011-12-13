package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.utils.WalltimeUtils;
import grith.jgrith.Credential;

public class AboutCommand implements GricliCommand {

	@SyntaxDescription(command = { "about" })
	public AboutCommand() {
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {

		Credential c = env.getGrisuRegistry().getCredential();

		try {
			int remainingLifetime = c.getRemainingLifetime();
			String[] remainingString = WalltimeUtils
					.convertSecondsInHumanReadableString(remainingLifetime);
			env.printMessage("remaining session lifetime: "
					+ remainingString[0] + " " + remainingString[1]);
		} catch (Exception e) {
			env.printMessage("remaining session lifetime: can't determine ("
					+ e.getLocalizedMessage() + ")");
		}

		env.printMessage("version: "
				+ grisu.jcommons.utils.Version.get("gricli"));
		env.printMessage("grisu frontend version: "
				+ grisu.jcommons.utils.Version.get("grisu-client"));
		env.printMessage("grisu backend: "
				+ env.getServiceInterface().getInterfaceInfo("NAME"));
		env.printMessage("grisu backend host: "
				+ env.getServiceInterface().getInterfaceInfo("HOSTNAME"));
		env.printMessage("grisu backend version: "
				+ env.getServiceInterface().getInterfaceInfo("VERSION"));
		env.printMessage("documentation: https://github.com/grisu/gricli/wiki");
		env.printMessage("contact: eresearch-admin@list.auckland.ac.nz");
		return env;
	}

}
