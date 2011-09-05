package grisu.gricli.command.help;

import grisu.gricli.Gricli;
import grisu.gricli.command.GricliCommand;
import grisu.gricli.command.SyntaxDescription;
import grisu.gricli.environment.GricliEnvironment;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

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

	private static Logger myLogger = Logger.getLogger(HelpManager.class
			.getName());

	public static String[] TOPICS = new String[] { "Globals", "Jobs", "Files" };

	public static String getHelpText(Type type, String s) throws Exception {
		InputStream is = null;
		List<String> list = null;

		try {
			is = FileUtils.class.getResourceAsStream("/help/" + type.toString()
					+ "/" + s + ".md");
			list = IOUtils.readLines(is);
		} catch (Exception e) {
			throw new Exception("Can't get get text for " + type.toString()
					+ " " + s + ": " + e.getLocalizedMessage());
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				myLogger.error(e);
			}
		}
		return StringUtils.join(list, "\n");
	}

	public static void main(String[] args) throws Exception {
		HelpManager t = new HelpManager();

		for (String topic : t.getTopics()) {
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

		for (String global : Gricli.completionCache.getEnvironment().getVariableNames()) {
			String temp;
			try {
				temp = getHelpText(Type.globals, global).trim();
				globals.put(global, temp);
			} catch (Exception e) {
				missingGlobals.add(global);
			}
		}

	}

	public String apropos(String keyword) {

		StringBuffer result = new StringBuffer();
		Formatter formatter = new Formatter(result, Locale.US);
		Map<String, String> temp = new TreeMap<String, String>();
		for (String command : commands.keySet()) {
			if (command.toLowerCase().contains(keyword.toLowerCase())
					|| commands.get(command).toLowerCase()
					.contains(keyword.toLowerCase())) {
				temp.put(command, getFirstLine(Type.commands, command));
			}
		}
		int max = 0;
		int max2 = 0;
		if (temp.size() > 0) {
			result.append("Commands:\n");
			for (String c : temp.keySet()) {
				if (c.length() > max) {
					max = c.length();
				}
				if (temp.get(c).length() > max2) {
					max2 = temp.get(c).length();
				}
			}
			for (String c : temp.keySet()) {

				formatter.format("%4s%" + -(max + 4) + "s%" + "s%n", "    ", c,
						temp.get(c));
			}
			result.append("\n");
		}
		temp = new TreeMap<String, String>();
		for (String global : globals.keySet()) {
			if (global.toLowerCase().contains(keyword.toLowerCase())
					|| globals.get(global).toLowerCase()
					.contains(keyword.toLowerCase())) {
				temp.put(global, getFirstLine(Type.globals, global));
			}
		}
		if (temp.size() > 0) {
			result.append("Globals:\n");
			max = 0;
			max2 = 0;
			for (String c : temp.keySet()) {
				if (c.length() > max) {
					max = c.length();
				}
				if (temp.get(c).length() > max2) {
					max2 = temp.get(c).length();
				}
			}
			for (String c : temp.keySet()) {

				formatter.format("%4s%" + -(max + 4) + "s%" + "s%n", "    ", c,
						temp.get(c));
			}
			result.append("\n");
		}
		temp = new TreeMap<String, String>();
		for (String topic : topics.keySet()) {
			if (topic.toLowerCase().contains(keyword.toLowerCase())
					|| topics.get(topic).toLowerCase()
					.contains(keyword.toLowerCase())) {
				temp.put(topic, getFirstLine(Type.topics, topic));
			}
		}
		if (temp.size() > 0) {
			result.append("Topics:\n");
			max = 0;
			max2 = 0;
			for (String c : temp.keySet()) {
				if (c.length() > max) {
					max = c.length();
				}
				if (temp.get(c).length() > max2) {
					max2 = temp.get(c).length();
				}
			}
			for (String c : temp.keySet()) {

				formatter.format("%4s%" + -(max + 4) + "s%" + "s%n", "    ", c,
						temp.get(c));
			}
			result.append("\n");
		}

		if (result.length() == 0) {
			result.append("No help texts found that contain the keyword \""
					+ keyword + "\".");
			return result.toString();
		}

		result.insert(0, "Help texts containing keyword \"" + keyword
				+ "\":\n\n");

		return result.toString();

	}

	public String get(String keyword) {
		String result = getCommand(keyword);
		if (StringUtils.isNotBlank(result)) {

			return result;
		}
		result = getGlobal(keyword);
		if (StringUtils.isNotBlank(result)) {
			return result;
		}
		result = getTopic(keyword);
		if (StringUtils.isNotBlank(result)) {
			return result;
		}
		result = apropos(keyword);
		if (StringUtils.isNotBlank(result)) {
			return result;
		}

		return null;

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
			return result;
		} else {
			return null;
		}
	}

	public String getCommandArguments(String command) {
		return commandArguments.get(command);
	}

	public String getCommandList() {

		int max = 0;
		int max2 = 0;
		for (String c : commands.keySet()) {
			if (c.length() > max) {
				max = c.length();
			}
			if (commands.get(c).length() > max2) {
				max2 = commands.get(c).length();
			}
		}
		StringBuffer result = new StringBuffer();
		Formatter formatter = new Formatter(result, Locale.US);

		for (String c : getCommands()) {
			String helpLine = getFirstLine(Type.commands, c);
			formatter.format("%4s%" + -(max + 4) + "s%" + "s%n", "    ", c,
					helpLine);
		}

		return result.toString();
	}

	public Set<String> getCommands() {
		return commands.keySet();
	}

	public String getFirstLine(Type type, String word) {

		String desc = null;
		switch (type) {

		case commands:

			desc = commands.get(word);
			if (StringUtils.isBlank(desc)) {
				return "n/a";
			}

			for (String line : desc.split("\n")) {
				if (line.toLowerCase().trim().startsWith("command")) {
					continue;
				} else if (StringUtils.isEmpty(line.trim())) {
					continue;
				}
				return line;

			}

			return "n/a";

		case globals:
			desc = globals.get(word);
			if (StringUtils.isBlank(desc)) {
				return "n/a";
			}

			for (String line : desc.split("\n")) {
				if (line.toLowerCase().trim().startsWith("global")
						|| line.toLowerCase().trim().startsWith(word)) {
					continue;
				} else if (line.contains("---") || line.contains("===")) {
					continue;
				} else if (StringUtils.isEmpty(line.trim())) {
					continue;
				}
				return line;
			}
			return "n/a";
		case topics:
			desc = topics.get(word);
			if (StringUtils.isBlank(desc)) {
				return "n/a";
			}

			for (String line : desc.split("\n")) {
				if (line.toLowerCase().trim().startsWith("topic")
						|| line.toLowerCase().trim().startsWith(word)) {
					continue;
				} else if (line.contains("---") || line.contains("===")) {
					continue;
				} else if (StringUtils.isEmpty(line.trim())) {
					continue;
				}
				return line;
			}
			return "n/a";
		default:
			return "n/a";
		}
	}

	public String getGlobal(String global) {
		String result = globals.get(global);
		return result;
	}

	public Set<String> getGlobals() {
		return globals.keySet();
	}

	public String getGlobalsList() {

		int max = 0;
		int max2 = 0;
		for (String c : globals.keySet()) {
			if (c.length() > max) {
				max = c.length();
			}
			if (globals.get(c).length() > max2) {
				max2 = globals.get(c).length();
			}
		}
		StringBuffer result = new StringBuffer();
		Formatter formatter = new Formatter(result, Locale.US);

		for (String c : getGlobals()) {
			String helpLine = getFirstLine(Type.globals, c);
			formatter.format("%4s%" + -(max + 4) + "s%" + "s%n", "    ", c,
					helpLine);
		}

		return result.toString();
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
		String result = topics.get(topic);
		if (result == null) {
			return topics.get(StringUtils.capitalize(topic));
		} else {
			return result;
		}
	}

	public String getTopicList() {

		int max = 0;
		int max2 = 0;
		for (String c : topics.keySet()) {
			if (c.length() > max) {
				max = c.length();
			}
			if (topics.get(c).length() > max2) {
				max2 = topics.get(c).length();
			}
		}
		StringBuffer result = new StringBuffer();
		Formatter formatter = new Formatter(result, Locale.US);

		for (String c : getTopics()) {
			String helpLine = getFirstLine(Type.topics, c);
			formatter.format("%4s%" + -(max + 4) + "s%"
					+ "s%n", "    ", c, helpLine);
		}

		return result.toString();
	}

	public Set<String> getTopics() {
		return topics.keySet();
	}

}
