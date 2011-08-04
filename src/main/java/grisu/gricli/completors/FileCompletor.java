package grisu.gricli.completors;

import grisu.control.ServiceInterface;
import grisu.gricli.Gricli;
import grisu.model.FileManager;
import grisu.model.dto.GridFile;
import grisu.settings.ClientPropertiesManager;

import java.io.File;
import java.util.List;
import java.util.Set;

import jline.Completor;

import org.python.google.common.collect.Sets;

import com.google.common.base.Strings;

public class FileCompletor implements Completor {

	public int complete(String s, int i, List l) {

		if (Strings.isNullOrEmpty(s) || FileManager.isLocal(s)) {
			return completeLocalFile(s, i, l);
		}

		String urlTemp = FileManager.calculateParentUrl(s);

		String url = FileManager.ensureTrailingSlash(urlTemp);

		GridFile f;
		try {
			f = Gricli.completionCache.ls(url);
		} catch (StillLoadingException e) {

			try {
				Thread.sleep(ClientPropertiesManager
						.getGricliCompletionSleepTimeInMS());
			} catch (InterruptedException e1) {
			}
			// try again
			try {
				f = Gricli.completionCache.ls(url);
			} catch (StillLoadingException e1) {
				l.add("*** loading...");
				l.add("...try again ***");
				return url.length();
			}

		}

		return matchRemoteFiles(s, s, f.getChildren(), l);
	}

	public int completeLocalFile(final String buf, final int cursor,
			final List candidates)
	{
		String buffer = buf == null ? "" : buf;

		String translated = buffer;

		// special character: ~ maps to the user's home directory
		if (translated.startsWith ("~" + File.separator))
		{
			translated = System.getProperty ("user.home")
					+ translated.substring (1);
		}
		else if (translated.startsWith ("~"))
		{
			translated = new File (System.getProperty ("user.home"))
			.getParentFile ().getAbsolutePath ();
		}
		else if (!(translated.startsWith (File.separator)))
		{
			translated = new File ("").getAbsolutePath ()
					+ File.separator + translated;
		}

		File f = new File (translated);

		final File dir;

		if (translated.endsWith (File.separator)) {
			dir = f;
		} else {
			dir = f.getParentFile ();
		}

		final File [] entries = dir == null ? new File [0] : dir.listFiles ();

		return matchFiles(buffer, translated, entries, candidates);

	}

	/**
	 *  Match the specified <i>buffer</i> to the array of <i>entries</i>
	 *  and enter the matches into the list of <i>candidates</i>. This method
	 *  can be overridden in a subclass that wants to do more
	 *  sophisticated file name completion.
	 *
	 *  @param      buffer          the untranslated buffer
	 *  @param      translated      the buffer with common characters replaced
	 *  @param      entries         the list of files to match
	 *  @param      candidates      the list of candidates to populate
	 *
	 *  @return  the offset of the match
	 */
	public int matchFiles (String buffer, String translated,
			File [] entries, List candidates)
	{
		if (entries == null) {
			return -1;
		}

		int matches = 0;
		if ((ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME + "://")
				.startsWith(buffer)) {
			matches = 1;
			//			String temp = new ANSIBuffer().blue(
			// ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME + "://") .toString();
			String temp = ServiceInterface.VIRTUAL_GRID_PROTOCOL_NAME+"://";

			candidates.add(temp);
		}

		// first pass: just count the matches
		for (File entrie : entries) {
			if (entrie.getAbsolutePath ().startsWith (translated))
			{
				matches++;
			}
		}

		// green - executable
		// blue - directory
		// red - compressed
		// cyan - symlink

		Set<String> tempSet = Sets.newTreeSet();
		for (File entrie : entries) {
			if (entrie.getAbsolutePath ().startsWith (translated))
			{
				String name = entrie.getName ()
						+ ((matches == 1) && entrie.isDirectory ()
								? File.separator : " ");

				/*
	                        if (entries [i].isDirectory ())
	                        {
	                                name = new ANSIBuffer ().blue (name).toString ();
	                        }
				 */

				tempSet.add(name);
			}
		}

		candidates.addAll(tempSet);

		final int index = buffer.lastIndexOf (File.separator);
		return index + File.separator.length ();
	}

	/**
	 * Match the specified <i>buffer</i> to the array of <i>entries</i> and
	 * enter the matches into the list of <i>candidates</i>. This method can be
	 * overridden in a subclass that wants to do more sophisticated file name
	 * completion.
	 * 
	 * @param buffer
	 *            the untranslated buffer
	 * @param translated
	 *            the buffer with common characters replaced
	 * @param entries
	 *            the list of files to match
	 * @param candidates
	 *            the list of candidates to populate
	 * 
	 * @return the offset of the match
	 */
	public int matchRemoteFiles(String buffer, String translated,
			Set<GridFile> entries, List candidates) {
		if (entries == null) {
			return -1;
		}

		int matches = 0;

		// first pass: just count the matches
		for (GridFile f : entries) {

			if (f.getPath() != null) {
				if (f.getPath().startsWith(translated)) {
					matches = matches + 1;
				}
			} else {
				if (translated.endsWith("/")
						|| f.getName().startsWith(
								FileManager.getFilename(translated))) {
					matches = matches + 1;
				}
			}
		}

		// green - executable
		// blue - directory
		// red - compressed
		// cyan - symlink
		for (GridFile f : entries) {
			if (f.getPath() != null) {
				if (f.getPath().startsWith(translated)) {
					String name = f.getName()
							+ (((matches == 1) && f.isFolder()) ? "/" : " ");

					/*
					 * if (entries [i].isDirectory ()) { name = new ANSIBuffer
					 * ().blue (name).toString (); }
					 */
					candidates.add(name);
				}
			} else {
				if (translated.endsWith("/")
						|| f.getName().startsWith(
								FileManager.getFilename(translated))) {
					String name = f.getName()
							+ (((matches == 1) && f.isFolder()) ? "/" : " ");
					candidates.add(name);
				}
			}
		}

		final int index = buffer.lastIndexOf("/");

		return index + 1;
	}

}