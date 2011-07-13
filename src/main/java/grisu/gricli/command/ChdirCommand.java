package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import jline.FileNameCompletor;

import org.apache.commons.lang.StringUtils;

public class ChdirCommand implements GricliCommand {

	private final SetCommand c;

	private static String lastUrl = null;

	@SyntaxDescription(command = {"cd"})
	public ChdirCommand(){
		this(System.getProperty("user.home"));
	}

	@SyntaxDescription(command = { "cd" }, arguments={"dir"})
	@AutoComplete(completors={FileNameCompletor.class})
	public ChdirCommand(String dir) {
		if ("-".equals(dir)) {
			if (StringUtils.isBlank(lastUrl)) {
				dir = System.getProperty("user.home");
			} else {
				dir = lastUrl;
			}
		}
		c = new SetCommand("dir", dir);
	}


	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		lastUrl = env.get("dir");
		return c.execute(env);
	}

}
