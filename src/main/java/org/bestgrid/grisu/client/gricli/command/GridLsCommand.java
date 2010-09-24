package org.bestgrid.grisu.client.gricli.command;

import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.GricliException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.dto.DtoFile;
import org.vpac.grisu.model.dto.DtoFolder;

public class GridLsCommand implements GricliCommand{

    public GricliEnvironment execute(GricliEnvironment env) throws GricliException {
        ServiceInterface si = env.getServiceInterface();
        try {
            String url = "gsiftp://" + env.get("host") + env.get("gdir");
            DtoFolder folder = si.ls(url, 1);
            for (DtoFile file: folder.getChildrenFiles()){
                System.out.println(file.getName());
            }

            for (DtoFolder file: folder.getChildrenFolders()){
                System.out.println(file.getName() + "/");
            }

        } catch (RemoteFileSystemException ex) {
            throw new GricliException(ex);
        }
        return env;
    }

}
