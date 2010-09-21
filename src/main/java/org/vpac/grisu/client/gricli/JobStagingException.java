package org.vpac.grisu.client.gricli;

public class JobStagingException extends Exception {

	public JobStagingException(String message) {
		super(message);
	}

	public JobStagingException(String message, Exception e) {
		super(message, e);
	}

}
