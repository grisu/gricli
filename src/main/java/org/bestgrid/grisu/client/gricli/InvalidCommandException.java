/**
 * command syntax is incorrect
 */
package org.bestgrid.grisu.client.gricli;

public class InvalidCommandException extends SyntaxException {

	public InvalidCommandException(String message) {
		super(message);
	}
}
