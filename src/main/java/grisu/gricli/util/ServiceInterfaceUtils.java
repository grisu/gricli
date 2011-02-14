package grisu.gricli.util;

import grisu.control.ServiceInterface;

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;

public class ServiceInterfaceUtils {

	public static List<String> filterJobNames(ServiceInterface si, String filter) {
		LinkedList<String> result = new LinkedList<String>();
		for (String jobname : si.getAllJobnames(null).asArray()) {
			if (FilenameUtils.wildcardMatch(jobname, filter)) {
				result.add(jobname);
			}
		}
		return result;
	}
}
