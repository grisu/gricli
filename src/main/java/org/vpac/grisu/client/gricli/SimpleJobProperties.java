package org.vpac.grisu.client.gricli;

import org.vpac.grisu.control.ServiceInterface;

public class SimpleJobProperties extends AbstractJobProperties implements JobProperties {
	
	private String absoluteJobDir = null;
	private String applicationName = null;
	private String[] arguments = null;
	private String emailAddress = null;
	private String executable = null;
	private String[] inputFiles = null;
	private String jobName = null;
	private String module = null;
	private int noCPUs = -1;
	private int memory = 0;
	private String stdout = null;
	private String stderr = null;
	private String submissionLocation = null;
	private String vo = null;
	private int walltime = -1;

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

	public String getModule() {
		return module;
	}

	public int getNoCPUs() {
		return noCPUs;
	}

	public int getMemory(){
		return memory;
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

	public String getWorkingDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

}
