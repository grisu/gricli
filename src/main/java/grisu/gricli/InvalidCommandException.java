/**
 * command syntax is incorrect
 */
package grisu.gricli;

public class InvalidCommandException extends SyntaxException {
	private static final long serialVersionUID = 1L;

	public InvalidCommandException(String message) {
		super(message);
	}
}
