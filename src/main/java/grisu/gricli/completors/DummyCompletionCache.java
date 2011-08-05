package grisu.gricli.completors;

import grisu.gricli.environment.GricliEnvironment;
import grisu.model.dto.GridFile;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class DummyCompletionCache implements CompletionCache {

	private final String[] dummyArray = new String[] {};
	private final SortedSet<String> dummySet = new TreeSet<String>();

	private final GridFile dummyFile = new GridFile();

	public void addFileListingToCache(String urlToList, GridFile list) {

	}

	public String[] getAllApplications() {
		return dummyArray;
	}

	public String[] getAllFqans() {
		return dummyArray;
	}

	public Set<String> getAllQueues() {
		return dummySet;
	}

	public String[] getAllQueuesForFqan(String fqan) {
		return dummyArray;
	}

	public Set<String> getAllSites() {
		return dummySet;
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
