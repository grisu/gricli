package org.vpac.grisu.client.gricli;

import org.vpac.grisu.control.ServiceInterface;


/**
 * @author markus
 *
 */
public interface JobProperties {
	
	/**
	 * Returns the name of the job.
	 * @return the name of the job.
	 */
	public String getJobname();

	/**
	 * Returns the applicationName. Using the application name is optional (otherwise the default "generic" is used) but recommended so 
	 * possible output viewer plugins can determine which viewer to use. Also, later on GrisuClient might use mds to auto-calculate a submission
	 * location using mds and load appropriate modules. In this case the application name would be necessary.
	 * @return
	 */
	public String getApplicationName();

	/**
	 * The name of the executable.
	 * @return the name of the executable.
	 */
	public String getExecutablesName();
	
	/**
	 * The arguments of the commandline (like: new String[]{"-p", "parameter", "-v"}).
	 * @return the arguments
	 */
	public String[] getArguments();
	
	/**
	 * Returns the workind directory of the job relatve to the output of the {@link #getUserExecutionHostFs()} method. Only returns something that 
	 * makes sense if called in "submit" mode because otherwise important parameters to calculate it aren't present.
	 * @return the working directory relative to the userexecutionhostfs or null
	 */
	public String getWorkingDirectory();
	
	/**
	 * The name of the stdout file (e.g. "stdout.txt").
	 * @return the name of the stdout file
	 */
	public String getStdout();
	
	/**
	 * The name of the stderr file (e.g. "stderr.txt").
	 * @return the name of the stderr file
	 */
	public String getStderr();
	
	public String getModule();
	
	public String getEmailAddress();

	public int getWalltimeInSeconds();

	public int getNoCPUs();

	public String getSubmissionLocation();

	/**
	 * Returns the userexecutionhostfs (aka mountpoint) of the job. This is calculated depending on the submission location and the VO that is used to submit the job.
	 * This method will most likely return null if not called in "submit" mode.
	 * @return the userexectutionhostfs or null
	 */
	public String getUserExecutionHostFs();
	
	/**
	 * Returns the absolute url of the job directory. It either gets calculated if the job is not submitted yet or it returns the result of the {@link ServiceInterface#getJobDirectory(String)}
	 * method if the job is already submitted. If the job directory can't be determined, this method returns null.
	 * @return the absolute url of the job directory or null
	 */
	public String getAbsoluteJobDir();
	
	public String[] getInputFiles();
	
	public String getVO();
	
}
