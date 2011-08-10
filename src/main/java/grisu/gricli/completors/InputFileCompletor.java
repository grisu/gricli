package grisu.gricli.completors;

import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.model.FileManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jline.Completor;
import jline.SimpleCompletor;

import org.apache.log4j.Logger;

public class InputFileCompletor implements Completor {

	private static Logger myLogger = Logger.getLogger(InputFileCompletor.class
			.getName());

	public int complete(String arg0, int arg1, List arg2) {

		GricliEnvironment env = Gricli.completionCache.getEnvironment();

		try {
			List<String> files = (List<String>) env.getVariable("files").get();
			Set<String> names = new HashSet<String>();
			for (String file : files) {
				String filename = FileManager.getFilename(file);
				names.add(filename);
			}

			return new SimpleCompletor(names.toArray(new String[] {}))
					.complete(arg0, arg1, arg2);
		} catch (GricliRuntimeException e) {
			myLogger.error(e);
			return -1;
		}
	}

}
