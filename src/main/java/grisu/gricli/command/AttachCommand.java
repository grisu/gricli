package grisu.gricli.command;

import grisu.control.exceptions.BatchJobException;
import grisu.control.exceptions.NoSuchJobException;
import grisu.frontend.model.job.BatchJobObject;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.GridFilesystemCompletor;
import grisu.gricli.environment.GricliEnvironment;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringUtils;

public class AttachCommand implements
GricliCommand {
	private final String[] globs;
	private final String batchname;

	@SyntaxDescription(command={"attach"},
			arguments = { "files" })
	@AutoComplete(completors={GridFilesystemCompletor.class})
	public AttachCommand(String... globs) {
		this(null,globs);
	}

	@SyntaxDescription(command={"batch","attach"},
			arguments={"batchjob","files"}, help="attach files to batch job")
	public AttachCommand(String batchname, String... globs){
		this.batchname = batchname;
		this.globs = globs;
	}

	private void addFile(String file,GricliEnvironment env) throws GricliRuntimeException{
		if (this.batchname == null){
			env.files.get().add(file);
		} else {

			BatchJobObject obj;
			try {
				obj = new BatchJobObject(env.getServiceInterface(),this.batchname,false);
			} catch (BatchJobException e) {
				throw new GricliRuntimeException(e);
			} catch (NoSuchJobException e) {
				throw new GricliRuntimeException("batch job container " + this.batchname +
						" does not exist. Use 'create batch [containername]' command");
			}

			obj.addInputFile(file);
		}
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {

		for (String glob: globs){
			if ((glob != null) && (glob.startsWith("grid://") || glob.startsWith("gsiftp://"))){
				addFile(glob,env);
			}
			else {
				String[] files = getAllFiles(glob);
				if (files.length == 0){
					throw new GricliRuntimeException("no files attached");
				}
				for (String file : files) {
					addFile(file,env);
				}
			}
		}
		return env;
	}

	private String[] getAllFiles(String glob) throws GricliRuntimeException {

		LinkedList<String> all = new LinkedList<String>();
		File dir = new File (glob);
		ArrayList<String> dirComponents =
				new ArrayList<String>(Arrays.asList(StringUtils.split(glob, System.getProperty("file.separator"))));
		if (dir.isAbsolute()) {
			// absolute path
			if (System.getProperty("file.separator").equals("/")){
				//unix
				dir = new File("/");
			} else {
				//windows
				String root = dirComponents.get(0)+"\\\\";
				dir = new File(root);
				dirComponents.remove(0);
			}
		} else if (glob.startsWith("~")){
			//unix home
			dir = new File(System.getProperty("user.home"));
			dirComponents.remove(0);
		}
		else {
			// relative path
			dir = new File(System.getProperty("user.dir"), glob)
			.getParentFile();
			String temp = dirComponents.get(dirComponents.size() - 1);
			dirComponents.clear();
			dirComponents.add(temp);
		}

		if ( ! dir.exists() ) {
			throw new GricliRuntimeException(dir.toString() + " does not exist");
		}

		getSubdirs(dir.getAbsolutePath(), new LinkedList(dirComponents), all);

		return all.toArray(new String[] {});
	}

	private void getSubdirs(String path, LinkedList<String> globs,
			LinkedList<String> result) throws GricliRuntimeException {
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

		if (subComponents.length == 0) {
			throw new GricliRuntimeException("No files found for: "
					+ dir.toString()
					+ File.separator + glob);
		}

		for (File sc : subComponents) {
			getSubdirs(sc.getAbsolutePath(), new LinkedList(globs), result);
		}
		return;
	}

}
