package org.bestgrid.grisu.client.gricli.command;

import java.util.Map;
import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.GricliRuntimeException;
import org.bestgrid.grisu.client.gricli.util.ServiceInterfaceUtils;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.model.dto.DtoJob;

public class PrintJobCommand implements GricliCommand{
    private final String jobname;
    private final String attribute;

    public PrintJobCommand(String jobname, String attribute){
        this.jobname = jobname;
        this.attribute = attribute;
    }

    public GricliEnvironment execute(GricliEnvironment env) throws GricliRuntimeException {
        ServiceInterface si = env.getServiceInterface();
        for (String j : ServiceInterfaceUtils.filterJobNames(si, jobname)) {
            try {
                if (attribute != null) {
                    printJobAttribute(si, j, attribute);
                } else {
                    printJob(si, j);
                }
            } catch (NoSuchJobException ex) {
                throw new GricliRuntimeException("job " + j + " does not exist");
            }
        }

        return env;
    }

    private void printJobAttribute(ServiceInterface si, String j, String attribute) throws NoSuchJobException{
        DtoJob job = si.getJob(j);
        if (!("status".equals(attribute))){
            System.out.println(j + " : " + job.jobProperty(attribute));
        } else {
            System.out.println(j + " : " + JobConstants.translateStatus(si.getJobStatus(j)));
        }
    }

    private void printJob(ServiceInterface si, String j) throws NoSuchJobException {
        DtoJob job = si.getJob(j);
        System.out.println("Printing details for job " + jobname);
        System.out.println("status: " + JobConstants.translateStatus(si.getJobStatus(jobname)));
        Map<String, String> props = job.propertiesAsMap();
        for (String key : props.keySet()) {
            System.out.println(key + " : " + props.get(key));
        }
    }

}
