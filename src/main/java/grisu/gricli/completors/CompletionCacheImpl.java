package grisu.gricli.completors;

import grisu.control.exceptions.RemoteFileSystemException;
import grisu.gricli.LoginRequiredException;
import grisu.gricli.completors.file.StillLoadingException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.constants.Constants;
import grisu.model.FileCache;
import grisu.model.GrisuRegistry;
import grisu.model.dto.DtoJob;
import grisu.model.dto.GridFile;
import grisu.model.info.dto.Application;
import grisu.model.info.dto.Queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public class CompletionCacheImpl implements CompletionCache {

	static final Logger myLogger = LoggerFactory
			.getLogger(CompletionCacheImpl.class);

	public final Set<String> currentlyListedUrls = Collections
			.synchronizedSet(new HashSet<String>());

	private final GricliEnvironment env;
	private final GrisuRegistry reg;

	// private static final String[] LOADING_VOS = new String[] {
	// "*** Loading...", "...try again***" };

	private String[] fqans = null;
	private SortedSet<String> allUsers = null;
	private String[] applications = new String[] { "*** Loading...",
	"...try again***" };

	private static Cache cache = FileCache.shortCache;

	public CompletionCacheImpl(GricliEnvironment env)
			throws LoginRequiredException {
		this.env = env;
		this.reg = env.getGrisuRegistry();
		// this.jm =
		// RunningJobManager.getDefault(this.env.getServiceInterface());
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					getAllQueues();
					myLogger.debug("All queues loaded for completion");
				} catch (Exception e) {
					myLogger.error("Can't load queues.", e);
				}
			}
		};
		t.setName("getAllQueuesBackgroundThread");
		// t.setDaemon(true);
		t.start();
		Thread t2 = new Thread() {
			@Override
			public void run() {
				final String[] fqans = getAllFqans();
				myLogger.debug("All vos loaded for completion");
				for (final String fqan : fqans) {
					getAllQueuesForFqan(fqan);
					myLogger.debug("All queues loaded for fqan: " + fqan);
				}
			}
		};
		t2.setName("groupsAndSublocLoadBackgroundThread");
		// t2.setDaemon(true);
		t2.start();
		Thread t3 = new Thread() {
			@Override
			public void run() {
				final ArrayList<Application> results = new ArrayList<Application>();
				Collections.addAll(results, CompletionCacheImpl.this.reg
						.getUserEnvironmentManager()
						.getAllAvailableApplications());
				results.add(0, Application.GENERIC_APPLICATION);

				final Application[] apps = results.toArray(new Application[] {});
				CompletionCacheImpl.this.applications = new String[apps.length];
				for (int i = 0; i < apps.length; i++) {
					CompletionCacheImpl.this.applications[i] = apps[i]
							.getName();
				}
				myLogger.debug("All applications loaded for completion");
			}
		};
		t3.setName("appInfoLoadBackgroundThread");
		// t3.setDaemon(true);
		t3.start();

		Thread t4 = new Thread() {
			@Override
			public void run() {
				getJobnames();
				myLogger.debug("All jobnames loaded for completion");
			}
		};
		t4.setName("jobnameLoadBackgroundThread");
		// t4.setDaemon(true);
		t4.start();
		Thread t5 = new Thread() {
			@Override
			public void run() {
				getAllSites();
				myLogger.debug("All sites loaded for completion");
			}
		};
		t5.setName("siteLoadBackgroundThread");
		// t5.setDaemon(true);
		t5.start();
	}

	public void addFileListingToCache(String urlToList, GridFile list) {
		final Element el = new Element(urlToList, list);
		cache.put(el);

	}

	public String[] getAllApplications() {
		return applications;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grisu.gricli.completors.CompletionCache#getAllFqans()
	 */
	public synchronized String[] getAllFqans() {
		if ((fqans == null)) {
			fqans = CompletionCacheImpl.this.reg.getUserEnvironmentManager()
					.getAllAvailableFqans(true);

		}
		return fqans;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grisu.gricli.completors.CompletionCache#getAllQueues()
	 */
	public Set<Queue> getAllQueues() {
		return this.reg.getUserEnvironmentManager()
				.getAllAvailableSubmissionLocations();
	}

	public Queue[] getAllQueuesForFqan(String fqan) {
		return this.reg.getResourceInformation()
				.getAllAvailableSubmissionLocations(fqan);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grisu.gricli.completors.CompletionCache#getAllSites()
	 */
	public Set<String> getAllSites() {
		return this.reg.getUserEnvironmentManager().getAllAvailableSites();
	}

	public SortedSet<DtoJob> getCurrentJobs(boolean forceRefresh) {

		SortedSet<DtoJob> jobs = null;
		if (!forceRefresh) {
			try {
				jobs = (SortedSet<DtoJob>) cache.get(Constants.ALLJOBS_KEY);
			} catch (final Exception e) {
				// doesn't matter
			}
		}

		if (jobs == null) {
			jobs = this.reg.getUserEnvironmentManager().getCurrentJobs(true);
			final Element e = new Element(Constants.ALLJOBS_KEY, jobs);
			cache.put(e);
		}

		return jobs;
	}

	public GricliEnvironment getEnvironment() {
		return env;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grisu.gricli.completors.CompletionCache#getJobnames()
	 */
	public SortedSet<String> getJobnames() {
		return this.reg.getUserEnvironmentManager().getReallyAllJobnames(false);
	}

	public GridFile ls(final String url) throws StillLoadingException {

		if (cache.get(url) == null) {

			synchronized (url) {
				// if url is not in short time cache or
				// url is not loaded currently, load it now in background
				// and give back loading string...
				if (!currentlyListedUrls.contains(url)) {

					currentlyListedUrls.add(url);
					Thread t = new Thread() {
						@Override
						public void run() {

							try {
								final GridFile f = reg.getFileManager().ls(url);
								final Element e = new Element(url, f);
								cache.put(e);
							} catch (final RemoteFileSystemException e) {
								myLogger.error(e.getLocalizedMessage(), e);
								final GridFile f = new GridFile(url, false, e);
								final Element el = new Element(url, f);
								cache.put(el);
							} finally {
								currentlyListedUrls.remove(url);
							}
						}
					};
					t.setName("ls thread for: " + url);
					t.start();
				}

				throw new StillLoadingException(url);
			}
		}

		return (GridFile) (cache.get(url).getObjectValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grisu.gricli.completors.CompletionCache#refreshJobnames()
	 */
	public void refreshJobnames() {
		Thread t = new Thread() {
			@Override
			public void run() {
				reg.getUserEnvironmentManager().getReallyAllJobnames(true);
			}
		};
		t.setName("refresh jobnames thread");
		t.start();
	}

	public void removeFileListingFromCache(String url) {
		cache.remove(url);
	}

	@Override
	public SortedSet<String> getAllUsers() {
		
		if ((allUsers == null)) {
			try {
			allUsers = CompletionCacheImpl.this.env.getServiceInterface().admin(Constants.LIST_USERS, null).asSortedSet();
			} catch (Exception e) {
				allUsers = Sets.newTreeSet();
			}

		}
		return allUsers;
		
	}

	public void setAllUsers(SortedSet<String> allUsers) {
		this.allUsers = allUsers;
		
	}

}
