package grisu.gricli;

public class GricliSetValueException extends GricliRuntimeException {
	private static final long serialVersionUID = 1L;
	private final String var;
	private final String value;
	private final String reason;

	public GricliSetValueException(String var, String value, String reason) {
		super();
		this.var = var;
		this.value = value;
		this.reason = reason;
	}

	public String getReason() {
		return this.reason;
	}

	public String getValue() {
		return this.value;
	}

	public String getVar() {
		return this.var;
	}
}
