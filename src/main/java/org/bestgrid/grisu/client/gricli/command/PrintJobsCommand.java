package org.bestgrid.grisu.client.gricli.command;

import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.GricliException;
import org.bestgrid.grisu.client.gricli.command.GricliCommand;
import org.vpac.grisu.control.ServiceInterface;


public class PrintJobsCommand implements GricliCommand{

    public GricliEnvironment execute(GricliEnvironment env) throws GricliException {
        ServiceInterface si = env.getServiceInterface();
        String[] jobnames = si.getAllJobnames(null).asArray();
        System.out.println("current jobs: ==============");
        for (String jobname:jobnames){
            System.out.println(jobname);
        }
        return env;
    }

}
