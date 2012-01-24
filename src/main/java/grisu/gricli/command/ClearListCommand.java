package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

public class ClearListCommand implements GricliCommand {
	private final String list;

	@SyntaxDescription(command = { "clear" }, arguments = { "list" })
	public ClearListCommand(String list) {
		this.list = list;
	}

	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {
		env.files.get().clear();

	}

}
