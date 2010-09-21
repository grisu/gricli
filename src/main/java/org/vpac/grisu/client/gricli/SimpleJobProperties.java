package org.vpac.grisu.client.gricli;

public class SimpleJobProperties extends AbstractJobProperties implements
		JobProperties {

	private final String absoluteJobDir = null;
	private final String applicationName = null;
	private final String[] arguments = null;
	private final String emailAddress = null;
	private final String executable = null;
	private final String[] inputFiles = null;
	private final String jobName = null;
	private final String module = null;
	private final int noCPUs = -1;
	private final int memory = 0;
	private final String stdout = null;
	private final String stderr = null;
	private final String submissionLocation = null;
	private final String vo = null;
	private final int walltime = -1;

	@Override
	public String getAbsoluteJobDir() {
		return absoluteJobDir;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String[] getArguments() {
		return arguments;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public String getExecutablesName() {
		return executable;
	}

	public String[] getInputFiles() {
		return inputFiles;
	}

	public String getJobname() {
		return jobName;
	}

	public int getMemory() {
		return memory;
	}

	public String getModule() {
		return module;
	}

	public int getNoCPUs() {
		return noCPUs;
	}

	public String getStderr() {
		return stderr;
	}

	public String getStdout() {
		return stdout;
	}

	public String getSubmissionLocation() {
		return submissionLocation;
	}

	@Override
	public String getUserExecutionHostFs() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getVO() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getWalltimeInSeconds() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getWorkingDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

}
