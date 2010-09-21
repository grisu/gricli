package org.vpac.grisu.client.gricli;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.utils.CommandlineHelpers;

/**
 * Gets properties from config file or command line. Default config file is
 * ~/.grisu/grisu.commandline
 **/
public class CommandlineProperties extends AbstractJobProperties implements
		JobProperties {

	public final static String DEFAULT_APP_NAME = "generic";
	public final static String DEFAULT_STDOUT = "stdout.txt";
	public final static String DEFAULT_STDERR = "stderr.txt";
	public final static int DEFAULT_MEMORY_SIZE = 2048;

	public final static String JOBNAME = "jobName";
	public final static String VO_OPTION = "vo";
	public final static String SUBMISSION_LOCATION_OPTION = "submissionLocation";
	public final static String COMMAND_OPTION = "command";
	public final static String WALLTIME_OPTION = "walltime";
	public final static String CPUS_OPTION = "cpus";
	public final static String MEMORY_OPTION = "ram";
	public final static String INPUTFILEPATH_OPTION = "inputFilePath";
	public final static String EMAIL_OPTION = "email";
	public final static String STDOUT_OPTION = "stdout";
	public final static String STDERR_OPTION = "stderr";
	public final static String MODULE_OPTION = "module";
	public final static String CONFIG_FILE_PATH_OPTION = "config";

	public final static String APPLICATION_OPTION = "application";
	public final static String GENERATE_UNIQUE_JOBNAME_OPTION = "unique";

	public final static String SHIB_USERNAME_OPTION = "shibUsername";
	public final static String SHIB_IDP_OPTION = "shibIdp";

	/**
	 * gets list of all jobs currently active and unique generates job name with
	 * given base.
	 **/
	private static String generateJobName(String base, String[] jobNames) {
		int token = 0;
		int newToken = 0;
		for (final String jobName : jobNames) {
			if (jobName.startsWith(base)) {
				try {
					newToken = Integer.parseInt(jobName.replaceFirst(base, "")) + 1;
					token = Math.max(newToken, token);
				} catch (final NumberFormatException e) {
					// that's ok - ignore.
				}
			}
		}

		return base + token;

	}

	private GrisuClientFileConfiguration configuration = null;

	private CommandLine line = null;
	private String submissionLocation = null;

	private String jobname = null;

	public CommandlineProperties(ServiceInterface serviceInterface,
			CommandLine line) {
		this.serviceInterface = serviceInterface;
		this.line = line;
		try {
			this.configuration = GrisuClientFileConfiguration
					.getConfiguration(line
							.getOptionValue(CONFIG_FILE_PATH_OPTION));
			// myLogger.debug(configuration);
		} catch (final ConfigurationException ex) {
			throw new RuntimeException(ex);
		}
	}

	public String getApplicationName() {

		String appName = line.getOptionValue(APPLICATION_OPTION);

		if (StringUtils.isEmpty(appName)) {
			appName = DEFAULT_APP_NAME;
		}

		return appName;
	}

	public String[] getArguments() {
		final String commandline = line.getOptionValue(COMMAND_OPTION);

		if (StringUtils.isEmpty(commandline)) {
			throw new InvalidOptionException(COMMAND_OPTION, commandline);
		}

		ArrayList<String> args;
		args = CommandlineHelpers.extractArgumentsFromCommandline(commandline);

		return args.toArray(new String[] {});
	}

	private String getConfigOption(String key) {
		try {
			return configuration.getJobOption(key);
		} catch (final ConfigurationException e) {
			// myLogger.debug("problem reading configuration option " + key +
			// ": " + e);
			return null;
		}
	}

	public String getEmailAddress() {

		String email = getConfigOption(EMAIL_OPTION);
		if (line.hasOption(EMAIL_OPTION)) {
			email = line.getOptionValue(EMAIL_OPTION);
		}
		// TODO check whether valid emailaddress

		if (email == null) {
			return "";
		} else {
			return email;
		}
	}

	public String getExecutablesName() {

		final String commandline = line.getOptionValue(COMMAND_OPTION);

		if (StringUtils.isEmpty(commandline)) {
			throw new InvalidOptionException(COMMAND_OPTION, commandline);
		}

		String executable = null;

		executable = CommandlineHelpers.extractExecutable(commandline);

		return executable;
	}

	public String[] getInputFiles() {

		final String inputFilesString = line
				.getOptionValue(INPUTFILEPATH_OPTION);

		String[] result = null;
		if (!StringUtils.isEmpty(inputFilesString)) {
			final String[] input = inputFilesString.split(",");
			result = new String[input.length];
			for (int i = 0; i < result.length; i++) {

				final File file = new File(input[i]);
				result[i] = file.toURI().toString();

			}
		} else {
			result = new String[] {};
		}

		return result;
	}

	public String getJobname() {
		if (jobname == null) {
			jobname = line.getOptionValue(JOBNAME);

			if (StringUtils.isEmpty(jobname)) {
				throw new InvalidOptionException(JOBNAME, jobname);
			}

			if (line.hasOption(GENERATE_UNIQUE_JOBNAME_OPTION)) {
				jobname = generateJobName(jobname, serviceInterface
						.getAllJobnames(null).asArray());
			}
		}

		return jobname;
	}

	public int getMemory() {
		try {
			String memoryString = getConfigOption(MEMORY_OPTION);
			int memory = DEFAULT_MEMORY_SIZE;
			if (line.hasOption(MEMORY_OPTION)) {
				memoryString = line.getOptionValue(MEMORY_OPTION);
				memory = Integer.parseInt(memoryString);
			}
			return memory;
		} catch (final NumberFormatException e) {
			throw new InvalidOptionException(MEMORY_OPTION,
					line.getOptionValue(MEMORY_OPTION));
		}
	}

	public String getModule() {

		String module = getConfigOption(MODULE_OPTION);
		if (line.hasOption(MODULE_OPTION)) {
			module = line.getOptionValue(MODULE_OPTION);
		}

		if (module == null) {
			return "";
		} else {
			return module;
		}
	}

	public int getNoCPUs() {

		int noCPUs = -1;

		try {
			String noCPUsString = getConfigOption(CPUS_OPTION);
			if (line.hasOption(CPUS_OPTION)) {
				noCPUsString = line.getOptionValue(CPUS_OPTION);
			}

			noCPUs = Integer.parseInt(noCPUsString);
		} catch (final NumberFormatException e) {
			throw new InvalidOptionException(CPUS_OPTION,
					line.getOptionValue(CPUS_OPTION));
		}

		return noCPUs;
	}

	public String getStderr() {

		final String stderr = line.getOptionValue(STDERR_OPTION);

		if (stderr == null) {
			return DEFAULT_STDERR;
		} else {
			return stderr;
		}
	}

	public String getStdout() {

		final String stdout = line.getOptionValue(STDOUT_OPTION);

		if (stdout == null) {
			return DEFAULT_STDOUT;
		} else {
			return stdout;
		}
	}

	public String getSubmissionLocation() {

		if (submissionLocation == null) {

			if (line.hasOption(SUBMISSION_LOCATION_OPTION)) {
				submissionLocation = line
						.getOptionValue(SUBMISSION_LOCATION_OPTION);
			} else {
				submissionLocation = getConfigOption(SUBMISSION_LOCATION_OPTION);
			}

			if (StringUtils.isEmpty(submissionLocation)) {
				throw new InvalidOptionException(SUBMISSION_LOCATION_OPTION,
						submissionLocation);
			}
		}

		return submissionLocation;

	}

	public String getVO() {

		String vo = getConfigOption(VO_OPTION);
		if (line.hasOption(VO_OPTION)) {
			vo = line.getOptionValue(VO_OPTION);
		}

		return vo;
	}

	public int getWalltimeInSeconds() {

		int walltimeInSeconds = -1;

		try {
			String walltime = getConfigOption(WALLTIME_OPTION);
			if (line.hasOption(WALLTIME_OPTION)) {
				walltime = line.getOptionValue(WALLTIME_OPTION);
			}

			walltimeInSeconds = Integer.parseInt(walltime);
		} catch (final NumberFormatException e) {
			throw new InvalidOptionException(WALLTIME_OPTION,
					line.getOptionValue(WALLTIME_OPTION));
		}

		return walltimeInSeconds;
	}

}
