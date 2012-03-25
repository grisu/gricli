package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.utils.OutputHelpers;
import grisu.model.status.StatusObject;

import java.util.Map;

import org.python.google.common.collect.Maps;

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


		env.printMessage("");
		Map<String, String> table = Maps.newLinkedHashMap();

		if (env.getActiveMonitors().size() == 0) {
			env.printMessage("No active tasks.");
		} else {
			env.printMessage("Active tasks:");
			env.printMessage("");

			for (StatusObject so : env.getActiveMonitors()) {
				table.put(so.getShortDesc(), so.getStatus().percentFinished()
						+ " %");
			}

			env.printMessage(OutputHelpers.getTable(table));

		}
		env.printMessage("");
		if (env.getFinishedMonitors().size() == 0) {
			env.printMessage("No finished tasks.");
		} else {
			env.printMessage("Finished tasks:");
			env.printMessage("");
			table = Maps.newLinkedHashMap();
			for (StatusObject so : env.getFinishedMonitors()) {
				table.put(so.getShortDesc(),
						(so.getStatus().isFailed()) ? "failed"
								: "success");
			}
			env.printMessage(OutputHelpers.getTable(table));
		}
		env.printMessage("");

	}


}
