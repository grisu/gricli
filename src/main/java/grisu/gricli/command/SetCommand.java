package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.VarCompletor;
import grisu.gricli.completors.VarValueCompletor;
import grisu.gricli.environment.GricliEnvironment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetCommand implements GricliCommand {

	static final Logger myLogger = LoggerFactory.getLogger(SetCommand.class
			.getName());

	private final String global;
	private final String[] values;

	@SyntaxDescription(command = { "unset" }, arguments = { "var" })
	@AutoComplete(completors = { VarCompletor.class })
	public SetCommand(String global) {
		this(global, "");
	}

	@SyntaxDescription(command = { "set" }, arguments = { "var", "value" })
	@AutoComplete(completors = { VarCompletor.class, VarValueCompletor.class })
	public SetCommand(String global, String value) {
		this.global = global;
		this.values = new String[] { value };
	}

	public GricliEnvironment execute(final GricliEnvironment env)
			throws GricliRuntimeException {

		env.getVariable(global).set(values);
		return env;

	}
}
