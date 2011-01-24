package org.bestgrid.grisu.client.gricli.command;

import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.GricliRuntimeException;

/**
 * 
 * add value to global list
 */
public class AddGlobalCommand implements GricliCommand {
	private final String value;
	private final String list;

	public AddGlobalCommand(String list, String value) {
		this.list = list;
		this.value = value;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		env.add(list, value);
		return env;
	}

}
