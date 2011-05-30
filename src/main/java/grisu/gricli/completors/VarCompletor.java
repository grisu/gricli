package grisu.gricli.completors;

import grisu.gricli.GricliVar;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jline.Completor;
import jline.SimpleCompletor;

public class VarCompletor implements Completor {
	
	SimpleCompletor sc;
	
	public VarCompletor(){
		sc = new SimpleCompletor(GricliVar.allNames().toArray(new String[] {}));
	}

	@SuppressWarnings("unchecked")
	public int complete(String s, int i, List l) {
		return sc.complete(s,i,l);
	}

}
