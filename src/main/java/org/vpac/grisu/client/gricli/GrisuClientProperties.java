package org.vpac.grisu.client.gricli;

public interface GrisuClientProperties {

	public abstract boolean cleanAfterStageOut();

	public abstract boolean debug();

	public abstract String getMode();

	public abstract String getMyProxyUsername();

	public abstract int getRecheckInterval();

	public abstract String getServiceInterfaceUrl();

	public abstract String getShibIdp();

	public abstract String getShibUsername();

	public abstract String getStageoutDirectory();

	public abstract boolean killPossiblyExistingJob();

	public abstract boolean stageOutResults();

	public abstract boolean useLocalProxy();

	public abstract boolean verbose();

}