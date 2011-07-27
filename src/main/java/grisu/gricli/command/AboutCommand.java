package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

public class AboutCommand implements GricliCommand {

	@SyntaxDescription(command={"about"})
	public AboutCommand(){}

	public GricliEnvironment execute(GricliEnvironment env)
	throws GricliRuntimeException {
		env.printMessage("version: " + grisu.jcommons.utils.Version.get("gricli"));
		env.printMessage("grisu frontend version: "
				+ grisu.jcommons.utils.Version.get("grisu-client"));
		env.printMessage("grisu backend: "
				+ env.getServiceInterface().getInterfaceInfo("NAME"));
		env.printMessage("grisu backend host: "
				+ env.getServiceInterface().getInterfaceInfo("HOSTNAME"));
		env.printMessage("grisu backend version: "
				+ env.getServiceInterface().getInterfaceInfo("VERSION"));
		env.printMessage("documentation: https://github.com/grisu/gricli");
		env.printMessage("contact: eresearch-admin@list.auckland.ac.nz");
		return env;
	}

}
