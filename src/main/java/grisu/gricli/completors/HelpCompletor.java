package grisu.gricli.completors;

import grisu.gricli.command.help.HelpManager;

import java.util.List;
import java.util.Set;

import jline.Completor;
import jline.SimpleCompletor;

import org.python.google.common.collect.Sets;

import com.google.common.collect.ImmutableSet;

public class HelpCompletor implements Completor {
	private final Set<String> all = Sets.newTreeSet();

	public static final Set<String> other = ImmutableSet.of("topic", "topics",
			"command", "commands", "global", "globals", "search");

	public HelpCompletor() {
		all.addAll(HelpManager.singleton.getCommands());
		all.addAll(HelpManager.singleton.getTopics());
		all.addAll(HelpManager.singleton.getGlobals());
		all.addAll(other);
	}

	public int complete(String arg0, int arg1, List arg2) {

		return new SimpleCompletor(all.toArray(new String[] {})).complete(arg0,
				arg1, arg2);
	}

}
