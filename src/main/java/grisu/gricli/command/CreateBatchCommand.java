package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.BatchJobException;
import grisu.frontend.model.job.BatchJobObject;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.jcommons.constants.Constants;

public class CreateBatchCommand implements
GricliCommand {


	private final String name;


	@SyntaxDescription(command={"batch","create"},
 arguments = { "name" })
			public CreateBatchCommand(String name){
		this.name = name;
	}


	public GricliEnvironment execute(GricliEnvironment env)
	throws GricliRuntimeException {

		ServiceInterface si = env.getServiceInterface();
		try {
			BatchJobObject obj = new BatchJobObject(si, this.name, env.get("vo"),
					env.get("application"), Constants.NO_VERSION_INDICATOR_STRING);
		} catch (BatchJobException e) {
			throw new GricliRuntimeException(e);
		}
		return env;
	}

}
