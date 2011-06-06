package grisu.gricli.command;

public class MultiCommand extends ErrorCommand{
	
	public String one,two;

	@SyntaxDescription(command = { "multi" })
	public MultiCommand(){
	}
	
	@SyntaxDescription(command = { "multi"}, arguments={"one"})
	public MultiCommand(String one){
		this.one = one;
	}
	
	@SyntaxDescription(command = { "multi"}, arguments={"one","two"})
	public MultiCommand(String one,String two){
		this.one = one;
		this.two = two;
	}
	
}
