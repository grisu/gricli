package grisu.gricli.command;

import grisu.control.exceptions.JobPropertiesException;
import grisu.control.exceptions.JobSubmissionException;
import grisu.frontend.model.job.JobObject;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.CompletionCache;
import grisu.jcommons.constants.Constants;


public class SubmitCommand implements
GricliCommand {

	private final String cmd;
	private final boolean isAsync;

	@SyntaxDescription(command={"submit"}, arguments={"commandline"})
	public SubmitCommand(String cmd) {
		this(cmd,null);
	}

	@SyntaxDescription(command={"submit"},arguments={"commandline","&"})
	public SubmitCommand(String cmd, String mod){
		this.cmd = cmd;
		this.isAsync = "&".equals(mod);

	}

	protected JobObject createJob(GricliEnvironment env) throws GricliRuntimeException{
		JobObject job = env.getJob();
		job.setCommandline(cmd);
		try {
			job.createJob(env.get("group"), Constants.UNIQUE_NUMBER_METHOD);
			return job;
		} catch (JobPropertiesException ex) {

			try {
				job.createJob(env.get("group"), Constants.TIMESTAMP_METHOD);
				return job;
			} catch (JobPropertiesException ex2){
				throw new GricliRuntimeException("job property is not valid"
						+ ex.getMessage(),ex);
			}
		}
	}

	public GricliEnvironment execute(GricliEnvironment env)
	throws GricliRuntimeException {
		final JobObject job = createJob(env);
		String jobname = job.getJobname();
		System.out.println(" job name is " + jobname);
		CompletionCache.jobnames.add(jobname);

		if (this.isAsync){
			new Thread() {
				@Override
				public void run() {
					try {submit(job);}
					catch (GricliRuntimeException ex) {/* do nothing */} }}.start();
		}
		else {
			submit(job);
		}

		return env;
	}

	private void submit(JobObject job) throws GricliRuntimeException{
		try{
			job.submitJob();
		} catch (JobSubmissionException e) {
			throw new GricliRuntimeException("fail to submit job: "
					+ e.getMessage(),e);
		} catch (InterruptedException e) {
			throw new GricliRuntimeException("jobmission was interrupted: "
					+ e.getMessage(),e);
		}
	}

}
