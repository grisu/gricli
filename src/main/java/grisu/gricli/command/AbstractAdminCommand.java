package grisu.gricli.command;

import java.util.Map;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.model.info.dto.DtoProperties;
import grisu.model.info.dto.DtoStringList;

public abstract class AbstractAdminCommand {


	protected DtoStringList execute(GricliEnvironment env, String command, Map<String, String> config) throws GricliRuntimeException {
		DtoProperties props = null;
		if ( config != null ) {
			props = DtoProperties.createProperties(config);
		}
		
		DtoStringList result = env.getServiceInterface().admin(command, props);
		
		if ( result == null ) {
			return null;
		} else {
			return result;
		}
	}
	
	protected DtoStringList execute(GricliEnvironment env, String command) throws GricliRuntimeException {
		return execute(env, command, null);
	}
}
