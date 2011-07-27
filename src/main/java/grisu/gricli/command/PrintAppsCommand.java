package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.model.GrisuRegistryManager;
import grisu.model.info.UserApplicationInformation;

import java.util.Set;

import org.apache.commons.lang.StringUtils;


public class PrintAppsCommand implements
GricliCommand {

	private final String app;

	@SyntaxDescription(command={"print","applications"})
	public PrintAppsCommand(){
		this(null);
	}

	@SyntaxDescription(command={"print","application"},arguments={"application"})
	// @AutoComplete(completors={SiteCompletor.class})
	public PrintAppsCommand(String app){
		this.app = app;
	}

	public GricliEnvironment execute(GricliEnvironment env)
	throws GricliRuntimeException {

		if (this.app != null){
			printApplication(env,app);
		} else {
			printApplications(env);
		}
		return env;
	}

	private void printApplication(GricliEnvironment env,String app) throws GricliRuntimeException{
		ServiceInterface si = env.getServiceInterface();
		UserApplicationInformation m = GrisuRegistryManager.getDefault(si).getUserApplicationInformation(app);
		Set<String> versions = m.getAllAvailableVersionsForUser();
		for (String version: versions){
			env.printMessage("version : " + version);

			Set<String> sublocs = m.getAvailableSubmissionLocationsForVersion(version);
			env.printMessage(StringUtils.join(sublocs,","));

			env.printMessage("");
		}
	}

	private void printApplications(GricliEnvironment env) throws GricliRuntimeException{
		ServiceInterface si = env.getServiceInterface();
		String[] apps = GrisuRegistryManager.getDefault(si).getUserEnvironmentManager().getAllAvailableApplications();
		for (String app: apps){
			env.printMessage(app);
		}
	}

}
