package org.bestgrid.grisu.client.gricli.command;

import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.GricliRuntimeException;

public class ClearListCommand implements GricliCommand {
	private final String list;

	public ClearListCommand(String list) {
		this.list = list;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		env.clear(list);
		return env;
	}

}
