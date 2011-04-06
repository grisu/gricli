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
		Set<String> names  = new HashSet<String>();
		for (GricliVar var: GricliVar.values()){
			names.add(var.name().toLowerCase());
		}
		sc = new SimpleCompletor(names.toArray(new String[] {}));
	}

	public int complete(String s, int i, List l) {
		return sc.complete(s,i,l);
	}

}
