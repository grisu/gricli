package grisu.gricli;

public class TooManyArgumentsException extends SyntaxException {
	private static final long serialVersionUID = 1L;

	public TooManyArgumentsException(String message) {
		super(message);
	}
}
