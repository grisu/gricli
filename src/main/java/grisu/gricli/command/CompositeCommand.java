package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

public class CompositeCommand implements GricliCommand {

	private final GricliCommand[] cs;

	public CompositeCommand(GricliCommand[] cs) {
		this.cs = cs;
	}

	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {
		for (final GricliCommand c : cs) {
			c.execute(env);
		}
	}

}
