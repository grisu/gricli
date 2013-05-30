package grisu.gricli.command;

import grisu.frontend.utils.ClientPropertiesHelper;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.utils.OutputHelpers;

import java.util.Map;

public class AboutCommand implements GricliCommand {

	@SyntaxDescription(command = { "about" })
	public AboutCommand() {
	}

	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {

        Map<String, String> temp = ClientPropertiesHelper.gatherClientProperties(env.getServiceInterface());

		String output = OutputHelpers.getTable(temp);

		env.printMessage(output);

	}

}
