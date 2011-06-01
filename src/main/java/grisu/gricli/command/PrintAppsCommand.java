package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.model.dto.DtoApplicationInfo;
import grisu.model.dto.DtoVersionInfo;

import java.util.List;

import org.apache.commons.lang.StringUtils;


public class PrintAppsCommand implements GricliCommand {
	
	private String app;

	@SyntaxDescription(command={"print","application"})
	// @AutoComplete(completors={SiteCompletor.class})
	public PrintAppsCommand(String app){
		this.app = app;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		ServiceInterface si = env.getServiceInterface();
		DtoApplicationInfo info = si.getSubmissionLocationsPerVersionOfApplication(app);
		
		List<DtoVersionInfo> versions = info.getAllVersions();
			for (DtoVersionInfo version: versions){
				String versionTag = version.getName();
				env.printMessage(app + " : " + versionTag + "\n submission locations:\n " 
						+ StringUtils.join(version.getAllSubmissionLocations().asSubmissionLocationStrings(),","));
			}
		
		return env;
	}

}
