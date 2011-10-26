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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputFileCompletor implements Completor {

	private static Logger myLogger = LoggerFactory
			.getLogger(InputFileCompletor.class
			.getName());

	public int complete(String arg0, int arg1, List arg2) {

		final GricliEnvironment env = Gricli.completionCache.getEnvironment();

		try {
			final List<String> files = (List<String>) env.getVariable("files")
					.get();
			final Set<String> names = new HashSet<String>();
			for (final String file : files) {
				final String filename = FileManager.getFilename(file);
				names.add(filename);
			}

			return new SimpleCompletor(names.toArray(new String[] {}))
			.complete(arg0, arg1, arg2);
		} catch (final GricliRuntimeException e) {
			myLogger.error(e.getLocalizedMessage(), e);
			return -1;
		}
	}

}
