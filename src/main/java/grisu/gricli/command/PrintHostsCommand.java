package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

import java.util.Map;

public class PrintHostsCommand implements GricliCommand {

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		ServiceInterface si = env.getServiceInterface();
		Map<String, String> hostMap = si.getAllHosts().asMap();

		System.out.println("available hosts: =====");

		for (String key : hostMap.keySet()) {
			System.out.println(key + " : " + hostMap.get(key));
		}
		return env;
	}

}
