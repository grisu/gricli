package grisu.gricli.completors;

import java.util.Set;
import java.util.SortedSet;

public interface CompletionCache {

	public abstract String[] getAllFqans();

	public abstract Set<String> getAllQueues();

	public abstract Set<String> getAllSites();

	public abstract SortedSet<String> getJobnames();

	public abstract void refreshJobnames();

}