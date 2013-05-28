package grisu.gricli.command;

import org.apache.commons.lang.StringUtils;

import com.ctc.wstx.util.StringUtil;

import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.constants.Constants;
import grisu.model.info.dto.DtoStringList;

public class UpdateInfoCommand extends AbstractAdminCommand implements GricliCommand {
	

	@SyntaxDescription(command = { "reload", "info" })
	public UpdateInfoCommand() {
		super();
	}

	@Override
	public void execute(GricliEnvironment env) throws GricliRuntimeException {

		DtoStringList result = execute(env, Constants.REFRESH_GRID_INFO);
		for (String line : result.asArray()) {
			env.printMessage(line);
		}
		
	}



}
