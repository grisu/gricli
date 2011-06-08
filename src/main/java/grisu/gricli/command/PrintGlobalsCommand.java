package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class PrintGlobalsCommand implements GricliCommand {
	
	@SyntaxDescription(command={"print","globals"})
	public PrintGlobalsCommand(){
		super();
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		Set<String> globals = env.getGlobalNames();
		for (String global : globals) {
			String value = env.get(global);
			value = (value == null) ? "" : value;
			env.printMessage(global + " = " + value);
		}
		
		List<String> files = env.getList("files");
		env.printMessage("files = [" + StringUtils.join(files,",") + "]");
		
		return env;
	}

}
