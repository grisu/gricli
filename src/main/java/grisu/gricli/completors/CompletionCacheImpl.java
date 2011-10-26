package grisu.gricli.completors;

import grisu.control.exceptions.RemoteFileSystemException;
import grisu.gricli.LoginRequiredException;
import grisu.gricli.completors.file.StillLoadingException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.constants.Constants;
import grisu.model.GrisuRegistry;
import grisu.model.dto.DtoJob;
import grisu.model.dto.GridFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompletionCacheImpl implements CompletionCache {

	static final Logger myLogger = LoggerFactory
			.getLogger(CompletionCacheImpl.class);

	public final Set<String> currentlyListedUrls = Collections
			.synchronizedSet(new HashSet<String>());

	private final GricliEnvironment env;
	private final GrisuRegistry reg;

	private String[] fqans = new String[] { "*** Loading...", "...try again***" };
	private String[] applications = new String[] { "*** Loading...",
	"...try again***" };

	private static Cache cache = CacheManager.getInstance().getCache("short");

	public CompletionCacheImpl(GricliEnvironment env)
			throws LoginRequiredException {
		this.env = env;
		this.reg = env.getGrisuRegistry();
		// this.jm =
		// RunningJobManager.getDefault(this.env.getServiceInterface());
		Thread t = new Thread() {
			@Override
			public void run() {
				getAllQueues();
				myLogger.debug("All queues loaded for completion");
			}
		};
		t.setName("getAllQueuesBackgroundThread");
		t.start();
		Thread t2 = new Thread() {
			@Override
			public void run() {
				final String[] fqans = CompletionCacheImpl.this.reg
						.getUserEnvironmentManager().getAllAvailableFqans(true);
				CompletionCacheImpl.this.fqans = fqans;
				myLogger.debug("All vos loaded for completion");
				for (final String fqan : fqans) {
					getAllQueuesForFqan(fqan);
					myLogger.debug("All queues loaded for fqan: " + fqan);
				}
			}
		};
		t2.setName("groupsAndSublocLoadBackgroundThread");
		t2.start();
		Thread t3 = new Thread() {
			@Override
			public void run() {
				final ArrayList<String> results = new ArrayList<String>();
				Collections.addAll(results, CompletionCacheImpl.this.reg
						.getUserEnvironmentManager()
						.getAllAvailableApplications());
				results.add(0, Constants.GENERIC_APPLICATION_NAME);

				final String[] apps = results.toArray(new String[] {});

				CompletionCacheImpl.this.applications = apps;
				myLogger.debug("All applications loaded for completion");
			}
		};
		t3.setName("appInfoLoadBackgroundThread");
		t3.start();

		Thread t4 = new Thread() {
			@Override
			public void run() {
				getJobnames();
				myLogger.debug("All jobnames loaded for completion");
			}
		};
		t4.setName("jobnameLoadBackgroundThread");
		t4.start();
		Thread t5 = new Thread() {
			@Override
			public void run() {
				getAllSites();
				myLogger.debug("All sites loaded for completion");
			}
		};
		t5.setName("siteLoadBackgroundThread");
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
	public String[] getAllFqans() {
		return fqans;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grisu.gricli.completors.CompletionCache#getAllQueues()
	 */
	public Set<String> getAllQueues() {
		return this.reg.getUserEnvironmentManager()
				.getAllAvailableSubmissionLocations();
	}

	public String[] getAllQueuesForFqan(String fqan) {
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

}
