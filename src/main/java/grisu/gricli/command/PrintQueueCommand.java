package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.QueueCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.model.info.ApplicationInformation;
import grisu.model.job.JobSubmissionObjectImpl;

import java.util.Set;

public class PrintQueueCommand implements GricliCommand {

	private final String queue;

	@SyntaxDescription(command = { "print", "queue" }, arguments = { "queue" })
	@AutoComplete(completors = { QueueCompletor.class })
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
		final Set<String> grs = ai.getQueues(job.getJobSubmissionPropertyMap(),
				fqan);


		if ((grs == null) || !grs.contains(queue)) {
			env.printError("Queue not available for current job setup.");
		}

		// final String output = formatOutput(match);
		// TODO get more information about queue
		env.printMessage("Queue is valid for current job setup.");

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
