package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.command.help.HelpManager;

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
			help = HelpManager.singleton.get(keyword);

			env.printMessage(help);
			break;
		default:
			if ("search".equals(keywords[0])) {
				help = HelpManager.singleton.get(keywords[1]);

				env.printMessage(help);
			} else {
				help = HelpManager.singleton.getCommand(StringUtils.join(
						keywords, " "));
			}
		}

		return env;

	}

}
