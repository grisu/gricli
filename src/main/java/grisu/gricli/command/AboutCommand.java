package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

public class AboutCommand implements GricliCommand {
	
	@SyntaxDescription(command={"about"})
	public AboutCommand(){}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		env.printMessage("version: " + grisu.jcommons.utils.Version.get("gricli"));
		env.printMessage("documentation: https://github.com/grisu/gricli");
		env.printMessage("contact: eresearch-admin@list.auckland.ac.nz");
		return env;
	}

}
