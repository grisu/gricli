package org.bestgrid.grisu.client.gricli.command;

import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.GricliException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.BatchJobException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;

public class KillJobCommand implements GricliCommand{
    private final String jobname;
    private final boolean clean;
    
    public KillJobCommand(String jobname, boolean clean){
        this.jobname = jobname;
        this.clean = clean;
    }

    public GricliEnvironment execute(GricliEnvironment env) throws GricliException {
        ServiceInterface si = env.getServiceInterface();
        try {
            si.kill(jobname, clean);
        } catch (RemoteFileSystemException ex) {
            throw new GricliException(ex);
        } catch (NoSuchJobException ex) {
            throw new GricliException(ex);
        } catch (BatchJobException ex) {
            throw new GricliException(ex);
        }
        return env;
    }


}
