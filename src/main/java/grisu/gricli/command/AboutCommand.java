package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.constants.Constants;
import grisu.jcommons.utils.OutputHelpers;
import grisu.jcommons.utils.WalltimeUtils;
import grith.jgrith.cred.Cred;

import java.util.Map;

import org.python.google.common.collect.Maps;

public class AboutCommand implements GricliCommand {

	@SyntaxDescription(command = { "about" })
	public AboutCommand() {
	}

	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {

		Cred c = env.getGrisuRegistry().getCredential();

		Map<String, String> temp = Maps.newLinkedHashMap();

		try {
			int remainingLifetime = c.getRemainingLifetime();
			String[] remainingString = WalltimeUtils
					.convertSecondsInHumanReadableString(remainingLifetime);
			temp.put("Remaining session lifetime", remainingString[0] + " "
					+ remainingString[1] + " (" + remainingLifetime
					+ " seconds)");
			// env.printMessage("Remaining session lifetime: "
			// + remainingString[0] + " " + remainingString[1]);
		} catch (Exception e) {
			temp.put("Remaining session lifetime",
					"can't determine (" + e.getLocalizedMessage() + ")");
			// env.printMessage("Remaining session lifetime: can't determine ("
			// + e.getLocalizedMessage() + ")");
		}

		temp.put("User ID", env.getServiceInterface().getDN());
		// env.printMessage("User ID: " + env.getServiceInterface().getDN());

		temp.put("Gricli version", grisu.jcommons.utils.Version.get("gricli"));
		temp.put("Grisu frontend version",
				grisu.jcommons.utils.Version.get("grisu-client-lib"));
		temp.put("Grisu backend version", env.getServiceInterface().getInterfaceInfo("VERSION"));
		temp.put("Grisu backend",
				(String) env.getGrisuRegistry().get(Constants.BACKEND));
		temp.put("Grisu backend host", env.getServiceInterface()
				.getInterfaceInfo("HOSTNAME"));
		temp.put("Grisu backend type", env.getServiceInterface()
				.getInterfaceInfo("TYPE"));
		temp.put("Grisu API version", env.getServiceInterface().getInterfaceInfo("API_VERSION"));

		// env.printMessage("Gricli version: "
		// + grisu.jcommons.utils.Version.get("gricli"));
		// env.printMessage("Grisu frontend version: "
		// + grisu.jcommons.utils.Version.get("grisu-client"));
		// env.printMessage("Grisu backend version: "
		// + env.getServiceInterface().getInterfaceInfo("VERSION"));
		// env.printMessage("Grisu backend: "
		// + env.getGrisuRegistry().get(Constants.BACKEND));
		// env.printMessage("Grisu backend host: "
		// + env.getServiceInterface().getInterfaceInfo("HOSTNAME"));
		// env.printMessage("Grisu API version: "
		// + env.getServiceInterface().getInterfaceInfo("API_VERSION"));

		temp.put("Documentation", "https://github.com/grisu/gricli/wiki");
		temp.put("Contact", "eresearch-admin@list.auckland.ac.nz");

		// env.printMessage("documentation: https://github.com/grisu/gricli/wiki");
		// env.printMessage("contact: eresearch-admin@list.auckland.ac.nz");

		String output = OutputHelpers.getTable(temp);

		env.printMessage(output);

	}

}
