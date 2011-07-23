package grisu.gricli.old.client.gricli;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.NoSuchJobException;
import grisu.jcommons.constants.Constants;
import grisu.model.MountPoint;

import org.apache.log4j.Logger;

public abstract class AbstractJobProperties implements JobProperties {

	private static Logger myLogger = Logger
			.getLogger(AbstractJobProperties.class.getName());

	protected ServiceInterface serviceInterface = null;

	private String userExecutionHostFs = null;
	private String workingdir = null;
	private String absoluteJobDir = null;

	public String getAbsoluteJobDir() {

		if (absoluteJobDir == null) {

			try {
				absoluteJobDir = serviceInterface.getJobProperty(getJobname(),
						Constants.JOBDIRECTORY_KEY);
			} catch (final NoSuchJobException e) {
				myLogger.error(e);
				return null;
			}
		}
		return absoluteJobDir;
	}

	public String getUserExecutionHostFs() {

		if (userExecutionHostFs == null) {

			final MountPoint mp = serviceInterface
					.getMountPointForUri(getAbsoluteJobDir());

			userExecutionHostFs = mp.getRootUrl();

		}
		return userExecutionHostFs;
	}

	public String getWorkingDirectory() {

		if (workingdir == null) {

			int i = 1;
			if (getUserExecutionHostFs().endsWith("/")) {
				i = 2;
			}
			workingdir = getAbsoluteJobDir().substring(
					getUserExecutionHostFs().length() + i);
		}
		return workingdir;
	}

}
