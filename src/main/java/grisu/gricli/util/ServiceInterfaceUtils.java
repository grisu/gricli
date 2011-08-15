package grisu.gricli.util;

import grisu.control.ServiceInterface;
import grisu.model.GrisuRegistryManager;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;

public class ServiceInterfaceUtils {

	public static List<String> filterJobNames(ServiceInterface si, String filter) {
		LinkedList<String> result = new LinkedList<String>();
		for (String jobname : si.getAllJobnames(null).asArray()) {
			if (FilenameUtils.wildcardMatch(jobname, filter)) {
				result.add(jobname);
			}
		}	
		Collections.sort(result);
		return result;
	}
	
	public static List<String> filterApplicationNames(ServiceInterface si, String filter){
		LinkedList<String> result = new LinkedList<String>();
		for (String app: GrisuRegistryManager.getDefault(si).getUserEnvironmentManager().getAllAvailableApplications()){
			if (FilenameUtils.wildcardMatch(app, filter, IOCase.INSENSITIVE)){
				result.add(app);
			}
		}
		return result;
	}
}
