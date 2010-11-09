package org.bestgrid.grisu.client.gricli.command;

import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.GricliRuntimeException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.dto.DtoFileObject;

public class GridLsCommand implements GricliCommand {

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		ServiceInterface si = env.getServiceInterface();
		try {
			String url = "gsiftp://" + env.get("host") + env.get("gdir");
			DtoFileObject folder = si.ls(url, 1);
			for (DtoFileObject file : folder.getChildren()) {
				if (file.isFolder()) {
					System.out.println(file.getName());
				} else {
					System.out.println(file.getName() + "/");
				}
			}

		} catch (RemoteFileSystemException ex) {
			throw new GricliRuntimeException(ex);
		}
		return env;
	}

}
