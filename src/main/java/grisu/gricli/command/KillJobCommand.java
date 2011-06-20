package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.BatchJobException;
import grisu.control.exceptions.NoSuchJobException;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.JobnameCompletor;
import grisu.gricli.util.ServiceInterfaceUtils;


public class KillJobCommand implements
GricliCommand {
	private final String jobFilter;
	private final boolean clean;

	@SyntaxDescription(command={"kill","job"}, arguments={"jobname"})
	@AutoComplete(completors={JobnameCompletor.class})
	public KillJobCommand(String jobFilter){
		this(jobFilter, false);
	}

	public KillJobCommand(String jobFilter, boolean clean) {
		this.jobFilter = jobFilter;
		this.clean = clean;
	}

	public GricliEnvironment execute(GricliEnvironment env)
	throws GricliRuntimeException {
		ServiceInterface si = env.getServiceInterface();
		for (String j : ServiceInterfaceUtils.filterJobNames(si, jobFilter)) {
			env.printMessage("killing job " + j);
			try {
				si.kill(j, clean);
			} catch (RemoteFileSystemException ex) {
				env.printError("job "+ j + ":" + ex.getMessage());
			} catch (NoSuchJobException ex) {
				env.printError("job " + j + " does not exist");
			} catch (BatchJobException ex) {
				env.printError("job "+ j + ": "+ ex.getMessage());
			}
		}

		return env;
	}

}
