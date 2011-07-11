package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.command.help.Topics;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

public class HelpCommand implements GricliCommand {

	class SyntaxComparator implements Comparator<SyntaxDescription>{

		public int compare(SyntaxDescription s1, SyntaxDescription s2) {
			String str1 = StringUtils.join(s1.command()," ");
			String str2 = StringUtils.join(s2.command()," ");
			return str1.compareTo(str2);
		}

	}

	public static final ResourceBundle help = ResourceBundle.getBundle("help");
	public static final ResourceBundle examples = ResourceBundle
			.getBundle("examples");

	private final String[] keywords;

	@SyntaxDescription(command={"help"},
			arguments={"keywords"}, help="prints this help message")
	public HelpCommand(String... keywords){
		this.keywords = keywords;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {

		switch (keywords.length) {
		case 0:
			printAllTopics(env);
			break;
		case 1:
			String keyword = keywords[0];
			if ("commands".equals(keyword)) {
				printAllCommands(env, keyword);
			} else {
				printApropos(env, keyword);
			}
			break;
		default:
			printCommand(env, keywords);
		}

		return env;

	}

	private ArrayList<SyntaxDescription> findCommandList(int pos,
			String keyword, List<SyntaxDescription> commands) {
		ArrayList<SyntaxDescription> result = new ArrayList<SyntaxDescription>();
		for (SyntaxDescription desc : commands) {
			if ((desc.command().length > pos)
					&& keyword.equals(desc.command()[pos])) {
				result.add(desc);
			}
		}
		return result;
	}

	private String generateHelpMessage(SyntaxDescription desc){
		StringBuffer helpMessage = new StringBuffer("\nCommand:\t");
		helpMessage.append(StringUtils.join(desc.command(), " "));

		String description;
		String helpKey = StringUtils.join(desc.command(), ".");
		try {
			description = help.getString(helpKey);
		} catch (MissingResourceException mre) {
			description = "No description available.";
		}

		if (StringUtils.isBlank(description)) {
			description = "No description available.";
		}

		StringBuffer argsDescription = new StringBuffer();

		for (String arg: desc.arguments()){

			helpMessage.append(" [" + arg +"]");

			String argDescription = null;
			try {
				argDescription = help.getString(helpKey + "." + arg);
			} catch (MissingResourceException e) {
				// doesn't matter
			}
			if (StringUtils.isNotBlank(argDescription)) {
				argsDescription.append("\t" + arg + ":\t" + argDescription
						+ "\n");
			}
		}

		helpMessage.append("\n\n" + description + "\n");

		if (argsDescription.length() > 0) {
			argsDescription.insert(0, "\nArguments:\n");
			helpMessage.append(argsDescription);
		}

		helpMessage.append("\n");


		String exampleString = null;
		try {
			exampleString = examples.getString(helpKey);
		} catch (MissingResourceException mre) {
			// doesn't matter
		}

		if (StringUtils.isNotBlank(exampleString)) {
			helpMessage.append("Example:\n\t" + exampleString + "\n");
		}

		return helpMessage.toString();
	}

	private ArrayList<SyntaxDescription> getAllCommands(GricliEnvironment env) {

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

	private void printAllCommands(GricliEnvironment env, String keyword) {
		List<SyntaxDescription> commands = getAllCommands(env);

		for (int i = 0; i < keywords.length; i++) {
			commands = findCommandList(i, keywords[i], commands);
		}

		if (commands.size() == 0) {
			env.printMessage("command \"" + StringUtils.join(keywords, " ")
					+ "\" not found");
		} else if (commands.size() == 1) {
			env.printMessage(generateHelpMessage(commands.get(0)));
		} else {
			for (SyntaxDescription c : commands) {
				String help = StringUtils.join(c.command(), " ");
				for (String arg : c.arguments()) {
					help += " [" + arg + "]";
				}
				env.printMessage(help);
			}
		}

	}

	private void printAllTopics(GricliEnvironment env) {

		Topics t = new Topics();

		for (String topic : t.getTopics() ) {
			env.printMessage(topic);
		}

	}

	private void printApropos(GricliEnvironment env, String keyword) {
		// do nothing
	}

	private void printCommand(GricliEnvironment env, String[] keywords) {

		List<SyntaxDescription> commands = getAllCommands(env);

		for (int i = 0; i < keywords.length; i++) {
			commands = findCommandList(i, keywords[i], commands);
		}

		if (commands.size() == 0) {
			env.printMessage("command \"" + StringUtils.join(keywords, " ")
					+ "\" not found");
		} else if (commands.size() == 1) {
			env.printMessage(generateHelpMessage(commands.get(0)));
		} else {
			for (SyntaxDescription c : commands) {
				String help = StringUtils.join(c.command(), " ");
				for (String arg : c.arguments()) {
					help += " [" + arg + "]";
				}
				env.printMessage(help);
			}
			// }
		}
	}

}
