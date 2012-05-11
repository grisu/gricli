package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

public class PrintGroupsCommand implements GricliCommand {

	@SyntaxDescription(command = { "print", "groups" })
	public PrintGroupsCommand() {
	}

	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {

		for (final String fqan : env.getGrisuRegistry()
				.getUserEnvironmentManager().getAllAvailableFqans(true)) {
			env.printMessage(fqan);
		}

	}

}
