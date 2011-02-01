package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

import javax.activation.DataHandler;

public class GetCommand implements GricliCommand {
	private final String file;

	public GetCommand(String file) {
		this.file = file;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		String url = "gsiftp://" + env.get("host") + env.get("gdir") + "/"
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
