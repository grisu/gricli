package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.JobnameCompletor;

public class WaitJobCommand implements GricliCommand {

	private String jobFilter;



	@SyntaxDescription(command="wait job")
	@AutoComplete(completors={JobnameCompletor.class})
	public WaitJobCommand(String jobFilter) {
		this.jobFilter = jobFilter;
	}

	
	
	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

}
