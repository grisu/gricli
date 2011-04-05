package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

/**
 * 
 * add value to global list
 */
public class AddGlobalCommand implements GricliCommand {
		
	private final String value;
	private final String list;

	@SyntaxDescription(command={"add","global"})
	public AddGlobalCommand(String list, String value) {
		this.list = list;
		this.value = value;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		env.add(list, value);
		return env;
	}

}
