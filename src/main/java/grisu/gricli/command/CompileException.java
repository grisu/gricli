package grisu.gricli.command;

import grisu.gricli.GricliException;
/**
 *  Happens during compile phase of command factory
 */
public class CompileException extends GricliException {
	public CompileException(String string) {
		super(string);
	}

	private static final long serialVersionUID = 1L;

}
