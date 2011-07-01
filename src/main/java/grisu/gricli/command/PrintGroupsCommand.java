package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

public class PrintGroupsCommand implements GricliCommand {
	
	
	@SyntaxDescription(command={"print", "groups"})
	public PrintGroupsCommand(){}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		for (String fqan : env.getServiceInterface().getFqans().asSortedSet()){
			env.printMessage(fqan);
		}
		return null;
	}

}
