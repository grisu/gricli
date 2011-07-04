package grisu.gricli.completors;

import grisu.frontend.control.jobMonitoring.RunningJobManager;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.LoginRequiredException;
import grisu.model.GrisuRegistry;

import java.util.Set;
import java.util.SortedSet;

public class CompletionCache {

	public static CompletionCache singleton;

	// public static SortedSet<String> jobnames = new TreeSet<String>();
	// public static SortedSet<String> fqans = new TreeSet<String>();
	// public static String[] queues = new String[] {};
	// public static String[] sites = new String[] {};

	private final GricliEnvironment env;
	private final GrisuRegistry reg;
	private final RunningJobManager jm;

	public CompletionCache(GricliEnvironment env) throws LoginRequiredException {
		this.env = env;
		this.reg = env.getGrisuRegistry();
		this.jm = RunningJobManager.getDefault(this.env.getServiceInterface());
	}

	public String[] getAllFqans() {
		return this.reg.getUserEnvironmentManager().getAllAvailableFqans(true);
	}

	public Set<String> getAllQueues() {
		return this.reg.getUserEnvironmentManager().getAllAvailableSubmissionLocations();
	}

	public Set<String> getAllSites() {
		return this.reg.getUserEnvironmentManager().getAllAvailableSites();
	}

	public SortedSet<String> getJobnames() {
		return this.reg.getUserEnvironmentManager().getReallyAllJobnames(false);
	}

	public void refreshJobnames() {
		new Thread() {
			@Override
			public void run() {
				reg.getUserEnvironmentManager().getReallyAllJobnames(true);
			}
		}.start();
	}



}
