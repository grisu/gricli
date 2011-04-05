package grisu.gricli.command;

import grisu.control.JobConstants;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.NoSuchJobException;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.util.ServiceInterfaceUtils;
import grisu.gricli.completors.JobnameCompletor;
import grisu.model.dto.DtoJob;

import java.util.Map;

public class PrintJobCommand implements GricliCommand {
	private final String jobname;
	private final String attribute;

	@SyntaxDescription(command={"print","job"})
	@AutoComplete(completors={JobnameCompletor.class})
	public PrintJobCommand(String jobname, String attribute) {
		this.jobname = jobname;
		this.attribute = attribute;
	}
	
	@SyntaxDescription(command={"print","job"})
	@AutoComplete(completors={JobnameCompletor.class})
	public PrintJobCommand(String jobname){
		this(jobname,null);
	}
	
	@SyntaxDescription(command={"print","jobs"})
	public PrintJobCommand(){
		this("*","status");
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		ServiceInterface si = env.getServiceInterface();
		for (String j : ServiceInterfaceUtils.filterJobNames(si, jobname)) {
			try {
				if (attribute != null) {
					printJobAttribute(si, j, attribute);
				} else {
					printJob(si, j);
				}
			} catch (NoSuchJobException ex) {
				throw new GricliRuntimeException("job " + j + " does not exist");
			}
		}

		return env;
	}

	private void printJobAttribute(ServiceInterface si, String j,
			String attribute) throws NoSuchJobException {
		DtoJob job = si.getJob(j);
		if (!("status".equals(attribute))) {
			System.out.println(j + " : " + job.jobProperty(attribute));
		} else {
			System.out.println(j + " : "
					+ JobConstants.translateStatus(si.getJobStatus(j)));
		}
	}

	private void printJob(ServiceInterface si, String j)
			throws NoSuchJobException {
		DtoJob job = si.getJob(j);
		System.out.println("Printing details for job " + jobname);
		System.out.println("status: "
				+ JobConstants.translateStatus(si.getJobStatus(jobname)));
		Map<String, String> props = job.propertiesAsMap();
		for (String key : props.keySet()) {
			System.out.println(key + " : " + props.get(key));
		}
	}

}
