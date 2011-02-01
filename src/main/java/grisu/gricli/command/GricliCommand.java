package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

/*
 * execute command based on environment
 */
public interface GricliCommand {

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException;
}
