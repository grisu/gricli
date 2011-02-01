/**
 * command syntax is incorrect
 */
package grisu.gricli;

public class InvalidCommandException extends SyntaxException {

	public InvalidCommandException(String message) {
		super(message);
	}
}
