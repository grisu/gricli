package grisu.gricli.completors;

import grisu.frontend.control.jobMonitoring.RunningJobManager;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.LoginRequiredException;
import grisu.jcommons.constants.Constants;
import grisu.model.GrisuRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;

import org.apache.log4j.Logger;

public class CompletionCacheImpl implements CompletionCache {

	static final Logger myLogger = Logger.getLogger(CompletionCacheImpl.class
			.getName());

	// public static SortedSet<String> jobnames = new TreeSet<String>();
	// public static SortedSet<String> fqans = new TreeSet<String>();
	// public static String[] queues = new String[] {};
	// public static String[] sites = new String[] {};

	private final GricliEnvironment env;
	private final GrisuRegistry reg;
	private final RunningJobManager jm;

	public CompletionCacheImpl(GricliEnvironment env) throws LoginRequiredException {
		this.env = env;
		this.reg = env.getGrisuRegistry();
		this.jm = RunningJobManager.getDefault(this.env.getServiceInterface());
		new Thread() {
			@Override
			public void run() {
				getAllQueues();
				myLogger.debug("All queues loaded for completion");
			}
		}.start();
		new Thread() {
			@Override
			public void run() {
				String[] fqans = getAllFqans();
				myLogger.debug("All vos loaded for completion");
				for (String fqan : fqans) {
					getAllQueuesForFqan(fqan);
					myLogger.debug("All queues loaded for fqan: " + fqan);
				}
			}
		}.start();
		new Thread() {
			@Override
			public void run() {
				getAllSites();
				myLogger.debug("All sites loaded for completion");
			}
		}.start();

		new Thread() {
			@Override
			public void run() {
				getJobnames();
				myLogger.debug("All jobnames loaded for completion");
			}
		}.start();
		new Thread() {
			@Override
			public void run() {
				getAllApplications();
				myLogger.debug("All applications loaded for completion");
			}
		}.start();
	}

	public String[] getAllApplications() {
		ArrayList<String> results = new ArrayList<String>();
		Collections.addAll(results, this.reg.getUserEnvironmentManager()
				.getAllAvailableApplications());
		results.add(0, Constants.GENERIC_APPLICATION_NAME);

		return results.toArray(new String[] {});
	}

	/* (non-Javadoc)
	 * @see grisu.gricli.completors.CompletionCache#getAllFqans()
	 */
	public String[] getAllFqans() {
		return this.reg.getUserEnvironmentManager().getAllAvailableFqans(true);
	}

	/* (non-Javadoc)
	 * @see grisu.gricli.completors.CompletionCache#getAllQueues()
	 */
	public Set<String> getAllQueues() {
		return this.reg.getUserEnvironmentManager().getAllAvailableSubmissionLocations();
	}

	public String[] getAllQueuesForFqan(String fqan) {
		return this.reg.getResourceInformation().getAllAvailableSubmissionLocations(fqan);
	}

	/* (non-Javadoc)
	 * @see grisu.gricli.completors.CompletionCache#getAllSites()
	 */
	public Set<String> getAllSites() {
		return this.reg.getUserEnvironmentManager().getAllAvailableSites();
	}

	public GricliEnvironment getEnvironment() {
		return env;
	}

	/* (non-Javadoc)
	 * @see grisu.gricli.completors.CompletionCache#getJobnames()
	 */
	public SortedSet<String> getJobnames() {
		return this.reg.getUserEnvironmentManager().getReallyAllJobnames(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grisu.gricli.completors.CompletionCache#refreshJobnames()
	 */
	public void refreshJobnames() {
		new Thread() {
			@Override
			public void run() {
				reg.getUserEnvironmentManager().getReallyAllJobnames(true);
			}
		}.start();
	}

}
