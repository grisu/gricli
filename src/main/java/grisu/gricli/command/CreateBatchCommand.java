package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.BatchJobException;
import grisu.frontend.model.job.BatchJobObject;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.constants.Constants;

public class CreateBatchCommand implements GricliCommand {

	private final String name;

	@SyntaxDescription(command = { "batch", "create" }, arguments = { "name" })
	public CreateBatchCommand(String name) {
		this.name = name;
	}

	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {

		final ServiceInterface si = env.getServiceInterface();
		try {
			final BatchJobObject obj = new BatchJobObject(si, this.name,
					env.group.get(), env.application.get(),
					Constants.NO_VERSION_INDICATOR_STRING);
		} catch (final BatchJobException e) {
			throw new GricliRuntimeException(e);
		}
	}

}
