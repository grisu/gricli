package org.bestgrid.grisu.client.gricli.command;

import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.GricliException;

public class SetGlobalCommand implements GricliCommand{
    private final String global;
    private final String value;

    public SetGlobalCommand(String global, String value){
        this.global = global;
        this.value = value;
    }

    public GricliEnvironment execute(GricliEnvironment env) throws GricliException {
        env.put(global, value);
        return env;
    }


}
