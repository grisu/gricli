package org.bestgrid.grisu.client.gricli.command;

import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.GricliException;

/*
 * execute command based on environment
 */
public interface GricliCommand {

    public GricliEnvironment execute (GricliEnvironment env) throws GricliException;
}
