/**
 * one-argument command 
 */
package grisu.gricli.command;

class Args1Command extends ErrorCommand {
	@SyntaxDescription(command={"arg1"},arguments={"one"})
	public Args1Command(String one){}
}