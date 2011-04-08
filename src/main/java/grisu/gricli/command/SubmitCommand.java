package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.JobPropertiesException;
import grisu.control.exceptions.JobSubmissionException;
import grisu.frontend.model.job.JobObject;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.CompletionCache;
import grisu.jcommons.constants.Constants;
import grisu.model.dto.GridFile;

import java.io.File;
import java.util.List;


public class SubmitCommand implements GricliCommand {

	private final String cmd;
	private final boolean isAsync;

	@SyntaxDescription(command={"submit"})
	public SubmitCommand(String cmd) {
		this(cmd,null);
	}
	
	@SyntaxDescription(command={"submit"})
	public SubmitCommand(String cmd, String mod){
		this.cmd = cmd;
		this.isAsync = "&".equals(mod);
		
	}
	
	protected JobObject createJob(GricliEnvironment env) throws GricliRuntimeException{
		ServiceInterface si = env.getServiceInterface();
		final JobObject job = new JobObject(si);
		job.setJobname(env.get("jobname"));
		String app = env.get("application");
		if (app == null){
			job.setApplication(Constants.GENERIC_APPLICATION_NAME);
		} 
		else {
			job.setApplication(app);
		}
		
		job.setCommandline(cmd);
		job.setCpus(Integer.parseInt(env.get("cpus")));
		job.setEmail_address(env.get("email"));
		job.setWalltimeInSeconds(Integer.parseInt(env.get("walltime")) * 60
				* job.getCpus());
		job.setMemory(Long.parseLong(env.get("memory")));
		job.setSubmissionLocation(env.get("queue"));

		boolean isMpi = "mpi".equals(env.get("jobtype"));
		job.setForce_mpi(isMpi);

		// attach input files
		List<String> files = env.getList("files");
		//String cdir = env.get("dir");
		for (String file : files) {
			job.addInputFileUrl(new GridFile(file).getUrl());
		}

		try {
			job.createJob(env.get("vo"), Constants.TIMESTAMP_METHOD);
			return job;
		} catch (JobPropertiesException ex) {
			throw new GricliRuntimeException("job property is not valid"
					+ ex.getMessage(),ex);
		}
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

	public GricliEnvironment execute(GricliEnvironment env)
	throws GricliRuntimeException {
		final JobObject job = createJob(env);
		String jobname = job.getJobname();
		System.out.println(" job name is " + jobname);	
		CompletionCache.jobnames.add(jobname);

		if (this.isAsync){
			new Thread() {
				public void run() {
					try {submit(job);} 
					catch (GricliRuntimeException ex) {/* do nothing */} }}.start();
		} 
		else {
			submit(job);
		}

		return env;
	}

}
