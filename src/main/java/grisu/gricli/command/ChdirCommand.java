package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import jline.FileNameCompletor;

public class ChdirCommand implements GricliCommand {

	private final SetCommand c;

	@SyntaxDescription(command = {"cd"})
	public ChdirCommand(){
		this(System.getProperty("user.home"));
	}

	@SyntaxDescription(command = { "cd" }, arguments={"dir"})
	@AutoComplete(completors={FileNameCompletor.class})
	public ChdirCommand(String dir) {
		c = new SetCommand("dir", dir);
	}


	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		return c.execute(env);
	}

}
