package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

/**
 * add value to  list
 */
public class AddCommand implements GricliCommand {

	private String file,value,var = null;

	@SyntaxDescription(command = { "add","files" }, arguments = {"value" })
	public AddCommand(String file) {
		this.file = file;
	}
	
	@SyntaxDescription(command={"add","environment"}, arguments={"var","value"})
	public AddCommand(String var, String value){
		this.var = var;
		this.value = value;
	}

	public GricliEnvironment execute(GricliEnvironment env)
	throws GricliRuntimeException {
		
		if (file != null){
			env.files.get().add(file);
		} else {
			env.env.get().put(var, value);
		}
		return env;
	}

}
