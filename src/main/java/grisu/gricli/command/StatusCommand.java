package grisu.gricli.command;

import grisu.control.JobConstants;
import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.model.dto.DtoJob;

import java.util.SortedSet;

public class StatusCommand implements GricliCommand {

	@SyntaxDescription(command = { "status" }, arguments = {})
	public StatusCommand() {
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {

		SortedSet<DtoJob> jobs = Gricli.completionCache.getCurrentJobs(true);

		int active = 0;
		int finished = 0;
		int failed = 0;

		for (DtoJob j : jobs) {
			if (j.getStatus() >= JobConstants.FINISHED_EITHER_WAY) {
				finished = finished + 1;
				if (j.getStatus() != JobConstants.DONE) {
					failed = failed + 1;
				}
			} else {
				active = active + 1;
			}
		}

		env.printMessage("Active jobs: " + active);
		if (failed > 0) {
			env.printMessage("Finished jobs: " + finished + " (Failed: "
					+ failed + ")");
		} else {
			env.printMessage("Finished jobs: " + finished);
		}

		return env;
	}

}
