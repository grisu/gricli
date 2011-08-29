package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

public class CompositeCommand implements
GricliCommand {

	private final GricliCommand[] cs;

	public CompositeCommand(GricliCommand[] cs){
		this.cs = cs;
	}

	public GricliEnvironment execute(GricliEnvironment env)
	throws GricliRuntimeException {
		for (GricliCommand c: cs){
			env = c.execute(env);
		}
		return env;
	}

}
