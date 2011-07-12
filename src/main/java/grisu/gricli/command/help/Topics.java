package grisu.gricli.command.help;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public class Topics {

	public enum Type {
		topics,
		globals,
		commands;
	}

	public static String[] TOPICS = new String[] { "Example" };

	public static String getHelpText(Type type, String s) {
		InputStream is = null;
		List<String> list = null;

		try {
			is = FileUtils.class.getResourceAsStream("/help/" + type.toString()
					+ "/" + s
					+ ".md");
			list = IOUtils.readLines(is);
		}
		catch (Exception e) {
			e.printStackTrace();
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

	public static void main (String[] args) {
		Topics t = new Topics();

		for ( String topic : t.getTopics() ) {
			System.out.println(topic);
			System.out.println("====================================");
			System.out.println(t.getTopic(topic));
			System.out.println("====================================");
			System.out.println("\n\n");

		}

	}

	public Map<String, String> topics = new TreeMap<String, String>();
	public Map<String, String> globals = new TreeMap<String, String>();
	public Map<String, String> commands = new TreeMap<String, String>();

	public Topics() {

		for (String TOPIC : TOPICS) {
			String temp = getHelpText(Type.topics, TOPIC);
			topics.put(TOPIC, temp);
		}
		for (String TOPIC : TOPICS) {
			String temp = getHelpText(Type.topics, TOPIC);
			globals.put(TOPIC, temp);
		}
		for (String TOPIC : TOPICS) {
			String temp = getHelpText(Type.topics, TOPIC);
			commands.put(TOPIC, temp);
		}

	}

	public String getTopic(String topic) {
		return topics.get(topic);
	}

	public Set<String> getTopics() {
		return topics.keySet();
	}

	public Map<String, String> getTopicsWithKeyword(String keyword) {
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

}
