package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.model.dto.GridFile;

/**
 * experiment with new style of filemanager.
 */

public class FilemanagerCommand implements GricliCommand {

	private final String url;

	@SyntaxDescription(command = { "filemanager" })
	public FilemanagerCommand(String url) {
		this.url = url;
	}

	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {
		final ServiceInterface si = env.getServiceInterface();
		try {
			final GridFile folder = si.ls(url, 1);
			for (final GridFile file : folder.getChildren()) {
				if (file.isFolder()) {
					env.printMessage(file.getName());
				} else {
					env.printMessage(file.getName() + "/");
				}
			}

		} catch (final RemoteFileSystemException ex) {
			throw new GricliRuntimeException(ex);
		}
	}

}
