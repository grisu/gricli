package org.vpac.grisu.client.gricli;

import java.io.File;

import org.vpac.grisu.control.ServiceInterface;

public class TestJobProperties extends AbstractJobProperties implements
		JobProperties {

	private String subLoc = null;
	private String vo = null;

	public TestJobProperties(ServiceInterface serviceInterface, String subLoc,
			String vo) {
		this.serviceInterface = serviceInterface;
		this.subLoc = subLoc;
		this.vo = vo;
	}

	public String getApplicationName() {
		return "test";
	}

	public String[] getArguments() {
		return new String[] { "test1.xxx" };
	}

	public String getEmailAddress() {
		return "";
	}

	public String getExecutablesName() {
		return "cat";
	}

	public String[] getInputFiles() {
		return new String[] { new File("/home/markus/tmp/test1.xxx").toURI()
				.toString() };
	}

	public String getJobname() {

		return ("AUTO_TEST_JOB_" + subLoc).replaceAll("\\s|;|'|\"|,|\\$|\\?|#",
				"_");
	}

	public int getMemory() {
		return 1024;
	}

	public String getModule() {
		return "";
	}

	public int getNoCPUs() {
		return 1;
	}

	public String getStderr() {
		return "stderr.txt";
	}

	public String getStdout() {
		return "stdout.txt";
	}

	public String getSubmissionLocation() {
		return subLoc;
	}

	public String getVO() {
		return vo;
	}

	public int getWalltimeInSeconds() {
		return 60;
	}

}
