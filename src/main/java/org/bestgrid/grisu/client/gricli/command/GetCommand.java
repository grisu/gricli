package org.bestgrid.grisu.client.gricli.command;

import javax.activation.DataHandler;
import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.GricliRuntimeException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;

public class GetCommand implements GricliCommand {
    private final String file;

    public GetCommand(String file){
        this.file = file;
    }

    public GricliEnvironment execute(GricliEnvironment env) throws GricliRuntimeException {
        String url = "gsiftp://" + env.get("host")  + env.get("gdir") + "/" + file;
        ServiceInterface si = env.getServiceInterface();
        try {
            DataHandler result = si.download(url);

        } catch (RemoteFileSystemException ex) {
            throw new GricliRuntimeException(ex);
        }
        return env;
    }

}
