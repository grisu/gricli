
package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.JobPropertiesException;
import grisu.control.exceptions.NoSuchJobException;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.JobnameCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.gricli.util.ServiceInterfaceUtils;
import grisu.jcommons.constants.Constants;


public class ArchiveJobCommand implements
GricliCommand {
	private final String jobFilter;

	@SyntaxDescription(command={"archive","job"},arguments={"jobname"})
	@AutoComplete(completors={JobnameCompletor.class})
	public ArchiveJobCommand(String jobFilter) {
		this.jobFilter = jobFilter;
	}

	public GricliEnvironment execute(GricliEnvironment env)
	throws GricliRuntimeException {
		ServiceInterface si = env.getServiceInterface();
		si.setUserProperty(Constants.DEFAULT_JOB_ARCHIVE_LOCATION, null);
		String jobname = null;
		try {
			for (String j : ServiceInterfaceUtils.filterJobNames(si, jobFilter)) {
				env.printMessage("archiving job " + j);
				jobname = j;
				si.archiveJob(j, null);
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