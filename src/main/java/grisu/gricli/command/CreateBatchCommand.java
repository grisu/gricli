package grisu.gricli.command;

import java.util.Map;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.BatchJobException;
import grisu.control.exceptions.NoSuchJobException;
import grisu.frontend.model.job.BatchJobObject;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.jcommons.constants.Constants;

public class CreateBatchCommand implements GricliCommand {
	
	
	private String name;


	@SyntaxDescription(command={"batch","create"},
			arguments={"name"},
			help="creates batch job object\n batch objects act as containers for jobs ")
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
