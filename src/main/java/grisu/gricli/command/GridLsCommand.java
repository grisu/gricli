package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.model.dto.GridFile;


public class GridLsCommand implements GricliCommand {
	
	
	@SyntaxDescription(command={"gls"})
	public GridLsCommand(){
		super();
	}
	
	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		ServiceInterface si = env.getServiceInterface();
		try {
			String url = "gsiftp://" + env.get("host") + env.get("gdir");
			GridFile folder = si.ls(url, 1);
			for (GridFile file : folder.getChildren()) {
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
