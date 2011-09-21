package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.FileCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.gricli.util.OutputHelpers;
import grisu.jcommons.utils.FileAndUrlHelpers;
import grisu.model.FileManager;
import grisu.model.GrisuRegistryManager;
import grisu.model.dto.GridFile;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.python.google.common.collect.Lists;
import org.python.google.common.collect.Sets;

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

			List<List<String>> listlist = Lists.newLinkedList();
			List<String> title = Lists.newLinkedList();
			title.add("Filename");
			title.add("Size");
			title.add("Last modified");
			listlist.add(title);

			Set<GridFile> filesToList = null;
			if (list.isFolder()) {
				filesToList = list.getChildren();
			} else {
				filesToList = Sets.newHashSet();
				filesToList.add(list);
			}

			for (GridFile c : filesToList) {
				List<String> child = Lists.newLinkedList();
				String filename = null;
				if (c.isFolder()) {
					filename = c.getName() + "/";
					// if (c.isVirtual()) {
					// filename = new ANSIBuffer().red(filename)
					// .toString();
					// }
					child.add(filename);
					child.add("");
				} else {
					filename = c.getName();
					// if (c.isVirtual()) {
					// filename = new ANSIBuffer().red(filename)
					// .toString();
					// }
					child.add(filename);
					child.add(FileAndUrlHelpers.calculateSizeString(c.getSize()));
				}
				try {
					if (c.getLastModified() <= 0) {
						child.add("n/a");
					} else {
						Date d = new Date(c.getLastModified());
						child.add(DateFormat.getInstance().format(d));
					}
				} catch (Exception e) {
					child.add("n/a");
				}
				listlist.add(child);
			}

			String table = OutputHelpers.getTable(listlist, true, 20,
					new Integer[] { 1, 2 });
			env.printMessage(table);


		} catch (RemoteFileSystemException e) {
			throw new GricliRuntimeException(e);
		}

		return env;
	}

}
