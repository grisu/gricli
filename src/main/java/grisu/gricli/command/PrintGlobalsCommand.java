package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

import java.util.Set;

public class PrintGlobalsCommand implements GricliCommand {
	
	@SyntaxDescription(command={"print","globals"})
	public PrintGlobalsCommand(){
		super();
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		Set<String> globals = env.getGlobalNames();
		for (String global : globals) {
			String value = env.get(global);
			value = (value == null) ? "" : value;
			env.printMessage(global + " = " + value);
		}
		return env;
	}

}
