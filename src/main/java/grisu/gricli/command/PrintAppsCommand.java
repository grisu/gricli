package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.ApplicationCompletor;
import grisu.gricli.completors.ApplicationVersionCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.utils.OutputHelpers;
import grisu.model.GrisuRegistryManager;
import grisu.model.info.UserApplicationInformation;
import grisu.model.info.dto.Application;
import grisu.model.info.dto.Queue;
import grisu.utils.ServiceInterfaceUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;

public class PrintAppsCommand implements GricliCommand {

	private final String app; // the application package
	private final String version;

	@SyntaxDescription(command = { "print", "packages" })
	public PrintAppsCommand() {
		this(null);
	}

	@SyntaxDescription(command = { "print", "package" }, arguments = { "package" })
	@AutoComplete(completors = { ApplicationCompletor.class })
	public PrintAppsCommand(String app) {
		this(app, "*");
	}

	@SyntaxDescription(command = { "print", "package" }, arguments = {
			"package", "version" })
	@AutoComplete(completors = { ApplicationCompletor.class,
			ApplicationVersionCompletor.class })
	public PrintAppsCommand(String app, String version) {
		this.app = app;
		this.version = version;
	}

	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {

		if (this.app != null) {
			printApplicationsTable(env, app);
		} else {
			printApplications(env);
		}
	}

	private void printApplications(GricliEnvironment env)
			throws GricliRuntimeException {
		final ServiceInterface si = env.getServiceInterface();
		final List<Application> apps = ServiceInterfaceUtils
				.filterApplicationNames(
						si, "*");
		for (final Application app : apps) {
			env.printMessage(app.getName());
		}
	}

	private void printApplicationsTable(GricliEnvironment env, String filter)
			throws GricliRuntimeException {
		final ServiceInterface si = env.getServiceInterface();
		final List<Application> apps = ServiceInterfaceUtils
				.filterApplicationNames(
						si, filter);
		final List<List<String>> table = new LinkedList<List<String>>();

		final List<String> title = new LinkedList<String>();
		title.add("name");
		title.add("version");
		title.add("queue");
		table.add(title);

		for (final Application app : apps) {

			final UserApplicationInformation m = GrisuRegistryManager
					.getDefault(si)
					.getUserApplicationInformation(app.getName());
			final Set<String> versions = m.getAllAvailableVersionsForUser();
			for (final String version : versions) {
				if (!FilenameUtils.wildcardMatch(version, this.version,
						IOCase.INSENSITIVE)) {
					continue;
				}
				final Set<Queue> sublocs = m
						.getAvailableSubmissionLocationsForVersionAndFqan(
								version, env.group.get());

				final List<String> row = new LinkedList<String>();
				row.add(app.getName());
				row.add(version);
				row.add("");
				table.add(row);
				for (final Queue subloc : sublocs) {
					final List<String> queueRow = new LinkedList<String>();
					queueRow.add("");
					queueRow.add("");
					queueRow.add(subloc.toString());
					table.add(queueRow);
				}

			}
		}

		env.printMessage(OutputHelpers.getTable(table));
	}

}
