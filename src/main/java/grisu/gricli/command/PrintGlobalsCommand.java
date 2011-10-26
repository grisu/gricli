package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.VarCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.gricli.environment.GricliVar;

import org.apache.commons.io.FilenameUtils;

public class PrintGlobalsCommand implements GricliCommand {

	private final String global;

	@SyntaxDescription(command = { "print", "globals" })
	public PrintGlobalsCommand() {
		this(null);
	}

	@SyntaxDescription(command = { "print", "global" }, arguments = { "varname" })
	@AutoComplete(completors = { VarCompletor.class })
	public PrintGlobalsCommand(String global) {
		this.global = global;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		for (final GricliVar<?> var : env.getVariables()) {
			if (this.global == null
					|| FilenameUtils.wildcardMatch(var.getName(), this.global)) {
				printGlobal(var, env);
			}
		}

		return env;
	}

	private void printGlobal(GricliVar<?> var, GricliEnvironment env) {
		final String name = var.getName();
		if (var.get() != null) {
			env.printMessage(name + " = " + var);
		}
	}

}
