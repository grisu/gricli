package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.frontend.control.clientexceptions.FileTransactionException;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.FileCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.model.FileManager;

import java.io.File;
import java.util.Set;

import org.python.google.common.collect.Sets;

public class CpCommand implements GricliCommand {

	private static String getAbsolutePath(GricliEnvironment env, String input) {

		if (input.startsWith(ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME)
				|| input.startsWith(File.separator)) {
			return input;
		} else {
			if (input.startsWith("~")) {
				final String home = System.getProperty("user.home");
				return input.replaceFirst("~", home);
			}
			return env.getCurrentAbsoluteDirectory() + File.separator + input;
		}

	}

	private final String[] files;

	@SyntaxDescription(command = { "cp" }, arguments = { "urls" })
	@AutoComplete(completors = { FileCompletor.class })
	public CpCommand(String... files) {
		this.files = files;
	}

	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {

		if (this.files.length == 0) {
			throw new GricliRuntimeException("No arguments");
		}

		if (this.files.length == 1) {
			throw new GricliRuntimeException("No target file.");
		}

		String target = null;
		final Set<String> sources = Sets.newHashSet();
		boolean inBackground = false;
		if ("&".equals(this.files[this.files.length - 1])) {
			if (this.files.length == 2) {
				throw new GricliRuntimeException("No target file.");
			}
			inBackground = true;
			target = getAbsolutePath(env, this.files[this.files.length - 2]);
			for (int i = 0; i < (this.files.length - 2); i++) {
				sources.add(getAbsolutePath(env, this.files[i]));
			}
		} else {
			target = getAbsolutePath(env, this.files[this.files.length - 1]);
			for (int i = 0; i < (this.files.length - 1); i++) {
				sources.add(getAbsolutePath(env, this.files[i]));
			}
		}

		final FileManager fm = env.getGrisuRegistry().getFileManager();

		if (!fm.isFolder(target)) {
			throw new GricliRuntimeException("Target not a folder: " + target);
		}

		try {
			fm.cp(sources, target, true);
		} catch (final FileTransactionException e) {
			throw new GricliRuntimeException(e);
		}

	}

}
