package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.QueueCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.interfaces.GridResource;
import grisu.jcommons.utils.SubmissionLocationHelpers;
import grisu.model.info.ApplicationInformation;
import grisu.model.job.JobSubmissionObjectImpl;

import java.util.Formatter;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class PrintQueueCommand implements GricliCommand {

	private final String queue;

	@SyntaxDescription(command = { "print", "queue" }, arguments = { "queue" })
	@AutoComplete(completors = { QueueCompletor.class })
	public PrintQueueCommand(String queue) {
		this.queue = queue;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {

		final String fqan = (String) env.getVariable("group").get();

		final JobSubmissionObjectImpl job = env.getJob();

		// UserEnvironmentManager uem =
		// env.getGrisuRegistry().getUserEnvironmentManager();
		final ApplicationInformation ai = env.getGrisuRegistry()
				.getApplicationInformation(job.getApplication());
		final Set<GridResource> grs = ai
				.getAllSubmissionLocationsAsGridResources(
						job.getJobSubmissionPropertyMap(), fqan);

		GridResource match = null;
		for (final GridResource gr : grs) {
			final String subLoc = SubmissionLocationHelpers
					.createSubmissionLocationString(gr);
			if (StringUtils.equals(subLoc, queue)) {
				match = gr;
				break;
			}
		}

		if (match == null) {
			env.printError("Queue not available for current job setup.");
			return env;
		}

		final String output = formatOutput(match);
		env.printMessage(output);

		return env;
	}

	private String formatOutput(GridResource gr) {

		final StringBuffer result = new StringBuffer("\n");
		final Formatter formatter = new Formatter(result, Locale.US);

		final String subLoc = SubmissionLocationHelpers
				.createSubmissionLocationString(gr);

		result.append("Queue: " + subLoc + "\n\n");

		final int freeJobSlots = gr.getFreeJobSlots();
		final String gramVersion = gr.getGRAMVersion();
		final int mainMemoryRAMSize = gr.getMainMemoryRAMSize();
		final String jobManager = gr.getJobManager();
		final int mainMemoryVirtualSize = gr.getMainMemoryVirtualSize();
		final int rank = gr.getRank();
		final int runningJobs = gr.getRunningJobs();
		final String siteName = gr.getSiteName();
		final String queueName = gr.getQueueName();
		final int smpSize = gr.getSmpSize();
		final int totalJobs = gr.getTotalJobs();
		final int waitingJobs = gr.getWaitingJobs();

		formatter.format("%-25s%s%n", "Site", siteName);
		formatter.format("%-25s%s%n", "Queue name", queueName);
		formatter.format("%-25s%s%n", "Job manager", jobManager);
		formatter.format("%-25s%s%n%n", "GRAM version", gramVersion);
		// formatter
		// .format("%-25s%s%n", "Main memory RAM size", mainMemoryRAMSize);
		// formatter.format("%-25s%s%n%n", "Main memory virtual size",
		// mainMemoryVirtualSize);
		// formatter.format("%-25s%s%n%n", "SMP size", smpSize);
		formatter.format("%-25s%s%n", "Total jobs", totalJobs);
		formatter.format("%-25s%s%n", "Running jobs", runningJobs);
		formatter.format("%-25s%s%n%n", "Waiting jobs", waitingJobs);
		formatter.format("%-25s%s%n%n", "Free job slots", freeJobSlots);
		formatter.format("%-25s%s%n", "Rank", rank);

		return result.toString();
	}

}
