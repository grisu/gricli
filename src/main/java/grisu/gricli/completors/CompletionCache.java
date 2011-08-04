package grisu.gricli.completors;

import grisu.gricli.environment.GricliEnvironment;
import grisu.model.dto.GridFile;

import java.util.Set;
import java.util.SortedSet;

public interface CompletionCache {

	public abstract String[] getAllApplications();

	public abstract String[] getAllFqans();

	public abstract Set<String> getAllQueues();

	public abstract String[] getAllQueuesForFqan(String fqan);

	public abstract Set<String> getAllSites();

	public abstract GricliEnvironment getEnvironment();

	public abstract SortedSet<String> getJobnames();

	public abstract GridFile ls(String url) throws StillLoadingException;

	public abstract void refreshJobnames();

	public abstract void removeFileListingFromCache(String url);

}