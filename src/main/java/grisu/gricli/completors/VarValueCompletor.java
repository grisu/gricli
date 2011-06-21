package grisu.gricli.completors;

import java.util.List;

import jline.Completor;
import jline.MultiCompletor;

public class VarValueCompletor implements Completor {
	
	private MultiCompletor mc;
	
	public VarValueCompletor(){
		mc = new MultiCompletor(new Completor[] {new QueueCompletor(), new FqanCompletor()});
	}

	public int complete(String s, int i, List l) {
		return mc.complete(s, i, l);
	}

}
