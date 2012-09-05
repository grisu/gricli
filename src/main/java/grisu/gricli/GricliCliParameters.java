package grisu.gricli;

import grisu.frontend.view.cli.GrisuCliParameters;

import com.beust.jcommander.Parameter;

public class GricliCliParameters extends GrisuCliParameters {

	@Parameter(names = { "-f", "--script" }, description = "the path to a gricli script")
	private String script;

	public String getScript() {
		return script;
	}

}
