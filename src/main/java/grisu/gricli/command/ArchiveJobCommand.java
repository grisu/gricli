
package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.JobPropertiesException;
import grisu.control.exceptions.NoSuchJobException;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.JobnameCompletor;
import grisu.gricli.util.ServiceInterfaceUtils;


public class ArchiveJobCommand implements GricliCommand {
	private final String jobFilter;

	@SyntaxDescription(command={"archive","job"})
	@AutoComplete(completors={JobnameCompletor.class})
	public ArchiveJobCommand(String jobFilter) {
		this.jobFilter = jobFilter;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		ServiceInterface si = env.getServiceInterface();
		String jobname = null;
		try {
			for (String j : ServiceInterfaceUtils.filterJobNames(si, jobFilter)) {
				System.out.println("archiving job " + j);
				jobname = j;
				//si.archiveJob(j, "grid://Groups/nz/NeSI");
				si.archiveJob(j, "gsiftp://gram5.ceres.auckland.ac.nz/home/yhal003");
			}
		} catch (RemoteFileSystemException ex) {
			throw new GricliRuntimeException(ex);
		} catch (NoSuchJobException ex) {
			throw new GricliRuntimeException("job " + jobname
					+ " does not exist");
		} catch (JobPropertiesException ex) {
			throw new GricliRuntimeException(ex);
		}
		return env;
	}

}