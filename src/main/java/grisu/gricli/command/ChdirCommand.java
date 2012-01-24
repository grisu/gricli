package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import jline.FileNameCompletor;

public class ChdirCommand implements GricliCommand {

	private final SetCommand c;

	private final String lastUrl = null;

	@SyntaxDescription(command = { "cd" })
	public ChdirCommand() {
		this(System.getProperty("user.home"));
	}

	@SyntaxDescription(command = { "cd" }, arguments = { "dir" })
	@AutoComplete(completors = { FileNameCompletor.class })
	public ChdirCommand(String dir) {
		c = new SetCommand("dir", dir);
	}

	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {
		c.execute(env);
	}

}
