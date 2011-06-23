package grisu.gricli.command;

import jline.FileNameCompletor;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

public class ChdirCommand implements GricliCommand {
	
	private SetCommand c;

	@SyntaxDescription(command = { "cd" }, arguments={"dir"})
	@AutoComplete(completors={FileNameCompletor.class})
	public ChdirCommand(String dir) {
		c = new SetCommand("dir", dir);
	}
	
	@SyntaxDescription(command = {"cd"})
	public ChdirCommand(){
		this(System.getProperty("user.home"));
	}
	

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		return c.execute(env);
	}

}
