package grisu.gricli.completors;

import grisu.gricli.Gricli;
import grisu.gricli.completors.file.StillLoadingException;
import grisu.jcommons.constants.Constants;
import grisu.jcommons.utils.CliHelpers;
import grisu.model.FileManager;
import grisu.model.dto.DtoJob;
import grisu.model.dto.GridFile;
import grisu.settings.ClientPropertiesManager;

import java.util.List;

import jline.Completor;

import org.apache.commons.lang.StringUtils;

public class JobDirFileCompletor implements Completor {

	private final FileCompletor fc = new FileCompletor();

	public int complete(String s, int i, List l) {

		final String previous = CliHelpers.getConsoleReader().getCursorBuffer()
				.getBuffer().toString();

		String jobname = null;
		try {
			jobname = previous.split("\\s")[1];
		} catch (Exception e) {
			return -1;
		}

		if (!Gricli.completionCache.getJobnames().contains(jobname)) {
			return -1;
		}

		DtoJob job = null;
		for (DtoJob jobTemp : Gricli.completionCache.getCurrentJobs(false)) {
			if (jobTemp.jobname().equals(jobname)) {
				job = jobTemp;
				break;
			}
		}

		if (job == null) {
			return -1;
		}

		String url = DtoJob.getProperty(job, Constants.JOBDIRECTORY_KEY);

		if (StringUtils.isBlank(url)) {
			return -1;
		}

		String part = s;
		if (StringUtils.isBlank(part)) {
			part = "";
		}

		url = FileManager.ensureTrailingSlash(url);

		if (part.contains("/")) {
			int index = part.lastIndexOf("/");
			String dirs = part.substring(0, index);
			url = url + dirs;
			url = FileManager.ensureTrailingSlash(url);
		}

		GridFile f;
		try {
			f = Gricli.completionCache.ls(url);
		} catch (final StillLoadingException e) {

			try {
				Thread.sleep(ClientPropertiesManager
						.getGricliCompletionSleepTimeInMS());
			} catch (final InterruptedException e1) {
			}
			// try again
			try {
				f = Gricli.completionCache.ls(url);
			} catch (final StillLoadingException e1) {
				l.add("*** loading...");
				l.add("...try again ***");
				return url.length();
			}

		}
		return fc.matchRemoteFiles(part, part, f.getChildren(), l);
	}

}
