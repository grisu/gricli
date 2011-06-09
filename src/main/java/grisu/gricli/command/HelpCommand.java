package grisu.gricli.command;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

public class HelpCommand implements GricliCommand {
	
	private String[] keywords;
	
	@SyntaxDescription(command={"help"},
			arguments={"keywords"}, help="prints this help message")
	public HelpCommand(String... keywords){
		this.keywords = keywords;
	}
	
	private String generateHelpMessage(SyntaxDescription desc){
		StringBuffer helpMessage = new StringBuffer();
		helpMessage.append(StringUtils.join(desc.command()," "));
		
		for (String arg: desc.arguments()){
			helpMessage.append(" [" + arg +"]");
		}
		helpMessage.append("\n" + desc.help() + "\n\n");
		
		return helpMessage.toString();
	}
	
	private ArrayList<SyntaxDescription> getAllCommands(GricliEnvironment env){
		
		ArrayList<SyntaxDescription> result = new ArrayList<SyntaxDescription>();
		
		List<Class<? extends GricliCommand>> cs = env.getCommandFactory().getCommands();
		for (Class<? extends GricliCommand> c: cs){
			Constructor<? extends GricliCommand>[] conss = (Constructor<? extends GricliCommand>[])c.getDeclaredConstructors();
			for (Constructor<? extends GricliCommand> cons: conss){
				if (cons.isAnnotationPresent(SyntaxDescription.class)){
					result.add(cons.getAnnotation(SyntaxDescription.class));
				}
			}
		}
		Collections.sort(result,new SyntaxComparator());
		return result;
	}
	
	private ArrayList<SyntaxDescription> findCommandList(
			int pos,String keyword, List<SyntaxDescription> commands) {
		ArrayList<SyntaxDescription> result = new ArrayList<SyntaxDescription>();
			for (SyntaxDescription desc : commands) {
				if (desc.command().length > pos && keyword.equals(desc.command()[pos])){
					result.add(desc);
				}
			}
		return result;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		
		List<SyntaxDescription> commands = getAllCommands(env);
		
		for (int i = 0; i< keywords.length; i++){
			commands = findCommandList(i, keywords[i],commands);
		}
		
		if (commands.size() == 0){
			env.printMessage("command \"" + StringUtils.join(keywords," ") + "\" not found");
		} else if (commands.size() == 1){
			env.printMessage(generateHelpMessage(commands.get(0)));
		} else {
			for (SyntaxDescription c: commands){
				String help = StringUtils.join(c.command()," ");
				for (String arg: c.arguments()){
					help += " [" + arg + "]";
				}
				env.printMessage(help);
			}
		}
		
		return env;
	}
	
	class SyntaxComparator implements Comparator<SyntaxDescription>{

		public int compare(SyntaxDescription s1, SyntaxDescription s2) {
			String str1 = StringUtils.join(s1.command()," ");
			String str2 = StringUtils.join(s2.command()," ");
			return str1.compareTo(str2);
		}
		
	}

}
