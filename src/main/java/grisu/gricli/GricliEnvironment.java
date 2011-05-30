package grisu.gricli;

import grisu.control.ServiceInterface;
import grisu.frontend.model.job.BatchJobObject;
import grisu.frontend.model.job.JobObject;
import grisu.jcommons.constants.Constants;
import grisu.model.dto.GridFile;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static grisu.gricli.GricliVar.*;

public class GricliEnvironment {

	private ServiceInterface si;
	private String siUrl;
	private HashMap<String, List<String>> globalLists = new HashMap<String, List<String>>();
	

	public GricliEnvironment() {
		try {
			VERSION.setValue(Constants.NO_VERSION_INDICATOR_STRING);
			WALLTIME.setValue("10");
			JOBNAME.setValue("gricli");
			CPUS.setValue("1");
			JOBTYPE.setValue("single");
			MEMORY.setValue("2048");
			FQAN.setValue("/ARCS");
			GRID_DIR.setValue("/");
			LOCAL_DIR.setValue(System.getProperty("user.dir"));
			APPLICATION.setValue(null);
			DEBUG.setValue("false");
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
		try {
			GricliVar.get(list).clear();
		} 
		catch (NullPointerException ex){
			throw new GricliRuntimeException("list " + ((list!=null)?list:"null") + " does not exist");
		}
	}
	
	public void printError(String message){
		System.out.println(message);
	}
	
	public JobObject getJob() throws LoginRequiredException{
		
		ServiceInterface si = getServiceInterface();
		final JobObject job = new JobObject(si);
		job.setJobname(JOBNAME.getValue());
		String app = APPLICATION.getValue();
		if (app == null){
			job.setApplication(Constants.GENERIC_APPLICATION_NAME);
		} 
		else {
			job.setApplication(app);
			job.setApplicationVersion(VERSION.getValue());
		}
		
		job.setCpus(Integer.parseInt(CPUS.getValue()));
		job.setEmail_address(EMAIL.getValue());
		job.setWalltimeInSeconds(Integer.parseInt(WALLTIME.getValue()) * 60
				* job.getCpus());
		job.setMemory(Long.parseLong(MEMORY.getValue()) * 1024 * 1024);
		job.setSubmissionLocation(QUEUE.getValue());

		boolean isMpi = "mpi".equals(JOBTYPE.getValue());
		job.setForce_mpi(isMpi);

		// attach input files
		List<String> files = getList("files");;
		for (String file : files) {
			job.addInputFileUrl(new GridFile(file).getUrl());
		}

		return job;
	}

}
