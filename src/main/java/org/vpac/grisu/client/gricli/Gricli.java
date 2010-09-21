package org.vpac.grisu.client.gricli;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import jline.ConsoleReader;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.JobPropertiesException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.NoValidCredentialException;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.utils.SeveralStringHelpers;

public class Gricli {

	public static final int DEFAULT_SLEEP_TIME_IN_SECONDS = 600;
	public static final String JOBNAME_PLACEHOLDER = "XXX_JOBNAME_XXX";
	public static final String APPLICATION_NAME_PLACEHOLDER = "XXX_APPLICATION_NAME_XXX";
	public static final String EXECUTABLE_NAME_PLACEHOLDER = "XXX_EXECUTABLE_XXX";
	public static final String ARGUMENTS_PLACEHOLDER = "XXX_ARGUMENT_ELEMENTS_XXX";
	public static final String WORKINGDIRECTORY_PLACEHOLDER = "XXX_WORKINGDIRECTORY_XXX";
	public static final String STDOUT_PLACEHOLDER = "XXX_STDOUT_XXX";
	public static final String STDERR_PLACEHOLDER = "XXX_STDERR_XXX";
	public static final String MODULE_PLACEHOLDER = "XXX_MODULE_XXX";
	public static final String EMAIL_PLACEHOLDER = "XXX_EMAIL_ADDRESS_XXX";
	public static final String TOTALCPUTIME_PLACEHOLDER = "XXX_TOTALCPUTIME_XXX";
	public static final String TOTALCPUCOUNT_PLACEHOLDER = "XXX_TOTALCPUCOUNT_XXX";
	public static final String SUBMISSIONLOCATION_PLACEHOLDER = "XXX_SUBMISSIONLOCATION_XXX";
	public static final String USEREXECUTIONHOSTFS_PLACEHOLDER = "XXX_USEREXECUTIONHOSTFS";
	public static final String MEMORY_PLACEHOLDER = "XXX_MEMORY_XXX";
	public static final String DEFAULT_MYPROXY_SERVER = "myproxy.arcs.org.au";
	public static final String DEFAULT_MYPROXY_PORT = "443";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Gricli client = null;

		try {
			client = new Gricli(args);
		} catch (final LoginException e) {
			System.err.println("Error with serviceInterface: "
					+ e.getLocalizedMessage());
			System.exit(1);
		} catch (final ServiceInterfaceException e) {
			System.err.println("Error with serviceInterface: "
					+ e.getLocalizedMessage());
			System.exit(1);
		} catch (final IOException e) {
			System.err.println("Could not read password input: "
					+ e.getLocalizedMessage());
			System.exit(1);
		}

