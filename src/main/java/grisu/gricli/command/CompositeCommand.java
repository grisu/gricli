package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

public class CompositeCommand implements GricliCommand {
	
	private GricliCommand[] cs;

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
