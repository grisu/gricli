package grisu.gricli.command;

import grisu.control.exceptions.BatchJobException;
import grisu.control.exceptions.NoSuchJobException;
import grisu.frontend.model.job.BatchJobObject;
import grisu.frontend.model.job.JobObject;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddBatchCommand implements GricliCommand {

	private final String name;
	private final String command;

	@SyntaxDescription(command = { "batch", "add" }, arguments = { "name",
			"command" })
	public AddBatchCommand(String name, String command) {
		this.name = name;
		this.command = command;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {

		BatchJobObject obj;
		try {
			obj = new BatchJobObject(env.getServiceInterface(), this.name,
					false);
		} catch (final BatchJobException e) {
			throw new GricliRuntimeException(e);
		} catch (final NoSuchJobException e) {
			throw new GricliRuntimeException(
					"batch job container "
							+ this.name
							+ " does not exist. Use 'create batch [containername]' command");
		}

		final JobObject job = env.getJob();

		/* ${i:filename} */

		final Pattern inputFile = Pattern.compile("\\$\\{i:([^}]+)}");
		final Matcher m = inputFile.matcher(this.command);
		final String path = obj.pathToInputFiles();
		job.setCommandline(m.replaceAll(path + "/$1"));

		obj.addJob(job);

		return env;
	}

}
