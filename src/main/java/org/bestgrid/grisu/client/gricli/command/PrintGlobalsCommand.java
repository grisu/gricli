package org.bestgrid.grisu.client.gricli.command;

import java.util.Set;
import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.GricliRuntimeException;

public class PrintGlobalsCommand implements GricliCommand{

    public GricliEnvironment execute(GricliEnvironment env) throws GricliRuntimeException {
        Set<String> globals = env.getGlobalNames();
        for (String global: globals){
            String value = env.get(global);
            value = (value == null) ? "" : value;
            System.out.println(global + " = " + value);
        }
        return env;
    }

}
