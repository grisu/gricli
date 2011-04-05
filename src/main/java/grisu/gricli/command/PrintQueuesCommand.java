package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.model.dto.DtoSubmissionLocations;


public class PrintQueuesCommand implements GricliCommand {
	private final String fqan;

	@SyntaxDescription(command={"print","queues"})
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
