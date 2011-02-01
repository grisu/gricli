package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.JobPropertiesException;
import grisu.control.exceptions.JobSubmissionException;
import grisu.frontend.model.job.JobObject;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.jcommons.constants.Constants;

import java.io.File;
import java.util.List;

public class SubmitCmdCommand implements GricliCommand {

	private final String cmd;

	public SubmitCmdCommand(String cmd) {
		this.cmd = cmd;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {

		ServiceInterface si = env.getServiceInterface();
		final JobObject job = new JobObject(si);
		job.setJobname(env.get("jobname"));
		job.setApplication(Constants.GENERIC_APPLICATION_NAME);
		job.setCommandline(cmd);
		job.setCpus(Integer.parseInt(env.get("cpus")));
		job.setEmail_address(env.get("email"));
		job.setWalltime(Integer.parseInt(env.get("walltime")) * 60
				* job.getCpus());
		job.setMemory(Long.parseLong(env.get("memory")));
		job.setSubmissionLocation(env.get("queue"));

		boolean isMpi = "mpi".equals(env.get("jobtype"));
		job.setForce_mpi(isMpi);

		// attach input files
		List<String> files = env.getList("files");
		String cdir = env.get("dir");
		for (String file : files) {
			if (file.startsWith("/")) {
				job.addInputFileUrl(file);
			} else {
				// relative path
				job.addInputFileUrl(cdir + File.pathSeparator + file);
			}
		}

		String jobname = null;
		try {
			jobname = job
					.createJob(env.get("fqan"), Constants.TIMESTAMP_METHOD);
		} catch (JobPropertiesException ex) {
			throw new GricliRuntimeException("job property is not valid"
					+ ex.getMessage());
		}
		try {
			job.submitJob();
		} catch (JobSubmissionException ex) {
			throw new GricliRuntimeException("fail to submit job: "
					+ ex.getMessage());
		} catch (InterruptedException ex) {
			throw new GricliRuntimeException("jobmission was interrupted: "
					+ ex.getMessage());
		}
		System.out.println(" job name is " + jobname);

		return env;
	}

}
