package grisu.gricli.command;

import grisu.control.exceptions.NoSuchJobException;
import grisu.frontend.control.clientexceptions.FileTransactionException;
import grisu.frontend.model.job.GrisuJob;
import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.JobDirFileCompletor;
import grisu.gricli.completors.ViewCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.constants.Constants;
import grisu.model.FileManager;
import grisu.model.dto.DtoJob;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import jline.NullCompletor;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class ViewCommand implements GricliCommand {

	private final String[] job_or_filenames;

	// private final String jobname;

	@SyntaxDescription(command = { "view" }, arguments = { "args" })
	@AutoComplete(completors = { ViewCompletor.class,
			JobDirFileCompletor.class, NullCompletor.class })
	public ViewCommand(String... job_or_filenames) {
		this.job_or_filenames = job_or_filenames;
		// this.jobname = null;
	}

	public void execute(GricliEnvironment env) throws GricliRuntimeException {
		
		if ((job_or_filenames == null) || (job_or_filenames.length == 0)) {
			throw new GricliRuntimeException("No jobname and/or file provided.");
		}

		String filename = null;
		String jobname = null;
		if (job_or_filenames.length == 1) {
			filename = job_or_filenames[0];
		} else if (job_or_filenames.length == 2) {
			filename = job_or_filenames[1];
			jobname = job_or_filenames[0];
		} else {
			throw new GricliRuntimeException("Too many arguments.");
		}

		String fileToView = null;
		GrisuJob jobToView = null;

		if (jobname != null) {

			DtoJob job = null;
			for (DtoJob jobTemp : Gricli.completionCache.getCurrentJobs(false)) {
				if (jobTemp.jobname().equals(jobname)) {
					job = jobTemp;
					break;
				}
			}

			if (job != null) {
				try {
					job = env.getServiceInterface().getJob(jobname);
				} catch (NoSuchJobException e) {
					throw new GricliRuntimeException("No job with name \""
							+ jobname + "\"");
				}
			}

			String jobdir = DtoJob.getProperty(job, Constants.JOBDIRECTORY_KEY);
			jobdir = FileManager.ensureTrailingSlash(jobdir);

			fileToView = jobdir + filename;

		} else {
			DtoJob job = null;
			for (DtoJob jobTemp : Gricli.completionCache.getCurrentJobs(false)) {
				if (jobTemp.jobname().equals(filename)) {
					job = jobTemp;
					break;
				}
			}

			if (job != null) {
				try {
					jobToView = new GrisuJob(env.getServiceInterface(), job);
				} catch (NoSuchJobException e) {
					throw new GricliRuntimeException("No job with name \""
							+ jobname + "\"");
				}
			} else {
				fileToView = filename;
			}

		}

		if (jobToView == null) {

			final FileManager fm = env.getGrisuRegistry().getFileManager();

			File cacheFile = null;
			try {
				cacheFile = fm.downloadFile(fileToView, false);
				// System.out.println("Cache: " + cacheFile.getAbsolutePath());
			} catch (final FileTransactionException e) {
				if (e.getCause() == null) {
					// means threshold bigger
					env.printError("File bigger than configured download threshold. Not downloading.");
				}
			}

			try {
				for (final String line : Files.readLines(cacheFile,
						Charsets.UTF_8)) {
					env.printMessage(line);
				}
			} catch (final IOException e) {
				env.printError("Can't read file: " + e.getLocalizedMessage());
			}

		} else {
			// view stdout & stderr
			
			env.printMessage("");
			env.printMessage("Reading stdout & stderr for job: '"+jobToView.getJobname()+"'...");
			env.printMessage("");
			try {
				env.printMessage("========================================");
				env.printMessage("Stdout:");
				env.printMessage("");
				String stdout = jobToView.getStdOutContent();
				env.printMessage(stdout);
			} catch (Exception e) {
				env.printError("Can't display stdout: "+e.getLocalizedMessage());
			}
			try {
				env.printMessage("");
				env.printMessage("========================================");
				env.printMessage("Stderr:");
				env.printMessage("");
				String stderr = jobToView.getStdErrContent();
				env.printMessage(stderr);
			} catch (Exception e) {
				env.printError("Can't display stderr: "+e.getLocalizedMessage());
			}
			env.printMessage("");
			env.printMessage("========================================");
			env.printMessage("");

		}

	}

}
