package grisu.gricli.command;

import grisu.control.exceptions.BatchJobException;
import grisu.control.exceptions.NoSuchJobException;
import grisu.frontend.model.job.BatchJobObject;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import grisu.gricli.completors.GridFilesystemCompletor;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringUtils;

public class AttachCommand implements GricliCommand {
	private String[] globs;
	private String batchname;
	
	@SyntaxDescription(command={"batch","attach"}, 
	arguments={"batchjob","files"}, help="attach files to batch job")
	public AttachCommand(String batchname, String... globs){
		this.batchname = batchname;
		this.globs = globs;
	}

	@SyntaxDescription(command={"attach"},
			arguments={"files"},
			help="Sets attached file list.Supports multiple arguments and glob regular expressions\n" +
					"example: attach *.txt submit.sh")
	@AutoComplete(completors={GridFilesystemCompletor.class})
	public AttachCommand(String... globs) {
		this(null,globs);
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		
		env.clear("files");
		for (String glob: globs){
			if (glob != null && (glob.startsWith("grid://") || glob.startsWith("gsiftp://"))){
				addFile(glob,env);
			}
			else {
				String[] files = getAllFiles(glob);
				for (String file : files) {
					addFile(file,env);
				}
			}
		}
		return env;
	}
	
	private void addFile(String file,GricliEnvironment env) throws GricliRuntimeException{
		if (this.batchname == null){
			env.add("files",file);
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

	private String[] getAllFiles(String glob) {
		LinkedList<String> all = new LinkedList<String>();
		File dir = null;
		List<String> dirComponents = (List<String>) Arrays.asList(StringUtils
				.split(glob, "/"));
		if (globs[0].startsWith("/")) {
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
