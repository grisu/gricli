package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import jline.FileNameCompletor;

import org.apache.commons.lang.StringUtils;

public class ChdirCommand implements GricliCommand {

	private final SetCommand c;

	private String lastUrl = null;

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
