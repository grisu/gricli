package grisu.gricli.completors;

import grisu.gricli.Gricli;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import jline.Completor;
import jline.SimpleCompletor;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public class UsersCompletor implements Completor {
	
	private class StripDnFunction<F,T> implements Function<F, T> {
		
		   @Override
		   public Object apply(Object f) {
		      String dn = (String)f;
		      
		      int index = dn.lastIndexOf("CN=");

		      return dn.substring(index+3);
		   }
		
	}

	@SuppressWarnings("unchecked")
	public int complete(String s, int i, List l) {
		
		SortedSet<String> allUsers = Gricli.completionCache.getAllUsers();
		
		Collection<String> cns = Collections2.transform(allUsers, new StripDnFunction<String, String>());
		
		return new SimpleCompletor(cns.toArray(new String[] {})).complete(s, i, l);
	}
}
