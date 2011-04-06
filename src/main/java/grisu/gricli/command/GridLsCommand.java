package grisu.gricli.command;

import jline.ANSIBuffer;
import jline.ANSIBuffer.ANSICodes;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.FqanCompletor;
import grisu.model.dto.GridFile;


public class GridLsCommand implements GricliCommand {
	
	
	private String path;

	@SyntaxDescription(command={"gls"})
	@AutoComplete(completors={FqanCompletor.class})
	public GridLsCommand(String path){
		this.path = path;
	}
	
	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		ServiceInterface si = env.getServiceInterface();
		try {
			String url = "grid://groups" + this.path;
			GridFile folder = si.ls(url, 1);
			for (GridFile file : folder.getChildren()) {
				String result = null;
				if (file.isFolder()) {
					result = file.getName() + "/";
				} else {
					result = file.getName();
				}
				if (file.isVirtual()){
					result = new ANSIBuffer().red(result).toString();
				}
				System.out.println(result);
			}

		} catch (RemoteFileSystemException ex) {
			throw new GricliRuntimeException(ex);
		}
		return env;
	}

}
