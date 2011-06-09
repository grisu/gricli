package grisu.gricli.command;

import grisu.gricli.SyntaxException;
import grisu.gricli.UnknownCommandException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public class CommandCreator{
	
	private Class<? extends GricliCommand> command;
	private HashMap<String,CommandCreator> keywords =
		new HashMap<String,CommandCreator>();
	
	
	private CommandCreator argCreator = null;
	private boolean hasVarArg = false;
	
	private Constructor<? extends GricliCommand> cons = null;
	
	
	public CommandCreator addKeyword(String keyword){
		CommandCreator c = keywords.get(keyword);
		if (c == null){
			c = new CommandCreator();
			keywords.put(keyword, c);
		}
		return c;
	}
	
	public CommandCreator addArgument(String argName) throws CompileException{
		if (this.keywords.size() > 0){
			throw new CompileException("inconsistent commands");
		}
		if (this.argCreator == null){
			this.argCreator = new CommandCreator();
		}
		return this.argCreator;
	}
	
	public CommandCreator addVarArg(String vararg) throws CompileException {
		if (this.keywords.size() > 0){
			throw new CompileException("inconsistent commands");
		}
		this.hasVarArg = true;
		return this;
	}
	
	public CommandCreator addConstructor(Constructor<? extends GricliCommand> cons){
		this.cons = cons;
		return this;
	}

	public GricliCommand create(String[] tokens) throws SyntaxException {
		PartialCommand pc = new PartialCommand();
		CommandCreator c = this;
		for (int i = 0; i< tokens.length; i++){
			c = c.nextToken(pc, tokens[i]);
		}
		if (c.cons == null){
			throw new SyntaxException("command does not exist");
		}
		pc.cons = c.cons;
		return pc.create();
	}
	
	private CommandCreator nextToken(PartialCommand pc, String nextToken) throws SyntaxException{
		CommandCreator c = keywords.get(nextToken);
		if (c != null){
			return c;
		}
		
		c = this.argCreator;
		
		if (c!= null){
			pc.arguments.add(nextToken);
			return c;
		} 
		
		if (this.hasVarArg){
			pc.arguments.add(nextToken);
			return this;
		}
		
		throw new UnknownCommandException("unknown command");
	}
	
	class PartialCommand {
		public ArrayList<String> arguments = new ArrayList<String>();
		public Constructor<? extends GricliCommand> cons = null;
		
		public GricliCommand create() throws SyntaxException{
			try {
				if (cons.isVarArgs()){
					return cons.newInstance((Object)(arguments.toArray(new String[] {})));
				}
				return cons.newInstance(arguments.toArray());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw new SyntaxException("illegal arguments");
			} catch (InstantiationException e) {
				throw new SyntaxException(e.getMessage());
			} catch (IllegalAccessException e) {
				throw new SyntaxException(e.getMessage());
			} catch (InvocationTargetException e) {
				throw new SyntaxException(e.getMessage());
			}
		}
	}


}
