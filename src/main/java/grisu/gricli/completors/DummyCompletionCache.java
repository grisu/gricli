package grisu.gricli.completors;

import grisu.gricli.environment.GricliEnvironment;
import grisu.model.dto.DtoJob;
import grisu.model.dto.GridFile;
import grisu.model.info.dto.Queue;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class DummyCompletionCache implements CompletionCache {

	private final String[] dummyArray = new String[] {};
	private final Queue[] dummyQueueArray = new Queue[] {};
	private final SortedSet<String> dummySet = new TreeSet<String>();
	private final HashSet<Queue> dummyQueueSet = new HashSet<Queue>();

	private final GridFile dummyFile = new GridFile();

	public void addFileListingToCache(String urlToList, GridFile list) {

	}

	public String[] getAllApplications() {
		return dummyArray;
	}

	public String[] getAllFqans() {
		return dummyArray;
	}

	public Set<Queue> getAllQueues() {
		return dummyQueueSet;
	}

	public Queue[] getAllQueuesForFqan(String fqan) {
		return dummyQueueArray;
	}

	public Set<String> getAllSites() {
		return dummySet;
	}

	public SortedSet<DtoJob> getCurrentJobs(boolean forceRefresh) {
		return new TreeSet<DtoJob>();
	}

	public GricliEnvironment getEnvironment() {
		return null;
	}

	public SortedSet<String> getJobnames() {
		return dummySet;
	}

	public GridFile ls(String url) {
		return dummyFile;
	}

	public void refreshJobnames() {
	}

	public void removeFileListingFromCache(String url) {
	}

}
