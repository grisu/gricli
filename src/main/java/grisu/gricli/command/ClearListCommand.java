package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

public class ClearListCommand implements
GricliCommand {
	private final String list;

	@SyntaxDescription(command={"clear"},arguments={"list"})
	public ClearListCommand(String list) {
		this.list = list;
	}

	public GricliEnvironment execute(GricliEnvironment env)
	throws GricliRuntimeException {
		env.clear(list);
		return env;
	}

}
