package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

public class PrintGroupsCommand implements GricliCommand {

	@SyntaxDescription(command = { "print", "groups" })
	public PrintGroupsCommand() {
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		for (final String fqan : env.getServiceInterface().getFqans()
				.asSortedSet()) {
			env.printMessage(fqan);
		}
		return null;
	}

}
