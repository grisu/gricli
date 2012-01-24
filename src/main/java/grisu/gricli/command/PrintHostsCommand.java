package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

import java.util.Map;

public class PrintHostsCommand implements GricliCommand {

	@SyntaxDescription(command = { "print", "hosts" })
	public PrintHostsCommand() {
		super();
	}

	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {
		final ServiceInterface si = env.getServiceInterface();
		final Map<String, String> hostMap = si.getAllHosts().asMap();

		env.printMessage("available hosts: =====");

		for (final String key : hostMap.keySet()) {
			env.printMessage(key + " : " + hostMap.get(key));
		}
	}

}
