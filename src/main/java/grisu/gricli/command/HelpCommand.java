package grisu.gricli.command;

import java.lang.reflect.Constructor;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

public class HelpCommand implements GricliCommand {
	
	private String helpMessage = "";
	
	@SuppressWarnings("unchecked")
	@SyntaxDescription(command={"help"}, help="prints this help message")
	public HelpCommand(){
		List<Class<? extends GricliCommand>> cs = GricliCommandFactory.commands;
		for (Class<? extends GricliCommand> c: cs){
			Constructor<? extends GricliCommand>[] conss = (Constructor<? extends GricliCommand>[])c.getDeclaredConstructors();
			for (Constructor<? extends GricliCommand> cons: conss){
				if (cons.isAnnotationPresent(SyntaxDescription.class)){
					SyntaxDescription desc = cons.getAnnotation(SyntaxDescription.class);
					helpMessage += StringUtils.join(desc.command()," ");
					for (String arg: desc.arguments()){
						helpMessage += " [" + arg +"]";
					}
					helpMessage += "\n" + desc.help() + "\n\n";
				}
			}
		}
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		env.printMessage(helpMessage);
		return env;
	}

}
