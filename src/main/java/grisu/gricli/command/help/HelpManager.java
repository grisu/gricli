package grisu.gricli.command.help;

import grisu.gricli.Gricli;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.command.GricliCommand;
import grisu.gricli.command.SyntaxDescription;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public class HelpManager {

	class SyntaxComparator implements Comparator<SyntaxDescription> {

		public int compare(SyntaxDescription s1, SyntaxDescription s2) {
			String str1 = StringUtils.join(s1.command(), " ");
			String str2 = StringUtils.join(s2.command(), " ");
			return str1.compareTo(str2);
		}

	}

	public enum Type {
		topics,
		globals,
		commands;
	}

	public static String[] TOPICS = new String[] { "Example" };

	public static String getHelpText(Type type, String s) throws Exception {
		InputStream is = null;
		List<String> list = null;

		try {
			is = FileUtils.class.getResourceAsStream("/help/" + type.toString()
					+ "/" + s
					+ ".md");
			list = IOUtils.readLines(is);
		}
		catch (Exception e) {
			throw new Exception("Can't get get text for " + type.toString()
					+ " " + s + ": " + e.getLocalizedMessage());
		}
		finally {
			try {
				if (is != null) {
					is.close();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return StringUtils.join(list, "\n");
	}

	public static void main(String[] args) throws Exception {
		HelpManager t = new HelpManager();

		for ( String topic : t.getTopics() ) {
			System.out.println(topic);
			System.out.println("====================================");
			System.out.println(t.getTopic(topic));
			System.out.println("====================================");
			System.out.println("\n\n");
		}

		for (String command : t.getCommands()) {
			System.out.println("Command:\t" + command + " "
					+ t.getCommandArguments(command));

		}

		// File root = new File("/home/markus/Desktop/help");
		// File topics = new File(root, "topics");
		// topics.mkdirs();
		// File globals = new File(root, "globals");
		// globals.mkdirs();
		// File commands = new File(root, "commands");
		// commands.mkdirs();
		//
		//
		// for ( String topic : t.getMissingTopics() ) {
		// List<String> dummy = new ArrayList<String>();
		// dummy.add(topic);
		// dummy.add("=========================");
		// dummy.add("");
		// dummy.add("dummy for topic "+topic);
		// FileUtils.writeLines(new File(topics, topic + ".md"), dummy);
		// }
		//
		// for (String topic : t.getMissingCommands()) {
		// List<String> dummy = new ArrayList<String>();
		// // dummy.add(topic);
		// // dummy.add("=========================");
		// dummy.add("dummy for command " + topic);
		// FileUtils.writeLines(new File(commands, topic + ".md"), dummy);
		// }
		//
		// for (String topic : t.getMissingGlobals()) {
		// List<String> dummy = new ArrayList<String>();
		// dummy.add(topic);
		// dummy.add("=========================");
		// dummy.add("");
		// dummy.add("dummy for global " + topic);
		// FileUtils.writeLines(new File(globals, topic + ".md"), dummy);
		// }

	}

	public Map<String, String> topics = new TreeMap<String, String>();
	public Map<String, String> globals = new TreeMap<String, String>();
	public Map<String, String> commands = new TreeMap<String, String>();
	public Map<String, String> commandArguments = new TreeMap<String, String>();

	public Set<String> missingTopics = new TreeSet<String>();

	public Set<String> missingGlobals = new TreeSet<String>();

	public Set<String> missingCommands = new TreeSet<String>();

	public static final HelpManager singleton = new HelpManager();

	public HelpManager() {

		for (String topic : TOPICS) {
			String temp;
			try {
				temp = getHelpText(Type.topics, topic).trim();
				topics.put(topic, temp);
			} catch (Exception e) {
				missingTopics.add(topic);
			}
		}
		for (SyntaxDescription command : getAllCommands()) {
			String cmd = StringUtils.join(command.command(), " ");
			String temp;
			try {
				temp = getHelpText(Type.commands, cmd).trim();
				commands.put(cmd, temp);
				StringBuffer args = new StringBuffer();
				for (String arg : command.arguments()) {
					args.append("[" + arg + "] ");
				}
				commandArguments.put(cmd, args.toString().trim());
			} catch (Exception e) {
				missingCommands.add(cmd);
			}
		}
		for (String global : GricliEnvironment.getVariables()) {
			String temp;
			try {
				temp = getHelpText(Type.globals, global).trim();
				commands.put(global, temp);
			} catch (Exception e) {
				missingGlobals.add(global);
			}
		}

	}
	public String get(String keyword) {
		String result = getCommand(keyword);
		if ( StringUtils.isNotBlank(result)) {

			return result;
		}
		result = getGlobal(keyword);
		if (StringUtils.isNotBlank(result)) {
			return result;
		}
		return getTopic(keyword);
	}

	private ArrayList<SyntaxDescription> getAllCommands() {

		ArrayList<SyntaxDescription> result = new ArrayList<SyntaxDescription>();

		List<Class<? extends GricliCommand>> cs = Gricli.SINGLETON_COMMANDFACTORY
				.getCommands();
		for (Class<? extends GricliCommand> c : cs) {
			Constructor<? extends GricliCommand>[] conss = (Constructor<? extends GricliCommand>[]) c
					.getDeclaredConstructors();
			for (Constructor<? extends GricliCommand> cons : conss) {
				if (cons.isAnnotationPresent(SyntaxDescription.class)) {
					result.add(cons.getAnnotation(SyntaxDescription.class));
				}
			}
		}
		Collections.sort(result, new SyntaxComparator());
		return result;
	}

	public String getCommand(String command) {
		String result = commands.get(command);
		if (StringUtils.isNotBlank(result)) {
			return "Command:\t" + command + " " + commandArguments.get(command)
					+ "\n\n" + result;
		} else {
			return null;
		}
	}

	public String getCommandArguments(String command) {
		return commandArguments.get(command);
	}

	public Set<String> getCommands() {
		return commands.keySet();
	}

	public String getGlobal(String global) {
		return globals.get(global);
	}

	public Set<String> getGlobals() {
		return globals.keySet();
	}

	public Map<String, String> getHelpTextsContainingKeyword(String keyword) {
		Map<String, String> result = new TreeMap<String, String>();
		for (String topic : topics.keySet()) {
			if (topic.toLowerCase().contains(keyword.toLowerCase())
					|| topics.get(topic).toLowerCase()
					.contains(keyword.toLowerCase())) {
				result.put(topic, topics.get(topic));
			}
		}
		return result;
	}

	public Set<String> getMissingCommands() {
		return missingCommands;
	}

	public Set<String> getMissingGlobals() {
		return missingGlobals;
	}

	public Set<String> getMissingTopics() {
		return missingTopics;
	}

	public String getTopic(String topic) {
		return topics.get(topic);
	}

	public Set<String> getTopics() {
		return topics.keySet();
	}

}
