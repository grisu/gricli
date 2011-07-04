package grisu.gricli.completors;

import grisu.gricli.GricliEnvironment;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class DummyCompletionCache implements CompletionCache {

	private final String[] dummyArray = new String[] {};
	private final SortedSet<String> dummySet = new TreeSet<String>();

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

	public void refreshJobnames() {
	}

}
