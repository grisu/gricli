package grisu.gricli;

public class UnknownCommandException extends SyntaxException {

	public UnknownCommandException(String command) {
		super(command);
	}

}
