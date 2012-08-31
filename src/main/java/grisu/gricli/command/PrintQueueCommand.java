package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.QueueCompletorNoAuto;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.utils.MemoryUtils;
import grisu.jcommons.utils.OutputHelpers;
import grisu.jcommons.utils.QueueHelpers;
import grisu.model.info.ApplicationInformation;
import grisu.model.info.dto.DtoProperty;
import grisu.model.info.dto.DynamicInfo;
import grisu.model.info.dto.JobQueueMatch;
import grisu.model.info.dto.Package;
import grisu.model.info.dto.Queue;
import grisu.model.info.dto.Version;
import grisu.model.job.JobSubmissionObjectImpl;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

public class PrintQueueCommand implements GricliCommand {

	public static Map<String, String> constructAvailableApplicationsDetails(
			Queue q) {
		Map<String, String> details = Maps.newLinkedHashMap();

		details.put("Name", "Version");

		if (q.getPackages().size() > 0) {

			for (Package p : q.getPackages()) {
				if (!p.getVersion().getVersion()
						.equals(Version.ANY_VERSION.getVersion())) {
					details.put(p.getApplication().getName(), p.getVersion()
							.getVersion());
				}
			}
		}

		return details;

	}

	public static Map<String, String> constructQueueDetails(Queue q) {

		Map<String, String> details = Maps.newLinkedHashMap();

		details.put("Name", q.getName());
		details.put("Description", q.getDescription());
		int wt = q.getWalltimeInMinutes();
		String wtString = QueueHelpers.prettyWalltime(wt);
		details.put("Max. walltime", wtString);

		details.put("CPUs", Integer.toString(q.getCpus()));
		details.put("Clockspeed (MHz)",
				Double.toString(q.getClockspeedInHz() / 1000000));
		details.put("CPUs per host", Integer.toString(q.getCpusPerHost()));
		details.put("Hosts", Integer.toString(q.getHosts()));
		details.put("Scheduler type", q.getFactoryType());
		details.put("Gateway", q.getGateway().getHost());
		details.put("Memory",
				MemoryUtils.humanReadableByteCount(q.getMemory(), false));
		details.put("Virtual memory",
				MemoryUtils.humanReadableByteCount(q.getVirtualMemory(), false));

		if (q.getDynamicInfo().size() > 0) {
			details.put("Dynamic info", "");
			for (DynamicInfo di : q.getDynamicInfo()) {
				details.put("  " + di.getType(), di.getValue());
			}
		}

		return details;

	}

	private final String queue;

	@SyntaxDescription(command = { "print", "queue" }, arguments = { "queue" })
	@AutoComplete(completors = { QueueCompletorNoAuto.class })
	public PrintQueueCommand(String queue) {
		this.queue = queue;
	}

	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {

		final String fqan = (String) env.getVariable("group").get();

		final JobSubmissionObjectImpl job = env.getJob();

		// UserEnvironmentManager uem =
		// env.getGrisuRegistry().getUserEnvironmentManager();
		final ApplicationInformation ai = env.getGrisuRegistry()
				.getApplicationInformation(job.getApplication());
		// final List<Queue> grs =
		// ai.getQueues(job.getJobSubmissionPropertyMap(),
		// fqan);

		final List<JobQueueMatch> grs = ai.getMatches(
				job.getJobSubmissionPropertyMap(),
				fqan);


		Queue q = null;

		JobQueueMatch match = null;
		for (JobQueueMatch m : grs) {
			if (m.getQueue().toString().equals(this.queue)) {
				match = m;
				break;
			}
		}

		if (match == null) {
			env.printError("Can't find queue: " + this.queue);
			return;
		}

		Map<String, String> details = constructQueueDetails(match.getQueue());

		env.printMessage("");
		env.printMessage("Queue details");
		env.printMessage("");
		env.printMessage(OutputHelpers.getTable(details));

		Map<String, String> apps = constructAvailableApplicationsDetails(match
				.getQueue());
		// env.printMessage("");
		// env.printMessage("");
		env.printMessage("\nPackages installed:");
		env.printMessage("");
		env.printMessage(OutputHelpers.getTable(apps));
		// env.printMessage("");
		env.printMessage("");


		if (match.isValid()) {
			env.printMessage("Queue is valid for current job setup.");
			env.printMessage("");
		} else {
			env.printMessage("Queue is not valid for current job setup:");
			env.printMessage("");
			for (DtoProperty prop : match.getPropertiesDetails()
					.getProperties()) {
				env.printMessage("\t" + prop.getKey() + ":\t"
						+ prop.getValue());
			}
			env.printMessage("");
		}


	}

}
