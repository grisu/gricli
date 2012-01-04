package grisu.gricli;

public class UnknownCommandException extends SyntaxException {
	private static final long serialVersionUID = 1L;

	public UnknownCommandException(String command) {
		super(command);
	}

}
