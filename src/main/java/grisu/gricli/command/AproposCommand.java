package grisu.gricli.command;

import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

public class AproposCommand implements GricliCommand {

	class SyntaxComparator implements Comparator<SyntaxDescription> {

		public int compare(SyntaxDescription s1, SyntaxDescription s2) {
			final String str1 = StringUtils.join(s1.command(), " ");
			final String str2 = StringUtils.join(s2.command(), " ");
			return str1.compareTo(str2);
		}

	}

	private final String keyword;

	@SyntaxDescription(command = { "apropos" }, arguments = { "keyword" })
	public AproposCommand(String keyword) {
		this.keyword = keyword;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {

		final Map<String, String> result = getAllCommands(env);

		for (final String key : result.keySet()) {
			env.printMessage(key + "\t- " + result.get(key));
		}

		return env;
	}

	private Map<String, String> getAllCommands(GricliEnvironment env) {

		final Map<String, String> result = new TreeMap<String, String>();

		final List<Class<? extends GricliCommand>> cs = Gricli.SINGLETON_COMMANDFACTORY
				.getCommands();
		for (final Class<? extends GricliCommand> c : cs) {
			final Constructor<? extends GricliCommand>[] conss = (Constructor<? extends GricliCommand>[]) c
					.getDeclaredConstructors();
			for (final Constructor<? extends GricliCommand> cons : conss) {
				if (cons.isAnnotationPresent(SyntaxDescription.class)) {

					final SyntaxDescription sd = cons
							.getAnnotation(SyntaxDescription.class);
					final String command = StringUtils.join(sd.command(), " ");
					final String arguments = StringUtils.join(sd.arguments(),
							" ");

					final String helpKey = StringUtils.join(sd.command(), ".");
					final String desc = "";
					try {
						// desc = HelpCommand.help.getString(helpKey);
					} catch (final Exception e) {
					}

					if (command.contains(keyword) || desc.contains(keyword)) {
						if (StringUtils.isBlank(desc)) {
							result.put(command, "no description");
						} else {
							result.put(command, desc);
						}
					}
				}
			}
		}
		return result;
	}

}
