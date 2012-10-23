package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.constants.Constants;
import grisu.model.info.dto.DtoStringList;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class ClearUserCacheCommand extends AbstractAdminCommand implements
		GricliCommand {

	private String user = Constants.ALL_USERS;

	@SyntaxDescription(command = { "users", "clear", "cache" })
	public ClearUserCacheCommand() {
		super();
	}

	@SyntaxDescription(command = { "user", "clear", "cache" }, arguments = { "user" })
	// @AutoComplete(completors = { UsersCompletor.class })
	public ClearUserCacheCommand(String user) {
		super();
		this.user = user;
	}

	@Override
	public void execute(GricliEnvironment env) throws GricliRuntimeException {

		DtoStringList users = execute(env, Constants.LIST_USERS);
		Set<String> toClear = Sets.newTreeSet();
		for (String u : users.asArray()) {
			if (u.toLowerCase().contains(this.user.toLowerCase())) {
				toClear.add(u);
			}
		}
		
		if ( toClear.isEmpty() ) {
			env.printMessage("No matching user for: "+this.user);
			return;
		}

		for (String u : toClear) {
			Map<String, String> config = ImmutableMap.of(Constants.USER, u);
			DtoStringList result = execute(env, Constants.CLEAR_USER_CACHE,
					config);

			for (String line : result.asArray()) {
				env.printMessage(line);
			}
		}

	}

}
