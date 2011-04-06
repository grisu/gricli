package grisu.gricli;

/**
 * any error that occurs during execution of command
 */
public class GricliRuntimeException extends GricliException {
	private static final long serialVersionUID = 1L;

	public GricliRuntimeException(String msg) {
		super(msg);
	}

	public GricliRuntimeException() {
		super();
	}

	public GricliRuntimeException(Exception ex) {
		super(ex);
	}
}
