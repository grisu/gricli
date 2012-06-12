package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.QueueCompletorNoAuto;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.utils.MemoryUtils;
import grisu.jcommons.utils.OutputHelpers;
import grisu.jcommons.utils.QueueHelpers;
import grisu.model.info.ApplicationInformation;
import grisu.model.info.dto.DynamicInfo;
import grisu.model.info.dto.Package;
import grisu.model.info.dto.Queue;
import grisu.model.info.dto.Version;
import grisu.model.job.JobSubmissionObjectImpl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import com.google.common.base.Functions;
import com.google.common.collect.Collections2;

public class PrintQueueCommand implements GricliCommand {

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
		final List<Queue> grs = ai.getQueues(job.getJobSubmissionPropertyMap(),
				fqan);

		Queue q = null;
		if ((grs == null)) {
			env.printError("Queue not available for current job setup.");
			return;
		} else {

			Collection<String> subLocs = Collections2.transform(grs,
					Functions.toStringFunction());

			for (Queue qu : grs) {
				if (qu.toString().equals(queue)) {
					q = qu;
					break;
				}
			}

			if (q == null) {
				env.printError("Queue not available for current job setup.");
				return;
			}

		}

		env.printMessage("");
		env.printMessage("Queue is valid for current job setup.");
		env.printMessage("");
		env.printMessage("Queue details");
		env.printMessage("");

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

		String output = OutputHelpers.getTable(details);

		details = Maps.newLinkedHashMap();

		if (q.getPackages().size() > 0) {
			output = output + "\nPackages installed:\n\n";

			details.put("Name", "Version");
			for ( Package p : q.getPackages() ) {
				if (!p.getVersion().getVersion()
						.equals(Version.ANY_VERSION.getVersion())) {
					details.put(p.getApplication().getName(), p.getVersion()
							.getVersion());
				}
			}
		}

		output = output + OutputHelpers.getTable(details);
		env.printMessage(output);


	}

	// private String formatOutput(GridResource gr) {
	//
	// final StringBuffer result = new StringBuffer("\n");
	// final Formatter formatter = new Formatter(result, Locale.US);
	//
	// final String subLoc = SubmissionLocationHelpers
	// .createSubmissionLocationString(gr);
	//
	// result.append("Queue: " + subLoc + "\n\n");
	//
	// final int freeJobSlots = gr.getFreeJobSlots();
	// final String gramVersion = gr.getGRAMVersion();
	// final int mainMemoryRAMSize = gr.getMainMemoryRAMSize();
	// final String jobManager = gr.getJobManager();
	// final int mainMemoryVirtualSize = gr.getMainMemoryVirtualSize();
	// final int rank = gr.getRank();
	// final int runningJobs = gr.getRunningJobs();
	// final String siteName = gr.getSiteName();
	// final String queueName = gr.getQueueName();
	// final int smpSize = gr.getSmpSize();
	// final int totalJobs = gr.getTotalJobs();
	// final int waitingJobs = gr.getWaitingJobs();
	//
	// formatter.format("%-25s%s%n", "Site", siteName);
	// formatter.format("%-25s%s%n", "Queue name", queueName);
	// formatter.format("%-25s%s%n", "Job manager", jobManager);
	// formatter.format("%-25s%s%n%n", "GRAM version", gramVersion);
	// // formatter
	// // .format("%-25s%s%n", "Main memory RAM size", mainMemoryRAMSize);
	// // formatter.format("%-25s%s%n%n", "Main memory virtual size",
	// // mainMemoryVirtualSize);
	// // formatter.format("%-25s%s%n%n", "SMP size", smpSize);
	// formatter.format("%-25s%s%n", "Total jobs", totalJobs);
	// formatter.format("%-25s%s%n", "Running jobs", runningJobs);
	// formatter.format("%-25s%s%n%n", "Waiting jobs", waitingJobs);
	// formatter.format("%-25s%s%n%n", "Free job slots", freeJobSlots);
	// formatter.format("%-25s%s%n", "Rank", rank);
	//
	// return result.toString();
	// }

}
