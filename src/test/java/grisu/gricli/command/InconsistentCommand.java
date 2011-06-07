package grisu.gricli.command;

public class InconsistentCommand extends ErrorCommand{
	@SyntaxDescription(command={"c1","c2"})
	public InconsistentCommand(){}
	
	@SyntaxDescription(command={"c1"}, arguments={"one"})
	public InconsistentCommand(String one){}
}
