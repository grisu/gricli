package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.FileCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.dto.GridFile;
import jline.ANSIBuffer;

import org.apache.commons.lang.StringUtils;

public class LsCommand implements GricliCommand {

	final private String url;

	@SyntaxDescription(command = { "ls" }, arguments = { })
	@AutoComplete(completors = { FileCompletor.class })
	public LsCommand() {
		this.url = null;
	}

	@SyntaxDescription(command = { "ls" }, arguments = { "url" })
	@AutoComplete(completors = { FileCompletor.class })
	public LsCommand(String url) {
		this.url = url;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {

		ServiceInterface si = env.getServiceInterface();
		FileManager fm = GrisuRegistryManager.getDefault(si).getFileManager();

		String urlToList = url;
		if (StringUtils.isBlank(urlToList)) {
			urlToList = env.dir.toString();
		}


		try {
			GridFile list = fm.ls(urlToList);

			// so we don't need to call that again for completion
			Gricli.completionCache.addFileListingToCache(urlToList, list);

			if (list.isFolder()) {

				for (GridFile c : list.getChildren()) {
					String result = null;
					if (c.isFolder()) {
						result = c.getName() + "/";
					} else {
						result = c.getName();
					}
					if (c.isVirtual()) {
						result = new ANSIBuffer().red(result).toString();
					}
					env.printMessage(result);
				}
			} else {
				String msg = list.getName() + "\t\t" + list.getLastModified()
						+ list.getSize();
				env.printMessage(msg);
			}
		} catch (RemoteFileSystemException e) {
			throw new GricliRuntimeException(e);
		}

		return env;
	}

}
