package org.vpac.grisu.client.gricli;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jline.ConsoleReader;

import org.vpac.grisu.client.control.exceptions.JobSubmissionException;
import org.vpac.grisu.client.control.login.LoginException;
import org.vpac.grisu.client.control.login.LoginHelpers;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.JobCreationException;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.frontend.control.login.LoginParams;

public class TestClient {

	public static final String DEFAULT_MYPROXY_SERVER = "myproxy.arcs.org.au";
	public static final String DEFAULT_MYPROXY_PORT = "443";

	private ServiceInterface serviceInterface = null;
	private boolean verbose = false;
	private String vo = "/ARCS/NGAdmin";

	private GrisuClientProperties clientProperties = new TestClientPropertiesSubmit();
	
	private Set<String> submittedJobs =  new HashSet<String>();
	private Set<String> finishedJobs = new HashSet<String>();
	private Map<String, Throwable> failedJobs = new HashMap<String, Throwable>();
	
	private int noJobs = -1;

	public TestClient(char[] password) throws LoginException,
			ServiceInterfaceException {

		login(clientProperties.getMyProxyUsername(), password);
	}

	private void login(String username, char[] password) throws LoginException,
			ServiceInterfaceException {

		if (verbose) {
			System.out.println("Login to grisu backend: "
					+ clientProperties.getServiceInterfaceUrl() + "...");
		}
		LoginParams loginParams = new LoginParams(clientProperties
				.getServiceInterfaceUrl(), username, password,
				DEFAULT_MYPROXY_SERVER, DEFAULT_MYPROXY_PORT);
		serviceInterface = LoginHelpers.login(loginParams);

		if (verbose) {
			System.out.println("Login successful.");
		}
	}

	public void startJobSubmissions() {

		String[] sublocs = serviceInterface.getAllSubmissionLocationsForFqan(vo).getSubmissionLocationStrings();
		noJobs = sublocs.length;
		
		for (String subLoc : sublocs) {
			startSingleJobSubmission(subLoc);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	public void startCheckingStatus() {

		System.out.println("All job submission threads started. Waiting for all jobs submissions to finish...");
		while ( submittedJobs.size() + failedJobs.size() != noJobs ) {
//			System.out.println("Waiting for all jobs submissions to finish...");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("All job submissions finished.");
		checkStatus(submittedJobs);
	}
	
	private void checkStatus(Set<String> jobSet) {
		
		for ( String job : submittedJobs ) {
			
			if ( serviceInterface.getJobStatus(job) >= JobConstants.FINISHED_EITHER_WAY ) {
				finishedJobs.add(job);
				stageOutFiles(job);
			}
			
		}

		submittedJobs.removeAll(finishedJobs);
		if ( submittedJobs.size() > 0 ) {
			try {
				System.out.println("Waiting for all jobs to finish...("+submittedJobs.size()+" still running)");
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			checkStatus(submittedJobs);
		}
	}
	
	private void stageOutFiles(String jobname) {
		
		System.out.println("Staging out files for job: "+jobname);
	}

	private void startSingleJobSubmission(final String subLoc) {

//		new Thread() {
//			public void run() {

				JobProperties jobProperties = new TestJobProperties(
						serviceInterface, subLoc, vo);
				System.out.println("Starting job submission for job: "+jobProperties.getJobname());
				Gricli client = new Gricli(serviceInterface,
						clientProperties, jobProperties);

				try {
					client.start();
					submittedJobs.add(jobProperties.getJobname());
					System.out.println("Job "+jobProperties.getJobname()+" submitted.");
				} catch (ExecutionException e) {
					Throwable cause = e.getCause();
					
					if ( cause instanceof JobStagingException ) {
						System.err.println("Couldn't stage files for job "+jobProperties.getJobname()+": "+cause.getLocalizedMessage());
					} else if ( cause instanceof JobSubmissionException ){
						System.err.println("Couldn't submit job "+jobProperties.getJobname()+": "+cause.getLocalizedMessage());
					} else if ( cause instanceof JobCreationException ) {
						System.err.println("Couldn't create job "+jobProperties.getJobname()+": "+cause.getLocalizedMessage());
					} else {
						System.err.println("Can't determine why job "+jobProperties.getJobname()+" could not be submitted.");
					}
					failedJobs.put(jobProperties.getJobname(), cause);
				}
//			}
//		}.start();

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		char[] password = null;

		// ask user
		try {
			ConsoleReader consoleReader = new ConsoleReader();
			password = consoleReader.readLine(
					"Please enter your myproxy password: ", new Character('*'))
					.toCharArray();
		} catch (IOException e) {
			System.err.println("Could not read password input: "
					+ e.getLocalizedMessage());
			System.exit(1);
		}

		TestClient client = null;
		try {
			client = new TestClient(password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		client.startJobSubmissions();
		client.startCheckingStatus();
	}

}
