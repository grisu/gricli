package grisu.gricli.command;

import grisu.control.exceptions.NoSuchJobException;
import grisu.frontend.control.clientexceptions.FileTransactionException;
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

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class ViewCommand implements GricliCommand {

	private final String[] job_or_filenames;

	// private final String jobname;

	@SyntaxDescription(command = { "view" }, arguments = { "args" })
	@AutoComplete(completors = { ViewCompletor.class, JobDirFileCompletor.class })
	public ViewCommand(String... job_or_filenames) {
		this.job_or_filenames = job_or_filenames;
		// this.jobname = null;
	}


	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {

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

		if (jobname != null) {

			DtoJob job = null;
			for (DtoJob jobTemp : Gricli.completionCache.getCurrentJobs(false)) {
				if (jobTemp.jobname().equals(jobname)) {
					job = jobTemp;
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
			fileToView = filename;
		}

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
			for (final String line : Files.readLines(cacheFile, Charsets.UTF_8)) {
				env.printMessage(line);
			}
		} catch (final IOException e) {
			env.printError("Can't read file: " + e.getLocalizedMessage());
		}

	}

}
