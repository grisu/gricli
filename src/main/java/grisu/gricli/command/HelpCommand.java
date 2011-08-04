package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.command.help.HelpManager;
import grisu.gricli.environment.GricliEnvironment;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

public class HelpCommand implements GricliCommand {

	public static void main(String[] args) throws GricliRuntimeException {

		HelpCommand hc = new HelpCommand("example");

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

		String help = null;

		switch (keywords.length) {
		case 0:
			help = HelpManager.singleton.getCommand("help");
			env.printMessage(help);
			break;
		case 1:
			String keyword = keywords[0];

			if ("commands".equals(keyword) || "command".equals(keyword)) {
				help = HelpManager.singleton.getCommandList();
			} else if ("topics".equals(keyword) || "topic".equals(keyword)) {
				help = HelpManager.singleton.getTopicList();
			} else if ("global".equals(keyword) || "globals".equals(keyword)) {
				help = HelpManager.singleton.getGlobalsList();
			} else if ("all".equals(keyword)) {
				help = "Commands:\n" + HelpManager.singleton.getCommandList();
				help = help + "\nGlobals:\n"
						+ HelpManager.singleton.getGlobalsList();
				help = help + "\nTopics:\n"
						+ HelpManager.singleton.getTopicList();
			} else {
				help = HelpManager.singleton.get(keyword);
			}

			env.printMessage(help);
			break;
		default:
			if ("search".equals(keywords[0])) {
				help = HelpManager.singleton.apropos(keywords[1]);

			} else if ("command".equals(keywords[0])) {
				String cmd = StringUtils.join(Arrays.copyOfRange(keywords, 1, keywords.length), " ");
				help = HelpManager.singleton.getCommand(cmd);
				if (StringUtils.isBlank(help)) {
					help = "Command \"" + cmd + "\" not available.";
				}
			} else if ("topic".equals(keywords[0])
					|| "topics".equals(keywords[0])) {
				help = "";
				for (int i = 1; i < keywords.length; i++) {
					help = help + HelpManager.singleton.getTopic(keywords[i])
							+ "\n\n";
				}
				help = help.trim();
				if (StringUtils.isBlank(help)) {
					help = "No topic found for keyword(s).";
				}
			} else if ("global".equals(keywords[0])
					|| "globals".equals(keywords[0])) {
				help = "";
				for (int i = 1; i < keywords.length; i++) {
					help = help + HelpManager.singleton.getGlobal(keywords[i])
							+ "\n\n";
				}
				help = help.trim();
				if (StringUtils.isBlank(help)) {
					help = "No global found for keyword(s).";
				}
			} else {
				String tmp = StringUtils.join(keywords, " ");
				help = HelpManager.singleton.getCommand(tmp);
				if (StringUtils.isBlank(help)) {
					help = "Command \"" + tmp + "\" not available.";
				}
			}
			env.printMessage(help);
		}

		return env;

	}

}
