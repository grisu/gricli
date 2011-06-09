package grisu.gricli.completors;

import java.util.List;

import jline.Completor;
import jline.SimpleCompletor;

public class QueueCompletor implements Completor {

	public int complete(String s, int i, List l) {
		return new SimpleCompletor(CompletionCache.queues).complete(s, i, l);
	}

}
