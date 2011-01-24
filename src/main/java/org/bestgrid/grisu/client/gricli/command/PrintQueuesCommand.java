package org.bestgrid.grisu.client.gricli.command;

import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.GricliRuntimeException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.dto.DtoSubmissionLocations;

public class PrintQueuesCommand implements GricliCommand {
	private final String fqan;

	public PrintQueuesCommand(String fqan) {
		this.fqan = fqan;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		ServiceInterface si = env.getServiceInterface();
		DtoSubmissionLocations queues = (fqan == null) ? si
				.getAllSubmissionLocations() : si
				.getAllSubmissionLocationsForFqan(fqan);

		System.out.println("available queues: ====");
		for (String queue : queues.asSubmissionLocationStrings()) {
			System.out.println(queue);
		}
		return env;
	}

}
