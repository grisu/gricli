package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.TaskIdCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.utils.OutputHelpers;
import grisu.model.dto.DtoActionStatus;
import grisu.model.status.StatusObject;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.python.google.common.collect.Maps;

public class PrintTaskCommand implements GricliCommand {

	private final String taskId;

	@SyntaxDescription(command = { "print", "task" }, arguments = { "task_id" })
	@AutoComplete(completors = { TaskIdCompletor.class })
	public PrintTaskCommand(String task_id) {
		this.taskId = task_id;
	}


	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {

		env.printMessage("");

		for (StatusObject so : env.getActiveMonitors()) {

			if (!so.getShortDesc().equals(this.taskId)) {
				continue;
			}

			Map<String, String> table = Maps.newLinkedHashMap();

			env.printMessage(this.taskId + ":");
			env.printMessage("");
			String status = so.getStatus().getCurrentElements() + " / "
					+ so.getStatus().getTotalElements() + " ("
					+ so.getStatus().percentFinished() + " %)";
			table.put("Status", status);
			String desc = so.getDescription();
			if (StringUtils.isBlank(desc)) {
				desc = "n/a";
			}
			table.put("Description", desc);
			env.printMessage(OutputHelpers.getTable(table));
			env.printMessage("Log:");
			String logs = DtoActionStatus.getLogMessagesAsString(
					so.getStatus(), "\n\t");
			env.printMessage("\t" + logs);
			env.printMessage("");

		}

		for (StatusObject so : env.getFinishedMonitors()) {

			if (!so.getShortDesc().equals(this.taskId)) {
				continue;
			}

			Map<String, String> table = Maps.newLinkedHashMap();

			env.printMessage(this.taskId + ":");
			env.printMessage("");
			String status = (so.getStatus().isFailed() ? "Failed" : "Success");
			table.put("Status", status);
			String desc = so.getDescription();
			if (StringUtils.isBlank(desc)) {
				desc = "n/a";
			}
			table.put("Description", desc);
			if (so.getStatus().isFailed()) {
				String error = so.getStatus().getErrorCause();
				if (StringUtils.isBlank(error)) {
					error = "n/a";
				}
				table.put("Error", error);
			}
			env.printMessage(OutputHelpers.getTable(table));
			env.printMessage("Log:");
			String logs = DtoActionStatus
					.getLogMessagesAsString(so.getStatus(), "\n\t");
			env.printMessage("\t" + logs);
			env.printMessage("");
		}

	}


}
