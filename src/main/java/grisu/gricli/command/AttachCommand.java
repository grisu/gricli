package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import jline.FileNameCompletor;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringUtils;

public class AttachCommand implements GricliCommand {
	private String glob;
	
	static {
		GricliCommandFactory.commands.add(AttachCommand.class);
	}

	@SyntaxDescription(command={"attach"}, 
			help="Sets attached file list. Equivalent to 'add global files [file]' " +
					"but with support of glob regular expressions\n" +
					"example: attach *.txt ")
	@AutoComplete(completors={FileNameCompletor.class})
	public AttachCommand(String glob) {
		this.glob = glob;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		String[] files = getAllFiles();
		for (String file : files) {
			env.add("files", file);
		}
		return env;
	}

	private void getSubdirs(String path, LinkedList<String> globs,
			LinkedList<String> result) {

		if (globs.size() == 0) {
			result.add(path);
			return;
		}
		File dir = new File(path);
		String glob = globs.removeFirst();
		FileFilter filter = new WildcardFileFilter(glob);
		File[] subComponents = dir.listFiles(filter);
		if (subComponents == null) {
			return;
		}
		for (File sc : subComponents) {
			getSubdirs(sc.getAbsolutePath(), new LinkedList(globs), result);
		}
		return;
	}

	private String[] getAllFiles() {
		LinkedList<String> all = new LinkedList<String>();
		File dir = null;
		List<String> dirComponents = (List<String>) Arrays.asList(StringUtils
				.split(glob, "/"));
		if (glob.startsWith("/")) {
			// absolute path
			dir = new File("/");
		} else {
			// relative path
			dir = new File(System.getProperty("user.dir"));
		}

		getSubdirs(dir.getAbsolutePath(), new LinkedList(dirComponents), all);

		return all.toArray(new String[] {});
	}

}
