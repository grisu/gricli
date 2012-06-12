package grisu.gricli.command;

import grisu.control.JobConstants;
import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.utils.OutputHelpers;
import grisu.model.dto.DtoJob;

import java.util.Map;
import java.util.SortedSet;

import com.google.common.collect.Maps;

public class StatusCommand implements GricliCommand {

	@SyntaxDescription(command = { "status" }, arguments = {})
	public StatusCommand() {
	}

	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {

		final SortedSet<DtoJob> jobs = Gricli.completionCache
				.getCurrentJobs(true);

		Integer active = 0;
		Integer finished = 0;
		Integer failed = 0;
		Integer success = 0;
		Integer unknown = 0;

		for (final DtoJob j : jobs) {
			if (j.getStatus() >= JobConstants.FINISHED_EITHER_WAY) {
				finished = finished + 1;
				if (j.getStatus() != JobConstants.DONE) {
					failed = failed + 1;
				} else {
					success = success + 1;
				}
			} else {
				if ((j.getStatus() < JobConstants.PENDING)
						|| (j.getStatus() == JobConstants.NO_SUCH_JOB)) {
					unknown = unknown + 1;
				} else {
					active = active + 1;
				}
			}
		}

		env.printMessage("Your jobs:\n");
		final Map<String, String> table = Maps.newLinkedHashMap();
		table.put("Active", active.toString());
		table.put("Finished", finished.toString());
		// if (failed > 0) {
		table.put("   Successful:", success.toString());
		table.put("   Failed:", failed.toString());
		// }
		table.put("Broken/Not found:", unknown.toString());

		final String msg = OutputHelpers.getTable(table);

		env.printMessage(msg);

	}

}
