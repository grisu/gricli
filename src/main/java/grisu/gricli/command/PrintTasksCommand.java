package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.model.status.StatusObject;

public class PrintTasksCommand implements GricliCommand {

	// private final String global;

	@SyntaxDescription(command = { "print", "tasks" })
	public PrintTasksCommand() {
		// this(null);
	}

	// @SyntaxDescription(command = { "print", "task" }, arguments = {
	// "taskname" })
	// @AutoComplete(completors = { VarCompletor.class })
	// public PrintTasksCommand(String global) {
	// this.global = global;
	// }

	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {


		env.printMessage("Active tasks:");
		for (StatusObject so : env.getActiveMonitors()) {

			env.printMessage(so.getHandle() + ": "
					+ so.getStatus().percentFinished() + " %");

		}

		env.printMessage("");
		env.printMessage("Finished tasks:");
		for (StatusObject so : env.getFinishedMonitors()) {
			env.printMessage(so.getHandle() + ": " + !so.getStatus().isFailed());
		}

	}


}
