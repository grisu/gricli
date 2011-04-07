package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.BatchJobException;
import grisu.control.exceptions.NoSuchJobException;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.JobnameCompletor;
import grisu.gricli.util.ServiceInterfaceUtils;


public class KillJobCommand implements GricliCommand {
	private final String jobFilter;
	private final boolean clean;

	@SyntaxDescription(command={"kill","job"})
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
		String jobname = null;
		try {
			for (String j : ServiceInterfaceUtils.filterJobNames(si, jobFilter)) {
				System.out.println("killing job " + j);
				jobname = j;
				si.kill(j, clean);
			}
		} catch (RemoteFileSystemException ex) {
			throw new GricliRuntimeException(ex);
		} catch (NoSuchJobException ex) {
			throw new GricliRuntimeException("job " + jobname
					+ " does not exist");
		} catch (BatchJobException ex) {
			throw new GricliRuntimeException(ex);
		}
		return env;
	}

}
