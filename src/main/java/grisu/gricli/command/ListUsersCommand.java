package grisu.gricli.command;

import org.apache.commons.lang.StringUtils;

import com.ctc.wstx.util.StringUtil;

import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.constants.Constants;
import grisu.model.info.dto.DtoStringList;

public class ListUsersCommand extends AbstractAdminCommand implements GricliCommand {
	
	private String user = null;
	
	@SyntaxDescription(command = { "users", "list" })
	public ListUsersCommand() {
		super();
	}
	
	@SyntaxDescription(command = { "user", "list" }, arguments = {"user"})
	public ListUsersCommand(String user) {
		this.user = user;
	}

	@Override
	public void execute(GricliEnvironment env) throws GricliRuntimeException {

		DtoStringList result = execute(env, Constants.LIST_USERS);
		Gricli.completionCache.setAllUsers(result.asSortedSet());

		for (String line : result.asSortedSet()) {
			if ( StringUtils.isBlank(user) ) {
				env.printMessage(line);
			} else {
				if (line.toLowerCase().contains(this.user.toLowerCase())) {
					env.printMessage(line);
				}
			}
		}
		
		
	}



}