		try {
			client.start();
		} catch (final ExecutionException e) {
			System.err.println("Can't execute command: "
					+ e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private GrisuClientProperties clientProperties = null;
	private JobProperties jobProperties = null;
	private ServiceInterface serviceInterface = null;
	private boolean verbose = false;

	private boolean forced_all_mode = false;

	/**
	 * Use this constructor if you want to control the client/job properties of
	 * this GrisuClient yourself. Useful if you want to write a multi-job
	 * control tool for example.
	 * 
	 * @param serviceInterface
	 *            the serviceInterface
	 * @param clientProperties
	 *            the clientProperties
	 * @param jobProperties
	 *            the jobProperties
	 */
	public Gricli(ServiceInterface serviceInterface,
			GrisuClientProperties clientProperties, JobProperties jobProperties) {

		this.serviceInterface = serviceInterface;
		this.clientProperties = clientProperties;
		this.jobProperties = jobProperties;

		verbose = clientProperties.verbose();
		enableDebug(clientProperties.debug());

	}

	/**
	 * Use this constructur if you want to reuse a serviceInterface/session.
	 * 
	 * @param serviceInterface
	 *            the serviceinterface
	 * @param args
	 *            the commandlineArguments
	 */
	public Gricli(ServiceInterface serviceInterface, String[] args) {

		this.serviceInterface = serviceInterface;

		clientProperties = new GrisuClientCommandlineProperties(args);

		verbose = clientProperties.verbose();
		enableDebug(clientProperties.debug());

		jobProperties = new CommandlineProperties(serviceInterface,
				((GrisuClientCommandlineProperties) clientProperties)
						.getCommandLine());

	}

	/**
	 * Use this constructor if you want to create a GrisuClient instance from
	 * scratch with everything needed using the commandline arguments that are
	 * specified in {@link GrisuClientCommandlineProperties} and
	 * {@link CommandlineProperties}.
	 * 
	 * @param args
	 *            the arguments
	 * @params password the password the password
	 * @throws ServiceInterfaceException
	 *             if the client can't create a valid serviceInterface
	 * @throws NoValidCredentialException
	 *             if there is a problem with the login (e.g. wrong
	 *             username/password)
	 */
	public Gricli(String[] args) throws ServiceInterfaceException, IOException,
			LoginException {

		clientProperties = new GrisuClientCommandlineProperties(args);

		verbose = clientProperties.verbose();
		enableDebug(clientProperties.debug());

		if (clientProperties.useLocalProxy()) {
			login();
		} else if ("login".equals(clientProperties.getMode())) {
			loginWithShibboleth(clientProperties.getShibUsername(),
					clientProperties.getShibIdp());
		} else {
			final ConsoleReader consoleReader = new ConsoleReader();
			final char[] password = consoleReader.readLine(
					"Please enter your myproxy password: ", new Character('*'))
					.toCharArray();
			login(clientProperties.getMyProxyUsername(), password);
		}

		jobProperties = new CommandlineProperties(serviceInterface,
				((GrisuClientCommandlineProperties) clientProperties)
						.getCommandLine());
	}

	private int checkStatus() throws ExecutionException {

		final int status = getStatus();
		final String stringStatus = JobConstants.translateStatus(status);

		if (verbose) {
			System.out.println("Status for job " + jobProperties.getJobname()
					+ ": " + stringStatus);
		}

		if (clientProperties.stageOutResults() || forced_all_mode) {

			if ((status == JobConstants.DONE)
					|| ((status >= JobConstants.FINISHED_EITHER_WAY) && forced_all_mode)) {

				if (verbose) {
					String stageoutdir = clientProperties
							.getStageoutDirectory();
					if (StringUtils.isEmpty(stageoutdir)) {
						try {
							stageoutdir = new File(".").getCanonicalPath();
						} catch (final IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					System.out.println("Trying to stageout job directory to "
							+ stageoutdir + "...");
				}

				try {
					if (clientProperties.cleanAfterStageOut()
							|| forced_all_mode) {
						if (verbose) {
							System.out
									.println("Deleting job & job directory...");
						}
						killAndCleanJob();
						if (verbose) {
							System.out.println("Job & job directory deleted.");
						}
					}
				} catch (final Exception e) {
					throw new ExecutionException(
							"Couldn't clean job & jobdirectory: "
									+ e.getLocalizedMessage(), e);
				}

			} else if (status >= JobConstants.FINISHED_EITHER_WAY) {

				if (verbose) {
					System.out
							.println("Didn't stageout files since either job failed or didn't finish with exit code 0.");
				}

			}

		}
		return status;
	}

	private void enableDebug(boolean debug) {

		if (debug) {
			final Level lvl = Level.toLevel("debug");
			Logger.getRootLogger().setLevel(lvl);
		}

	}

	private void executeSubmission() throws NoSuchJobException,
			JobStagingException {

		final InputStream in = Gricli.class
				.getResourceAsStream("/templates/generic_memory.xml");
		final String jsdlTemplateString = SeveralStringHelpers
				.fromInputStream(in);

		// // this is a workaround because of a hsqldb bug which will cause the
		// whole process to fail
		// // for some reason if a job is submitted directly
		// int status = serviceInterface.getJobStatus("nonexistentJobca");

		if (clientProperties.killPossiblyExistingJob()) {
			try {
				if (verbose) {
					System.out
							.println("Trying to kill possibly existing job with jobname \""
									+ jobProperties.getJobname() + "\"");
				}
				serviceInterface.kill(jobProperties.getJobname(), true);
				if (verbose) {
					System.out.println("Killed and cleaned existing job \""
							+ jobProperties.getJobname() + "\"");
				}
			} catch (final NoSuchJobException e) {
				// that's ok
				if (verbose) {
					System.out
							.println("No job killed because no job with jobname "
									+ jobProperties.getJobname() + " existed.");
				}
			} catch (final Exception e) {
				throw new RuntimeException(
						"Can't kill & clean existing job with jobname \""
								+ jobProperties.getJobname() + "\"");
			}
		}

		if (verbose) {
			System.out.println("Preparing job description...");
		}

		String jsdl = jsdlTemplateString.replaceAll(JOBNAME_PLACEHOLDER,
				jobProperties.getJobname());
		jsdl = jsdl.replaceAll(APPLICATION_NAME_PLACEHOLDER,
				jobProperties.getApplicationName());
		jsdl = jsdl.replaceAll(EXECUTABLE_NAME_PLACEHOLDER,
				jobProperties.getExecutablesName());
		final String[] args = jobProperties.getArguments();
		final StringBuffer argElements = new StringBuffer();
		for (final String arg : args) {
			argElements.append("<Argument>" + arg + "</Argument>\n");
		}
		jsdl = jsdl.replaceAll(ARGUMENTS_PLACEHOLDER, argElements.toString());
		// this will be calculated on the backend now.
		jsdl = jsdl.replaceAll(WORKINGDIRECTORY_PLACEHOLDER, "");
		jsdl = jsdl.replaceAll(STDOUT_PLACEHOLDER, jobProperties.getStdout());
		jsdl = jsdl.replaceAll(STDERR_PLACEHOLDER, jobProperties.getStderr());
		jsdl = jsdl.replaceAll(MODULE_PLACEHOLDER, jobProperties.getModule());
		jsdl = jsdl.replaceAll(EMAIL_PLACEHOLDER,
				jobProperties.getEmailAddress());
		final int noCpus = jobProperties.getNoCPUs();
		final int cpuTime = jobProperties.getWalltimeInSeconds() * noCpus;
		final int memory = jobProperties.getMemory();
		jsdl = jsdl.replaceAll(TOTALCPUTIME_PLACEHOLDER,
				new Integer(cpuTime).toString());
		jsdl = jsdl.replaceAll(TOTALCPUCOUNT_PLACEHOLDER,
				new Integer(noCpus).toString());
		jsdl = jsdl.replaceAll(MEMORY_PLACEHOLDER,
				new Integer(memory).toString());
		jsdl = jsdl.replaceAll(SUBMISSIONLOCATION_PLACEHOLDER,
				jobProperties.getSubmissionLocation());
		// this will be calculated on the backend now
		jsdl = jsdl.replaceAll(USEREXECUTIONHOSTFS_PLACEHOLDER, "");

		if (verbose) {
			System.out.println("Job description prepared:");
			System.out.println(jsdl);
			System.out.println("\nCreating job on grisu backend...");
		}

		String jobname;
		try {
			jobname = serviceInterface.createJob(jsdl, jobProperties.getVO(),
					null);
		} catch (final JobPropertiesException e1) {
			throw new RuntimeException("Can't create job on backend: "
					+ e1.getLocalizedMessage());

		}
		if (verbose) {
			System.out.println("Job created.");
			System.out.println("Setting job description...");
		}

		if (verbose) {
			System.out.println("Job description set.");
		}

		String inputFiles = null;
		final String[] inputFilesStrings = jobProperties.getInputFiles();
		if (inputFilesStrings.length > 0) {
			if (verbose) {
				System.out.println("Uploading input files:");
			}
			inputFiles = stageInputFiles(inputFilesStrings,
					jobProperties.getAbsoluteJobDir());

			if (verbose) {
				System.out.println("Upload successful.");
			} else {
				if (verbose) {
					System.out.println("No files to stagein.");
				}
			}
		}
		if (verbose) {
			System.out.println("Submitting job...");
		}
		try {
			serviceInterface.submitJob(jobname);
		} catch (final Exception e) {
			throw new RuntimeException("Job submission failed: "
					+ e.getLocalizedMessage(), e);
		}
		if (verbose) {
			System.out.println("Job submitted.");
			System.out
					.println("Adding job properties for this job on the grisu backend...");
		}

		// TODO this won't work anymore. better to use the uploadInputFiles
		// method now. This will add this property automatically.
		// if (!StringUtils.isEmpty(inputFiles) && inputFiles.length() <= 253) {
		// serviceInterface.addJobProperty(jobname,
		// Constants.INPUT_FILE_URLS_KEY, inputFiles);
		// }

		if (verbose) {
			System.out.println("Job submission finished successfully.");
		}
	}

	private void executeWholeJobsubmissionCycle() throws ExecutionException {

		try {
			executeSubmission();
		} catch (final Exception e) {
			throw new ExecutionException("Couldn't submit job: "
					+ e.getLocalizedMessage(), e);
		}

		joinJob();

	}

	/**
	 * private EnvironmentManager getEnvironmentManager() {
	 * 
	 * if (em == null) { em = new EnvironmentManager(serviceInterface); } return
	 * em; }
	 **/

	private int getStatus() {

		final int status = serviceInterface.getJobStatus(jobProperties
				.getJobname());

		return status;
	}

	private void joinJob() throws ExecutionException {

		int status = -1;
		int sleepTime = clientProperties.getRecheckInterval();
		if (sleepTime <= 0) {
			sleepTime = DEFAULT_SLEEP_TIME_IN_SECONDS;
		}
		do {

			status = checkStatus();

			if (status < JobConstants.FINISHED_EITHER_WAY) {
				try {
					Thread.sleep(sleepTime * 1000);
				} catch (final InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} while (status < JobConstants.FINISHED_EITHER_WAY);

	}

	private void killAndCleanJob() throws ExecutionException {

		try {
			if (verbose) {
				System.out
						.println("Trying to kill possibly existing job with jobname \""
								+ jobProperties.getJobname() + "\"");
			}
			serviceInterface.kill(jobProperties.getJobname(), true);
			if (verbose) {
				System.out.println("Killed and cleaned existing job \""
						+ jobProperties.getJobname() + "\"");
			}
		} catch (final NoSuchJobException e) {
			// that's ok
			if (verbose) {
				System.out.println("No job killed because no job with jobname "
						+ jobProperties.getJobname() + " existed.");
			}
		} catch (final Exception e) {
			throw new ExecutionException(
					"Can't kill & clean existing job with jobname \""
							+ jobProperties.getJobname() + "\"");
		}

	}

	private void login() throws ServiceInterfaceException, LoginException {
		try {
			if (verbose) {
				System.out.println("Login to grisu backend: "
						+ clientProperties.getServiceInterfaceUrl() + "...");
			}
			serviceInterface = LoginManager.login(clientProperties
					.getServiceInterfaceUrl());
			if (verbose) {
				System.out.println("Login successful.");
			}
		} catch (final LoginException e) {
			throw new LoginException(e.getMessage());
		}
	}

	private void login(String username, char[] password)
			throws ServiceInterfaceException, LoginException {

		if (verbose) {
			System.out.println("Login to grisu backend: "
					+ clientProperties.getServiceInterfaceUrl() + "...");
		}

		final LoginParams loginParams = new LoginParams(
				clientProperties.getServiceInterfaceUrl(), username, password,
				DEFAULT_MYPROXY_SERVER, DEFAULT_MYPROXY_PORT);
		serviceInterface = LoginManager.login(null, null, null, null,
				loginParams, false);

		if (verbose) {
			System.out.println("Login successful.");
		}

	}

	private void loginWithShibboleth(String username, String idp) {
		try {
			final ConsoleReader consoleReader = new ConsoleReader();
			System.out.println(clientProperties.getServiceInterfaceUrl());
			final char[] password = consoleReader.readLine(
					"Please enter shibboleth password: ", new Character('*'))
					.toCharArray();
			System.out.println(username);
			LoginManager.shiblogin(username, password, idp,
					clientProperties.getServiceInterfaceUrl(), true);
		} catch (final IOException ie) {
			throw new RuntimeException(ie);
		} catch (final LoginException le) {
			throw new RuntimeException(le);
		}
	}

	/**
	 * Uploads local files via the grisu backend to a target directory.
	 * 
	 * @param uris
	 *            the uris of the local files to upload
	 * @param targetDirectory
	 *            the target directory
	 * @return all the urls of the staged files, seperated with a comma
	 */
	public String stageInputFiles(String[] uris, String targetDirectory)
			throws JobStagingException {

		final StringBuffer inputFiles = new StringBuffer();
		for (final String uri : uris) {

			DataSource dataSource = null;
			String fileName = null;
			try {
				final File file = new File(new URI(uri));

				if (!file.exists()) {
					throw new JobStagingException("Local file " + uri
							+ " does not exist.");
				}

				dataSource = new FileDataSource(file);
				fileName = file.getName();
			} catch (final URISyntaxException e) {
				throw new JobStagingException("Couldn't stage in file: " + uri
						+ ": Wrong uri format.", e);
			}

			try {
				if (verbose) {
					System.out.println("Uploading file " + fileName + " to "
							+ targetDirectory);
				}
				final String targetFile = serviceInterface.upload(
						new DataHandler(dataSource), targetDirectory + "/"
								+ fileName);
				inputFiles.append(targetFile + ",");
			} catch (final Exception e) {
				throw new JobStagingException("Couldn't stage in file: " + uri
						+ ": " + e.getLocalizedMessage(), e);
			}
		}

		return inputFiles.toString();
	}

	public void stageoutFiles() {
		/**
		 * if (verbose) { System.out
		 * .println("Getting job details to find out url of jobdirectory..."); }
		 * 
		 * DtoJob jobDetails =
		 * serviceInterface.getJob(jobProperties.getJobname());
		 * 
		 * String jobDirectory = jobDetails
		 * .readJobProperty(Constants.JOBDIRECTORY_KEY);
		 * 
		 * GrisuFileObject source = null; try { source =
		 * getEnvironmentManager().getFileManager().getFileObject(
		 * jobDirectory); } catch (URISyntaxException e) { throw new
		 * FileTransferException("Could not transfer jobdirectory " +
		 * jobDirectory + ": " + e.getLocalizedMessage()); }
		 * 
		 * File stageOutDir = null; String stageoutDirPath =
		 * clientProperties.getStageoutDirectory();
		 * 
		 * if (StringUtils.isEmpty(stageoutDirPath)) { stageOutDir = new
		 * File("."); } else { stageOutDir = new File(stageoutDirPath); }
		 * 
		 * GrisuFileObject target = null; target =
		 * getEnvironmentManager().getFileManager().getFileObject(
		 * stageOutDir.toURI());
		 * 
		 * FileTransfer transfer = new FileTransfer( new GrisuFileObject[] {
		 * source }, target, FileTransfer.DONT_OVERWRITE);
		 * //transfer.addListener(this); transfer.startTransfer(true);
		 **/
	}

	public void start() throws ExecutionException {

		if (serviceInterface == null) {
			System.err
					.println("Could not find valid serviceInterface. Are you logged in?");
			System.exit(1);
		}

		final String mode = clientProperties.getMode();
		if (GrisuClientCommandlineProperties.SUBMIT_MODE_PARAMETER.equals(mode)) {
			try {
				executeSubmission();
			} catch (final Exception e) {
				throw new ExecutionException("Couldn't submit job: "
						+ e.getLocalizedMessage(), e);
			}
		} else if (GrisuClientCommandlineProperties.STATUS_MODE_PARAMETER
				.equals(mode)) {

			final int status = checkStatus();
			System.out.println("Status for job " + jobProperties.getJobname()
					+ ": " + JobConstants.translateStatus(status));

		} else if (GrisuClientCommandlineProperties.FORCE_CLEAN_MODE_PARAMETER
				.equals(mode)) {

			killAndCleanJob();

		} else if (GrisuClientCommandlineProperties.JOIN_MODE_PARAMETER
				.equals(mode)) {

			joinJob();

		} else if (GrisuClientCommandlineProperties.ALL_MODE_PARAMETER
				.equals(mode)) {

			executeWholeJobsubmissionCycle();

		} else if (GrisuClientCommandlineProperties.FORCE_ALL_MODE_PARAMETER
				.equals(mode)) {

			forced_all_mode = true;
			executeWholeJobsubmissionCycle();

		} else if (GrisuClientCommandlineProperties.LOGIN_MODE_PARAMETER
				.equals(mode)) {
			// do nothing - login already takes care of things
		}

	}

	/*
	 * public void fileTransferEventOccured(FileTransferEvent e) {
	 * 
	 * if (verbose) {
	 * System.out.println(e.getTransfer().getLatestTransferMessage()); }
	 * 
	 * }
	 */
}
