package grisu.gricli.completors;

import java.util.List;

import jline.Completor;
import jline.SimpleCompletor;

public class JobPropertiesCompletor implements Completor {

	SimpleCompletor sc;

	public JobPropertiesCompletor() {
		sc = new SimpleCompletor(new String[] { "status", "package", "version",
				"commandline", "concatenated_output", "cpus", "email",
				"email_on_finish", "email_on_start", "executable",
				"factoryType", "group", "hostcount", "inputFilesUrls",
				"jobDirectory", "jobname", "memory", "modules", "mountpoint",
				"pbsDebug", "queue", "result_directory", "stagingFileSystem",
				"stderr", "stdout", "submissionHost", "submissionLocation",
				"submissionSite", "submissionTime", "submissionType",
				"walltime", "workingDirectory" });
	}

	@SuppressWarnings("unchecked")
	public int complete(String s, int i, List l) {
		return sc.complete(s, i, l);
	}

}
