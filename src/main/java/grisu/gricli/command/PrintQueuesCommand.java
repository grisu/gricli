package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.model.info.ApplicationInformation;
import grisu.model.info.dto.DtoProperty;
import grisu.model.info.dto.JobQueueMatch;
import grisu.model.job.JobSubmissionObjectImpl;

import java.util.List;
import java.util.Set;

import org.python.google.common.collect.Sets;

public class PrintQueuesCommand implements GricliCommand {

	// enum PROPERTY {
	// rank("Rank"),
	// site("Site"),
	// queue_name("Queue name"),
	// job_manager("Job manager"),
	// gram_version("Gram version"),
	// // ramsize("Main memory RAM size"),
	// // virtualramsize("Main memory virtual size"),
	// // smp_size("SMP size"),
	// total_jobs("Total jobs"),
	// running_jobs("Running jobs"),
	// waiting_jobs("Waiting jobs"),
	// free_job_slots("Free job slots");
	//
	// String prettyName;
	//
	// private PROPERTY(String prettyName) {
	// this.prettyName = prettyName;
	// }
	// }

	// private static String[] PROPERTY_NAMES = null;

	public static List<JobQueueMatch> calculateAllAvailableQueues(
			GricliEnvironment env)
					throws GricliRuntimeException {


		final String fqan = (String) env.getVariable("group").get();

		final JobSubmissionObjectImpl job = env.getJob();

		final ApplicationInformation ai = env.getGrisuRegistry()
				.getApplicationInformation(job.getApplication());
		final List<JobQueueMatch> grs = ai.getMatches(
				job.getJobSubmissionPropertyMap(),
				fqan);

		return grs;
	}



	// public static String[] getGridResourcePropertyNames() {
	// if (PROPERTY_NAMES == null) {
	// PROPERTY_NAMES = new String[PROPERTY.values().length];
	// for (int i = 0; i < PROPERTY.values().length; i++) {
	// PROPERTY_NAMES[i] = PROPERTY.values()[i].toString();
	// }
	// Arrays.sort(PROPERTY_NAMES);
	// }
	// return PROPERTY_NAMES;
	// }


	// private final String[] propertiesToDisplay;

	@SyntaxDescription(command = { "print", "queues" })
	// @AutoComplete(completors = { GridResourcePropertyCompletor.class })
	public PrintQueuesCommand(String... properties) {
		// if (properties.length == 0) {
		// this.propertiesToDisplay = new String[] { "site" };
		// } else {
		// this.propertiesToDisplay = properties;
		// }
	}

	public void execute(GricliEnvironment env) throws GricliRuntimeException {

		final List<JobQueueMatch> grs = calculateAllAvailableQueues(env);

		if ((grs == null) || (grs.size() == 0)) {
			env.printMessage("No queues available for your currently setup environment. Maybe try to set another group/application?");
			return;
		}

		Set<JobQueueMatch> validQueues = Sets.newTreeSet();
		Set<JobQueueMatch> inValidQueues = Sets.newTreeSet();

		for (JobQueueMatch match : grs) {
			if (match.isValid()) {
				validQueues.add(match);
			} else {
				inValidQueues.add(match);
			}
		}

		env.printMessage("");

		if (validQueues.size() > 0) {
			env.printMessage("Valid queues: ");
			env.printMessage("");
			for (JobQueueMatch q : validQueues) {
				env.printMessage("\t" + q.getQueue().toString() + " ("
						+ q.getQueue().getGateway().getSite().getName()
						+ ")");
			}
			env.printMessage("");
		}
		if (inValidQueues.size() > 0) {
			env.printMessage("Invalid queues: ");
			env.printMessage("");
			for (JobQueueMatch match : inValidQueues) {
				if (!match.isValid()) {
					env.printMessage("\t" + match.getQueue().toString() + " ("
							+ match.getQueue().getGateway().getSite().getName()
							+ ")");
					for (DtoProperty prop : match.getPropertiesDetails().getProperties()) {
						env.printMessage("\t\t" + prop.getKey() + ":\t"
								+ prop.getValue());
					}
				}
			}
			env.printMessage("");
		}

	}

	// private String formatOutput(Set<GridResource> grs)
	// throws GricliRuntimeException {
	//
	// final List<List<String>> grListList = new LinkedList<List<String>>();
	//
	// final List<String> titleList = new LinkedList<String>();
	// grListList.add(titleList);
	//
	// titleList.add("Queue");
	//
	// for (final String property : propertiesToDisplay) {
	// try {
	// titleList.add(PROPERTY.valueOf(property).prettyName);
	// } catch (final IllegalArgumentException e) {
	// throw new GricliRuntimeException(
	// "Property \""
	// + property
	// + "\" not valid. Allowed values: "
	// + StringUtils.join(
	// getGridResourcePropertyNames(), ", "));
	// }
	// }
	//
	// for (final GridResource gr : grs) {
	// final List<String> grList = new LinkedList<String>();
	// grListList.add(grList);
	//
	// final String subLoc = SubmissionLocationHelpers
	// .createSubmissionLocationString(gr);
	// grList.add(subLoc);
	//
	// for (final String property : propertiesToDisplay) {
	// final String value = getGridResourceValue(gr, property);
	// grList.add(value);
	// }
	//
	// }
	//
	// final String output = OutputHelpers.getTable(grListList, true, 0,
	// new Integer[] {});
	//
	// return output;
	//
	// }

}
