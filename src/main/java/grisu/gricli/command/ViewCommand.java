package grisu.gricli.command;

import grisu.control.exceptions.NoSuchJobException;
import grisu.frontend.control.clientexceptions.FileTransactionException;
import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.FileCompletor;
import grisu.gricli.completors.JobDirFileCompletor;
import grisu.gricli.completors.JobnameCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.constants.Constants;
import grisu.model.FileManager;
import grisu.model.dto.DtoJob;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.python.google.common.io.Files;

import com.google.common.base.Charsets;

public class ViewCommand implements GricliCommand {

	private final String filename;
	private final String jobname;

	@SyntaxDescription(command = { "view" }, arguments = { "filename" })
	@AutoComplete(completors = { FileCompletor.class })
	public ViewCommand(String filename) {
		this.filename = filename;
		this.jobname = null;
	}

	@SyntaxDescription(command = { "view", "job" }, arguments = { "jobname", "filename" })
	@AutoComplete(completors = { JobnameCompletor.class,
			JobDirFileCompletor.class })
	public ViewCommand(String jobname, String file) {
		this.jobname = jobname;
		this.filename = file;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {

		if (StringUtils.isBlank(filename)) {
			throw new GricliRuntimeException("No file provided.");
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

			fileToView = jobdir + this.filename;

		} else {
			fileToView = this.filename;
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

		return env;
		// try {
		// BufferedReader in
		// = new BufferedReader(new FileReader(this.filename));
		//
		// Terminal t = Terminal.getTerminal();
		// int h = t.getTerminalHeight();
		// int w = t.getTerminalWidth();
		//
		// String line = "";
		//
		// int ch = 0;
		// int c = 0;
		//
		// while ((line = in.readLine()) != null){
		//
		// if (ch >= h){
		// while (true){
		//
		// c = t.readVirtualKey(System.in);
		// if (c == jline.UnixTerminal.ARROW_DOWN){
		// break;
		// } else if (c == jline.UnixTerminal.END_CODE){
		// ch = 0;
		// break;
		// } else {
		// System.out.println(c);
		// }
		//
		// }
		// c = 0;
		//
		// } else {
		// ch++;
		// }
		// env.printMessage(line.replaceAll("\\p{Cntrl}", "?"));
		//
		// }
		//
		//
		// return env;
		// } catch (IOException ex){
		// throw new GricliRuntimeException("file " + this.filename +
		// "not found");
		// }
	}

}
