package grisu.gricli;

import grisu.frontend.view.cli.GridCliParameters;

import com.beust.jcommander.Parameter;

public class GricliCliParameters extends GridCliParameters {

	@Parameter(names = { "-f", "--script" }, description = "the path to a gricli script")
	private String script;

	public String getScript() {
		return script;
	}

}
