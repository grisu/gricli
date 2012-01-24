package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

public class PrintMessagesCommand implements GricliCommand {

	@SyntaxDescription(command = { "print", "messages" })
	public PrintMessagesCommand() {
	}


	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {

		for (String message : env.getNotifications()) {
			env.printMessage(message);
		}

		env.getNotifications().clear();

	}

}
