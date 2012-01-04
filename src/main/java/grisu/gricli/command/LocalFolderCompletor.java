package grisu.gricli.command;

import java.io.File;
import java.util.List;

import jline.Completor;
import jline.FileNameCompletor;

public class LocalFolderCompletor extends FileNameCompletor implements
Completor {

	@Override
	public int matchFiles(String buffer, String translated, File[] entries,
			List candidates) {
		if (entries == null) {
			return -1;
		}

		int matches = 0;

		for (File entrie : entries) {
			if (entrie.isDirectory()
					&& entrie.getAbsolutePath().startsWith(translated)) {
				matches++;
			}
		}

		for (File entrie : entries) {
			if (entrie.isDirectory()
					&& entrie.getAbsolutePath().startsWith(translated)) {
				String name = entrie.getName()
						+ (((matches == 1) && entrie.isDirectory()) ? File.separator
								: " ");

				candidates.add(name);
			}
		}

		final int index = buffer.lastIndexOf(File.separator);

		return index + File.separator.length();
	}

}
