package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.VarCompletor;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class PrintGlobalsCommand implements
GricliCommand {

	private String global;

	@SyntaxDescription(command={"print","globals"})
	public PrintGlobalsCommand(){
		this(null);
	}
	
	@SyntaxDescription(command={"print","global"}, arguments={"varname"})
	@AutoComplete(completors={VarCompletor.class})
	public PrintGlobalsCommand(String global){
		this.global = global;
	}

	public GricliEnvironment execute(GricliEnvironment env)
	throws GricliRuntimeException {
		if (this.global == null){
			printAllGlobals(env);
		} else if ("files".equals(this.global)){
			List<String> files = env.getList("files");
			env.printMessage("files = [" + StringUtils.join(files,",") + "]");
		} else {
			printGlobal(this.global,env);
		}
		return env;
	}
	
	private void printAllGlobals(GricliEnvironment env)
		throws  GricliRuntimeException {
		Set<String> globals = env.getGlobalNames();
		for (String global : globals) {
			printGlobal(global,env);
		}

		List<String> files = env.getList("files");
		env.printMessage("files = [" + StringUtils.join(files,",") + "]");
	}
	
	private void printGlobal(String global, GricliEnvironment env) 
		throws GricliRuntimeException {
		if (!env.getGlobalNames().contains(global)){
			throw new GricliRuntimeException("global " + global + " does not exist");
		}
		String value = env.get(global);
		value = (value == null) ? "" : value;
		env.printMessage(global + " = " + value);
	}

}
