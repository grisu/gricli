package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.GricliVar;

import javax.activation.DataHandler;

@SuppressWarnings("restriction")
public class GetCommand implements GricliCommand {
	private final String file;

	public GetCommand(String file) {
		this.file = file;
	}

	@SyntaxDescription(command={"get"})
	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		String url = "gsiftp://" + GricliVar.HOST.getValue() + GricliVar.GRID_DIR.getValue() + "/"
				+ file;
		ServiceInterface si = env.getServiceInterface();
		try {
			DataHandler result = si.download(url);

		} catch (RemoteFileSystemException ex) {
			throw new GricliRuntimeException(ex);
		}
		return env;
	}

}
