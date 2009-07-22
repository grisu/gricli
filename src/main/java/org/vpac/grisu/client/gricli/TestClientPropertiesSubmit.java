package org.vpac.grisu.client.gricli;

public class TestClientPropertiesSubmit implements GrisuClientProperties {

	public boolean cleanAfterStageOut() {
		return true;
	}

	public String getMode() {
		return "submit";
	}

	public String getMyProxyUsername() {
		return "markus";
	}

	public int getRecheckInterval() {
		return 5;
	}

	public String getServiceInterfaceUrl() {
		return "https://ngportaldev.vpac.org/grisu-ws/services/grisu";
	}

	public String getStageoutDirectory() {
		return null;
	}

	public boolean killPossiblyExistingJob() {
		return true;
	}

	public boolean stageOutResults() {
		return false;
	}

	public boolean verbose() {
		return false;
	}

	public boolean debug() {
		return false;
	}

	public boolean useLocalProxy() {
		return false;
	}

}
