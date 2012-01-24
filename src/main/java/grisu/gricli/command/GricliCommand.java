package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

/*
 * execute command based on environment
 */
public interface GricliCommand {

	public void execute(GricliEnvironment env)
			throws GricliRuntimeException;
}
