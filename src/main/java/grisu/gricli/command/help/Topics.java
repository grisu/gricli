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

	public static String[] TOPICS = new String[] { "Example" };

	public static String getTopicText(String s) {
		InputStream is = null;
		List<String> list = null;

		try {
			is = FileUtils.class.getResourceAsStream("/help/"+s+".md");
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

	public Map<String, String> topicStrings = new TreeMap<String, String>();

	public Topics() {

		for (String TOPIC : TOPICS) {
			String temp = getTopicText(TOPIC);
			topicStrings.put(TOPIC, temp);
		}

	}

	public String getTopic(String topic) {
		return topicStrings.get(topic);
	}

	public Set<String> getTopics() {
		return topicStrings.keySet();
	}

	public Map<String, String> getTopicsWithKeyword(String keyword) {
		Map<String, String> result = new TreeMap<String, String>();
		for (String topic : topicStrings.keySet()) {
			if (topic.toLowerCase().contains(keyword.toLowerCase())
					|| topicStrings.get(topic).toLowerCase()
					.contains(keyword.toLowerCase())) {
				result.put(topic, topicStrings.get(topic));
			}
		}
		return result;
	}

}
