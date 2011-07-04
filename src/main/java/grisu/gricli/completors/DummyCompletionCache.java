package grisu.gricli.completors;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class DummyCompletionCache implements CompletionCache {

	private final String[] dummyArray = new String[] {};
	private final SortedSet<String> dummySet = new TreeSet<String>();

	public String[] getAllFqans() {
		return dummyArray;
	}

	public Set<String> getAllQueues() {
		return dummySet;
	}

	public Set<String> getAllSites() {
		return dummySet;
	}

	public SortedSet<String> getJobnames() {
		return dummySet;
	}

	public void refreshJobnames() {
	}

}
