package grisu.gricli.command;


import grisu.control.exceptions.BatchJobException;
import grisu.control.exceptions.NoSuchJobException;
import grisu.frontend.model.job.BatchJobObject;
import grisu.frontend.model.job.JobObject;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddBatchCommand implements
GricliCommand {

	private final String name;
	private final String command;

	@SyntaxDescription(command = { "batch", "add" }, arguments = { "name",
	"command" })
	public AddBatchCommand(String name, String command){
		this.name = name;
		this.command = command;
	}

	public GricliEnvironment execute(GricliEnvironment env)
	throws GricliRuntimeException {

		BatchJobObject obj;
		try {
			obj = new BatchJobObject(env.getServiceInterface(),this.name,false);
		} catch (BatchJobException e) {
			throw new GricliRuntimeException(e);
		} catch (NoSuchJobException e) {
			throw new GricliRuntimeException("batch job container " + this.name +
			" does not exist. Use 'create batch [containername]' command");
		}

		JobObject job = env.getJob();

		/* ${i:filename} */

		Pattern inputFile = Pattern.compile("\\$\\{i:([^}]+)}");
		Matcher m = inputFile.matcher(this.command);
		String path = obj.pathToInputFiles();
		job.setCommandline(m.replaceAll(path + "/$1"));

		obj.addJob(job);

		return env;
	}

}
