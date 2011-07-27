package grisu.gricli.command;

import grisu.control.JobConstants;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.NoSuchJobException;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.JobPropertiesCompletor;
import grisu.gricli.completors.JobnameCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.gricli.util.ServiceInterfaceUtils;
import grisu.model.dto.DtoJob;
import grisu.utils.WalltimeUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class PrintJobCommand implements
GricliCommand {
	private final String jobname;
	private final String attribute;

	@SyntaxDescription(command={"print","jobs"})
	public PrintJobCommand(){
		this("*","status");
	}

	@SyntaxDescription(command={"print","job"},arguments={"jobname"})
	@AutoComplete(completors={JobnameCompletor.class})
	public PrintJobCommand(String jobname){
		this(jobname,null);
	}

	@SyntaxDescription(command={"print","job"},arguments={"jobname","attribute"})
	@AutoComplete(completors={JobnameCompletor.class,JobPropertiesCompletor.class})
	public PrintJobCommand(String jobname, String attribute) {
		this.jobname = jobname;
		this.attribute = attribute;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		ServiceInterface si = env.getServiceInterface();
		for (String j : ServiceInterfaceUtils.filterJobNames(si, jobname)) {
			try {
				if (attribute != null) {
					printJobAttribute(env,si, j, attribute);
				} else {
					printJob(env,si, j);
				}
			} catch (NoSuchJobException ex) {
				throw new GricliRuntimeException("job " + j + " does not exist");
			}
		}

		return env;
	}

	private String formatAttribute(String aName, String aVal){
		if ("submissionTime".equals(aName)){
			Date d = new Date(Long.parseLong(aVal));
			return DateFormat.getInstance().format(d);
		} else if ("memory".equals(aName)){
			double memory = Long.parseLong(aVal);
			memory = memory / 1024.0 / 1024.0 / 1024.0;
			return String.format("%.2f GB", memory);

		} else if ("walltime".equals(aName)) {
			String[] strings = WalltimeUtils
					.convertSecondsInHumanReadableString(Integer.parseInt(aVal) * 60);
			return StringUtils.join(strings, " ");
		} else {
			return aVal;
		}
	}

	private void printJob(GricliEnvironment env, ServiceInterface si, String j)
			throws NoSuchJobException {
		DtoJob job = si.getJob(j);
		env.printMessage("Printing details for job " + jobname);
		env.printMessage("status: "
				+ JobConstants.translateStatus(si.getJobStatus(jobname)));
		Map<String, String> props = job.propertiesAsMap();
		for (String key : props.keySet()) {
			env.printMessage(key + " : " + formatAttribute(key,props.get(key)));
		}
	}

	private void printJobAttribute(GricliEnvironment env, ServiceInterface si, String j,
			String attribute) throws NoSuchJobException {
		DtoJob job = si.getJob(j);
		if (("status".equals(attribute))) {
			env.printMessage(j + " : "
					+ JobConstants.translateStatus(si.getJobStatus(j)));
		} else {
			env.printMessage(j + " : " + formatAttribute(attribute,job.jobProperty(attribute)));
		}
	}

}
