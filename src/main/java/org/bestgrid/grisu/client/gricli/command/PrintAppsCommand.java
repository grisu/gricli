package org.bestgrid.grisu.client.gricli.command;

import java.util.List;

import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.GricliRuntimeException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.dto.DtoApplicationInfo;
import org.vpac.grisu.model.dto.DtoVersionInfo;

public class PrintAppsCommand implements GricliCommand {

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		ServiceInterface si = env.getServiceInterface();
		for (String app : si.getAllAvailableApplications(null).asArray()) {
			DtoApplicationInfo info = si
					.getSubmissionLocationsPerVersionOfApplication(app);
			String appName = info.getName();
			List<DtoVersionInfo> versions = info.getAllVersions();

			for (DtoVersionInfo version : versions) {
				String versionTag = version.getName();
				String[] queues = si
						.getSubmissionLocationsForApplicationAndVersion(
								appName, versionTag)
						.asSubmissionLocationStrings();

				for (String queue : queues) {
					System.out
							.println(appName + " " + versionTag + " " + queue);
				}
			}
		}
		return env;
	}

}
