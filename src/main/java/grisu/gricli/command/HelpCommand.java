package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.command.help.HelpManager;

public class HelpCommand implements GricliCommand {

	public static void main(String[] args) throws GricliRuntimeException {

		HelpCommand hc = new HelpCommand("print");

		hc.execute(new GricliEnvironment());


	}

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
			// printAllTopics(env);
			break;
		case 1:
			String keyword = keywords[0];
			String help = HelpManager.singleton.get(keyword);

			env.printMessage(help);
			break;
		default:
			// printCommand(env, keywords);
		}

		return env;

	}

	// private ArrayList<SyntaxDescription> findCommandList(int pos,
	// String keyword, List<SyntaxDescription> commands) {
	// ArrayList<SyntaxDescription> result = new ArrayList<SyntaxDescription>();
	// for (SyntaxDescription desc : commands) {
	// if ((desc.command().length > pos)
	// && keyword.equals(desc.command()[pos])) {
	// result.add(desc);
	// }
	// }
	// return result;
	// }

	// private String generateHelpMessage(SyntaxDescription desc){
	// StringBuffer helpMessage = new StringBuffer("\nCommand:\t");
	// helpMessage.append(StringUtils.join(desc.command(), " "));
	//
	// String description;
	// String helpKey = StringUtils.join(desc.command(), ".");
	// try {
	// description = help.getString(helpKey);
	// } catch (MissingResourceException mre) {
	// description = "No description available.";
	// }
	//
	// if (StringUtils.isBlank(description)) {
	// description = "No description available.";
	// }
	//
	// StringBuffer argsDescription = new StringBuffer();
	//
	// for (String arg: desc.arguments()){
	//
	// helpMessage.append(" [" + arg +"]");
	//
	// String argDescription = null;
	// try {
	// argDescription = help.getString(helpKey + "." + arg);
	// } catch (MissingResourceException e) {
	// // doesn't matter
	// }
	// if (StringUtils.isNotBlank(argDescription)) {
	// argsDescription.append("\t" + arg + ":\t" + argDescription
	// + "\n");
	// }
	// }
	//
	// helpMessage.append("\n\n" + description + "\n");
	//
	// if (argsDescription.length() > 0) {
	// argsDescription.insert(0, "\nArguments:\n");
	// helpMessage.append(argsDescription);
	// }
	//
	// helpMessage.append("\n");
	//
	//
	// String exampleString = null;
	// try {
	// exampleString = examples.getString(helpKey);
	// } catch (MissingResourceException mre) {
	// // doesn't matter
	// }
	//
	// if (StringUtils.isNotBlank(exampleString)) {
	// helpMessage.append("Example:\n\t" + exampleString + "\n");
	// }
	//
	// return helpMessage.toString();
	// }



	// private void printAllCommands(GricliEnvironment env, String keyword) {
	// List<SyntaxDescription> commands = getAllCommands(env);
	//
	// for (int i = 0; i < keywords.length; i++) {
	// commands = findCommandList(i, keywords[i], commands);
	// }
	//
	// if (commands.size() == 0) {
	// env.printMessage("command \"" + StringUtils.join(keywords, " ")
	// + "\" not found");
	// } else if (commands.size() == 1) {
	// env.printMessage(generateHelpMessage(commands.get(0)));
	// } else {
	// for (SyntaxDescription c : commands) {
	// String help = StringUtils.join(c.command(), " ");
	// for (String arg : c.arguments()) {
	// help += " [" + arg + "]";
	// }
	// env.printMessage(help);
	// }
	// }
	//
	// }

	// private void printAllTopics(GricliEnvironment env) {
	//
	// HelpManager t = new HelpManager();
	//
	// for (String topic : t.getTopics() ) {
	// env.printMessage(topic);
	// }
	//
	// }

	// private void printApropos(GricliEnvironment env, String keyword) {
	// // do nothing
	// }

	// private void printCommand(GricliEnvironment env, String[] keywords) {
	//
	// List<SyntaxDescription> commands = getAllCommands(env);
	//
	// for (int i = 0; i < keywords.length; i++) {
	// commands = findCommandList(i, keywords[i], commands);
	// }
	//
	// if (commands.size() == 0) {
	// env.printMessage("command \"" + StringUtils.join(keywords, " ")
	// + "\" not found");
	// } else if (commands.size() == 1) {
	// env.printMessage(generateHelpMessage(commands.get(0)));
	// } else {
	// for (SyntaxDescription c : commands) {
	// String help = StringUtils.join(c.command(), " ");
	// for (String arg : c.arguments()) {
	// help += " [" + arg + "]";
	// }
	// env.printMessage(help);
	// }
	// // }
	// }
	// }

}
