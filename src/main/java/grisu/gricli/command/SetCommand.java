package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.VarValueCompletor;
import grisu.gricli.completors.VarCompletor;

public class SetCommand implements GricliCommand {

	private final String global;
	private final String value;

	@SyntaxDescription(command={"set"},arguments={"var","value"})
	@AutoComplete(completors={VarCompletor.class, VarValueCompletor.class})
	public SetCommand(String global, String value) {
		this.global = global;
		this.value = value;
	}

	public GricliEnvironment execute(GricliEnvironment env)
	throws GricliRuntimeException {
		env.put(global, value);
		return env;
	}

}
