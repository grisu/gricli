package grisu.gricli.command;

public class VarArgsCommand extends ErrorCommand {
	
	@SyntaxDescription(command={"var"}, arguments={"varargs"})
	public VarArgsCommand(String...strings){}
}
