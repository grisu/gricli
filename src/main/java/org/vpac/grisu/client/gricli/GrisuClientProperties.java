package org.vpac.grisu.client.gricli;

import org.apache.commons.cli.CommandLine;

public interface GrisuClientProperties {

	public abstract String getStageoutDirectory();

	public abstract String getServiceInterfaceUrl();

	public abstract String getMode();

	public abstract boolean stageOutResults();

	public abstract boolean cleanAfterStageOut();

	public abstract boolean verbose();
	
	public abstract boolean debug();

	public abstract boolean useLocalProxy();

	public abstract boolean killPossiblyExistingJob();

	public abstract int getRecheckInterval();

	public abstract String getMyProxyUsername();

       public  abstract String getShibIdp(); 

       public  abstract String getShibUsername(); 

}