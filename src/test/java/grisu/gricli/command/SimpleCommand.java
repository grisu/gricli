/**
 * simplest command possible
 */
package grisu.gricli.command;


class SimpleCommand extends ErrorCommand {
	@SyntaxDescription(command={"a"},arguments={})
	public SimpleCommand(){}
}