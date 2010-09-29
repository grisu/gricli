package org.bestgrid.grisu.client.gricli.command;

import java.util.Map;
import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.GricliRuntimeException;
import org.vpac.grisu.control.ServiceInterface;

public class PrintHostsCommand implements GricliCommand{

    public GricliEnvironment execute(GricliEnvironment env) throws GricliRuntimeException {
        ServiceInterface si = env.getServiceInterface();
        Map<String,String> hostMap = si.getAllHosts().asMap();

        System.out.println("available hosts: =====");

        for (String key: hostMap.keySet()){
            System.out.println(key + " : " + hostMap.get(key));
        }
        return env;
    }

}
