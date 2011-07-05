package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.VarCompletor;
import grisu.gricli.completors.VarValueCompletor;
import grisu.jcommons.constants.Constants;
import grisu.model.GrisuRegistry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class SetCommand implements GricliCommand {

	static final Logger myLogger = Logger.getLogger(SetCommand.class.getName());

	private final String global;
	private final String value;

	@SyntaxDescription(command={"set"},arguments={"var","value"})
	@AutoComplete(completors={VarCompletor.class, VarValueCompletor.class})
	public SetCommand(String global, String value) {
		this.global = global;
		this.value = value;
	}

	public GricliEnvironment execute(final GricliEnvironment env)
			throws GricliRuntimeException {

		env.put(global, value);

		// load application/fqan combination for completion cache...
		if (Constants.APPLICATIONNAME_KEY.equals(global)) {

			if (!StringUtils.isBlank(value)
					&& ! Constants.GENERIC_APPLICATION_NAME.equals(value)) {
				final String fqan = env.get("group");
				if (StringUtils.isNotBlank(fqan)) {
					final String a = value;
					new Thread() {
						@Override
						public void run() {
							GrisuRegistry reg = env.getGrisuRegistry();
							myLogger.debug("Pre-loading cache for " + a + " / "
									+ fqan);
							reg.getApplicationInformation(a)
							.getAllAvailableVersionsForFqan(fqan);
							myLogger.debug("Pre-loading finished.");
						}
					}.start();
				}
			}
		} else if ( "group".equals(global)) {
			if (StringUtils.isNotBlank(value)) {
				final String app = env.get(Constants.APPLICATIONNAME_KEY);
				if (StringUtils.isNotBlank(app) && !Constants.GENERIC_APPLICATION_NAME.equals(app)) {
					final String f = value;
					new Thread() {
						@Override
						public void run() {

							GrisuRegistry reg = env.getGrisuRegistry();
							myLogger.debug("Pre-loading cache for "+f+" / "+app);
							reg.getApplicationInformation(app).getAllAvailableVersionsForFqan(f);
							myLogger.debug("Pre-loading finished.");
						}
					}.start();
				}
			}
		}
		return env;

	}
}
