package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

import java.util.Set;

public class PrintGlobalsCommand implements GricliCommand {

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		Set<String> globals = env.getGlobalNames();
		for (String global : globals) {
			String value = env.get(global);
			value = (value == null) ? "" : value;
			System.out.println(global + " = " + value);
		}
		return env;
	}

}
