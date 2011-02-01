package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.model.dto.DtoApplicationInfo;
import grisu.model.dto.DtoVersionInfo;

import java.util.List;


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
