package org.bestgrid.grisu.client.gricli.command;

import java.util.Map;
import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.GricliException;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.model.dto.DtoJob;

public class PrintJobCommand implements GricliCommand{
    private final String jobname;

    public PrintJobCommand(String jobname){
        this.jobname = jobname;
    }

    public GricliEnvironment execute(GricliEnvironment env) throws GricliException {
        ServiceInterface si = env.getServiceInterface();
        try {
            DtoJob job = si.getJob(this.jobname);

            System.out.println("Printing details for job " + jobname);
            System.out.println("status: " + JobConstants.translateStatus(si.getJobStatus(jobname)));
            Map<String,String> props = job.propertiesAsMap();
            for (String key: props.keySet()){
                System.out.println(key + " : " + props.get(key));
            }
            return env;

        } catch (NoSuchJobException ex) {
           throw new GricliException(ex);
        }

    }

}
