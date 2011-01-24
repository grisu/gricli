package org.bestgrid.grisu.client.gricli;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.vpac.grisu.control.ServiceInterface;
import static org.bestgrid.grisu.client.gricli.GricliVar.*;

public class GricliEnvironment {

	private ServiceInterface si;
	private String siUrl;
	private HashMap<String, List<String>> globalLists = new HashMap<String, List<String>>();

	public GricliEnvironment() {
		try {
			WALLTIME.setValue("10");
			JOBNAME.setValue("gricli");
			CPUS.setValue("1");
			JOBTYPE.setValue("single");
			MEMORY.setValue("2048");
			FQAN.setValue("/ARCS");
			GRID_DIR.setValue("/");
			LOCAL_DIR.setValue(System.getProperty("user.dir"));
			PROMPT.setValue("gricli> ");
		} catch (GricliSetValueException ex) {
			// never happens
		}

		globalLists.put("files", new LinkedList<String>());
	}

	public String get(String global) {
		return GricliVar.get(global).getValue();
	}

	public List<String> getList(String globalList) {
		return GricliVar.get(globalList).getList();
	}

	public Set<String> getGlobalNames() {
		return GricliVar.allValues();
	}

	public void put(String global, String value) throws GricliRuntimeException {
		GricliVar v = GricliVar.get(global);
		if (v != null) {
			v.setValue(value);
		} else {
			throw new GricliRuntimeException(global
					+ " global variable does not exist");
		}
	}

	public void add(String globalList, String value)
			throws GricliRuntimeException {
		GricliVar.get(globalList).add(value);

	}

	public ServiceInterface getServiceInterface() throws LoginRequiredException {
		if (si == null) {
			throw new LoginRequiredException();
		}
		return si;
	}

	public void setServiceInterface(ServiceInterface si) {
		this.si = si;
	}

	public String getServiceInterfaceUrl() {
		return siUrl;
	}

	public void setServiceInterfaceUrl(String siUrl) {
		this.siUrl = siUrl;
	}

	public void clear(String list) throws GricliRuntimeException {
		GricliVar.get(list).clear();
	}

}
