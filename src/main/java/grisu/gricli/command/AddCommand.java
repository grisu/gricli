package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

/**
 * add value to  list
 */
public class AddCommand implements GricliCommand {

	private final String value;
	private final String list;

	@SyntaxDescription(command = { "add" }, arguments = { "list", "value" })
	public AddCommand(String list, String value) {
		this.list = list;
		this.value = value;
	}

	public GricliEnvironment execute(GricliEnvironment env)
	throws GricliRuntimeException {
		env.files.get().add(value);
		return env;
	}

}
