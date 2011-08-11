package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.GridResourcePropertyCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.gricli.util.OutputHelpers;
import grisu.jcommons.interfaces.GridResource;
import grisu.jcommons.utils.SubmissionLocationHelpers;
import grisu.model.info.ApplicationInformation;
import grisu.model.job.JobSubmissionObjectImpl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class PrintQueuesCommand implements GricliCommand {

	enum PROPERTY {
		rank("Rank"),
		site("Site"),
		queue_name("Queue name"),
		job_manager("Job manager"),
		gram_version("Gram version"),
		ramsize("Main memory RAM size"),
		virtualramsize("Main memory virtual size"),
		smp_size("SMP size"),
		total_jobs("Total jobs"),
		running_jobs("Running jobs"),
		waiting_jobs("Waiting jobs"),
		free_job_slots("Free job slots");

		String prettyName;

		private PROPERTY(String prettyName) {
			this.prettyName = prettyName;
		}
	}

	private static String[] PROPERTY_NAMES = null;

	public static String[] getGridResourcePropertyNames() {
		if ( PROPERTY_NAMES == null ) {
			PROPERTY_NAMES = new String[PROPERTY.values().length];
			for ( int i=0; i<PROPERTY.values().length; i++ ) {
				PROPERTY_NAMES[i] = PROPERTY.values()[i].toString();
			}
			Arrays.sort(PROPERTY_NAMES);
		}
		return PROPERTY_NAMES;
	}

	private static String getGridResourceValue(GridResource gr, String property)
			throws GricliRuntimeException {

		try {
			PROPERTY p = PROPERTY.valueOf(property);

			switch (p) {
			case rank:
				return new Integer(gr.getRank()).toString();
			case site:
				return gr.getSiteName();
			case queue_name:
				return gr.getQueueName();
			case job_manager:
				return gr.getJobManager();
			case gram_version:
				return gr.getGRAMVersion();
			case ramsize:
				return new Integer(gr.getMainMemoryRAMSize()).toString();
			case virtualramsize:
				return new Integer(gr.getMainMemoryVirtualSize()).toString();
			case smp_size:
				return new Integer(gr.getSmpSize()).toString();
			case total_jobs:
				return new Integer(gr.getTotalJobs()).toString();
			case running_jobs:
				return new Integer(gr.getRunningJobs()).toString();
			case waiting_jobs:
				return new Integer(gr.getWaitingJobs()).toString();
			case free_job_slots:
				return new Integer(gr.getFreeJobSlots()).toString();
			}

			return p.prettyName;
		} catch (IllegalArgumentException e) {
			throw new GricliRuntimeException("Property \"" + property
					+ "\" not valid. Allowed values: "
					+ StringUtils.join(getGridResourcePropertyNames(), ", "));
		}


	}


	private final String[] propertiesToDisplay;

	// @SyntaxDescription(command={"print","queues"})
	// public PrintQueuesCommand(){
	// this(new String[] { "rank" });
	// }

	@SyntaxDescription(command = { "print", "queues" }, arguments = { "properties" })
	@AutoComplete(completors = { GridResourcePropertyCompletor.class })
	public PrintQueuesCommand(String... properties) {
		if (properties.length == 0) {
			this.propertiesToDisplay = new String[] { "rank" };
		} else {
			this.propertiesToDisplay = properties;
		}
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {

		String fqan = (String) env.getVariable("group").get();

		JobSubmissionObjectImpl job = env.getJob();

		// UserEnvironmentManager uem =
		// env.getGrisuRegistry().getUserEnvironmentManager();
		ApplicationInformation ai = env.getGrisuRegistry()
				.getApplicationInformation(job.getApplication());
		Set<GridResource> grs = ai.getAllSubmissionLocationsAsGridResources(
				job.getJobSubmissionPropertyMap(), fqan);

		if (grs.size() == 0) {
			env.printMessage("No queues available for your currently setup environment. Maybe try to set another group/application?");
			return env;
		}

		String output = formatOutput(grs);
		env.printMessage("\n" + output);

		return env;
	}

	private String formatOutput(Set<GridResource> grs)
			throws GricliRuntimeException {

		List<List<String>> grListList = new LinkedList<List<String>>();

		List<String> titleList = new LinkedList<String>();
		grListList.add(titleList);

		titleList.add("Queue");

		for (String property : propertiesToDisplay) {
			try {
				titleList.add(PROPERTY.valueOf(property).prettyName);
			} catch (IllegalArgumentException e) {
				throw new GricliRuntimeException(
						"Property \""
								+ property
								+ "\" not valid. Allowed values: "
								+ StringUtils.join(
										getGridResourcePropertyNames(), ", "));
			}
		}

		for (GridResource gr : grs) {
			List<String> grList = new LinkedList<String>();
			grListList.add(grList);

			String subLoc = SubmissionLocationHelpers
					.createSubmissionLocationString(gr);
			grList.add(subLoc);

			for (String property : propertiesToDisplay) {
				String value = getGridResourceValue(gr, property);
				grList.add(value);
			}

		}

		String output = OutputHelpers.getTable(grListList, true, 0,
				new Integer[] {});

		return output;

		// StringBuffer result = new StringBuffer("\n");
		// Formatter formatter = new Formatter(result, Locale.US);
		//
		// int maxSubLoc = 0;
		// for (GridResource gr : grs) {
		// String subLoc = SubmissionLocationHelpers
		// .createSubmissionLocationString(gr);
		// if (subLoc.length() > maxSubLoc) {
		// maxSubLoc = subLoc.length();
		// }
		// }
		// formatter.format("%" + -(maxSubLoc + 8) + "s%s%n", "Queue", "Rank");
		// for (GridResource gr : grs) {
		// String subLoc = SubmissionLocationHelpers
		// .createSubmissionLocationString(gr);
		// formatter.format("%4s%" + -(maxSubLoc + 4) + "s%" + "s%n",
		// "    ",
		// subLoc, gr.getRank());
		// }
		// result.append("\n");
		//
		// return result.toString();
	}

}
