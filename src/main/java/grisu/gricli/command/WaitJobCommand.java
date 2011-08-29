package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.NoSuchJobException;
import grisu.frontend.model.job.JobObject;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.JobnameCompletor;
import grisu.gricli.environment.GricliEnvironment;

public class WaitJobCommand implements
GricliCommand {

	private final String jobname;

	@SyntaxDescription(command={"wait","job"},arguments={"jobname"})
	@AutoComplete(completors={JobnameCompletor.class})
	public WaitJobCommand(String jobname) {
		this.jobname = jobname;
	}



	public GricliEnvironment execute(GricliEnvironment env)
	throws GricliRuntimeException {
		ServiceInterface si = env.getServiceInterface();
		try {
			JobObject job = new JobObject(si,si.getJob(this.jobname));
			job.waitForJobToFinish(5);
		} catch (NoSuchJobException e) {
			env.printError("job " + jobname + " not found");
		}

		return env;
	}

}
