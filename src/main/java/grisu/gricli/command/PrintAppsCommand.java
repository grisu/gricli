package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.ApplicationCompletor;
import grisu.gricli.completors.ApplicationVersionCompletor;
import grisu.gricli.environment.GricliEnvironment;
import grisu.gricli.util.OutputHelpers;
import grisu.gricli.util.ServiceInterfaceUtils;
import grisu.model.GrisuRegistryManager;
import grisu.model.info.UserApplicationInformation;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;


public class PrintAppsCommand implements
GricliCommand {

	private final String app;
	private String version;

	@SyntaxDescription(command={"print","applications"})
	public PrintAppsCommand(){
		this(null);
	}

	@SyntaxDescription(command={"print","application"},arguments={"application"})
	@AutoComplete(completors={ApplicationCompletor.class})
	public PrintAppsCommand(String app){
		this(app,"*");
	}
	
	@SyntaxDescription(command={"print","application"},arguments={"application","version"})
	@AutoComplete(completors={ApplicationCompletor.class,ApplicationVersionCompletor.class})
	public PrintAppsCommand(String app,String version){
		this.app = app;
		this.version = version;
	}
	

	public GricliEnvironment execute(GricliEnvironment env)
	throws GricliRuntimeException {

		if (this.app != null){
			printApplicationsTable(env,app);
		} else {
			printApplications(env);
		}
		return env;
	}
	
	private void printApplicationsTable(GricliEnvironment env, String filter) throws GricliRuntimeException {
		ServiceInterface si = env.getServiceInterface();
		List<String> apps = ServiceInterfaceUtils.filterApplicationNames(si, filter);
		List<List<String>> table = new LinkedList<List<String>>();
		
		List<String> title = new LinkedList<String>();
		title.add("name");
		title.add("version");
		title.add("queue");
		table.add(title);
		
		for (String app: apps){
			
			UserApplicationInformation m = GrisuRegistryManager.getDefault(si).getUserApplicationInformation(app);
			Set<String> versions = m.getAllAvailableVersionsForUser();
			for (String version: versions){
				if  (!FilenameUtils.wildcardMatch(version, this.version, IOCase.INSENSITIVE)){
					continue;
				}
				Set<String> sublocs = m.getAvailableSubmissionLocationsForVersionAndFqan(version, env.group.get());
				
				List<String> row = new LinkedList<String>();
				row.add(app);
				row.add(version);
				row.add("");
				table.add(row);
				for (String subloc: sublocs){
					List<String> queueRow = new LinkedList<String>();
					queueRow.add("");
					queueRow.add("");
					queueRow.add(subloc);
					table.add(queueRow);
				}
				
			}
		}
		
		env.printMessage(OutputHelpers.getTable(table));
	}


	private void printApplications(GricliEnvironment env) throws GricliRuntimeException{
		ServiceInterface si = env.getServiceInterface();
		List<String> apps = ServiceInterfaceUtils.filterApplicationNames(si, "*");
		for (String app: apps){
			env.printMessage(app);
		}
	}

}
