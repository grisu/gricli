package grisu.gricli.completors;

import grisu.frontend.control.jobMonitoring.RunningJobManager;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.LoginRequiredException;
import grisu.model.GrisuRegistry;

import java.util.Set;
import java.util.SortedSet;

public class CompletionCacheImpl implements CompletionCache {

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
				getAllFqans();
			}
		}.start();
		new Thread() {
			@Override
			public void run() {
				getAllQueues();
			}
		}.start();
		new Thread() {
			@Override
			public void run() {
				getAllSites();
			}
		}.start();

		new Thread() {
			@Override
			public void run() {
				getJobnames();
			}
		}.start();
		new Thread() {
			@Override
			public void run() {

			}
		}.start();
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

	/* (non-Javadoc)
	 * @see grisu.gricli.completors.CompletionCache#getAllSites()
	 */
	public Set<String> getAllSites() {
		return this.reg.getUserEnvironmentManager().getAllAvailableSites();
	}

	/* (non-Javadoc)
	 * @see grisu.gricli.completors.CompletionCache#getJobnames()
	 */
	public SortedSet<String> getJobnames() {
		return this.reg.getUserEnvironmentManager().getReallyAllJobnames(false);
	}

	/* (non-Javadoc)
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
