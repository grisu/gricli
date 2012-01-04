package grisu.gricli;

public class NotEnoughArgumentsException extends SyntaxException {
	private static final long serialVersionUID = 1L;

	public NotEnoughArgumentsException(String message) {
		super(message);
	}

}
