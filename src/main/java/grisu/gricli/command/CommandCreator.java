package grisu.gricli.command;

import grisu.gricli.NotEnoughArgumentsException;
import grisu.gricli.SyntaxException;
import grisu.gricli.TooManyArgumentsException;
import grisu.gricli.UnknownCommandException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class CommandCreator{

	class PartialCommand {
		public ArrayList<String> keywords = new ArrayList<String>();
		public ArrayList<String> arguments = new ArrayList<String>();
		public Constructor<? extends GricliCommand> cons = null;

		public GricliCommand create() throws SyntaxException {
			try {
				if (cons.isVarArgs()) {
					return cons.newInstance((Object) (arguments
							.toArray(new String[] {})));
				}
				return cons.newInstance(arguments.toArray());
			} catch (IllegalArgumentException e) {
				myLogger.error(e);
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

	static final Logger myLogger = Logger.getLogger(CommandCreator.class.getName());
	private Class<? extends GricliCommand> command;


	private final HashMap<String,CommandCreator> keywords =
			new HashMap<String,CommandCreator>();
	private CommandCreator argCreator = null;

	private boolean hasVarArg = false;


	private Constructor<? extends GricliCommand> cons = null;

	public CommandCreator addArgument(String argName) throws CompileException{
		if (this.keywords.size() > 0){
			throw new CompileException("inconsistent commands");
		}
		if (this.argCreator == null){
			this.argCreator = new CommandCreator();
		}
		return this.argCreator;
	}

	public CommandCreator addConstructor(Constructor<? extends GricliCommand> cons){
		this.cons = cons;
		return this;
	}

	public CommandCreator addKeyword(String keyword){
		CommandCreator c = keywords.get(keyword);
		if (c == null){
			c = new CommandCreator();
			keywords.put(keyword, c);
		}
		return c;
	}

	public CommandCreator addVarArg(String vararg) throws CompileException {
		if (this.keywords.size() > 0){
			throw new CompileException("inconsistent commands");
		}
		this.hasVarArg = true;
		return this;
	}

	public GricliCommand create(String[] tokens) throws SyntaxException {
		PartialCommand pc = new PartialCommand();
		CommandCreator c = this;
		for (String token : tokens) {
			c = c.nextToken(pc, token);
		}
		if (c.cons == null && c.argCreator != null){
			String command = StringUtils.join(pc.keywords.toArray()," ");
			throw new NotEnoughArgumentsException("not enough arguments for \"" + command + "\"");
		} else if (c.cons == null){
			throw new UnknownCommandException("incomplete command");
		}
		pc.cons = c.cons;
		return pc.create();
	}
	

	private CommandCreator nextToken(PartialCommand pc, String nextToken) throws SyntaxException{
		CommandCreator c = keywords.get(nextToken);
		if (c != null){
			pc.keywords.add(nextToken);
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
		
		String command = StringUtils.join(pc.keywords.toArray(), " ");
		
		if ((pc.arguments.size() > 0 || this.cons != null) && command.length() > 0) {
			throw new TooManyArgumentsException("too many arguments for \"" + command + "\"");
		} else {
			throw new UnknownCommandException("unknown command: \"" + command + " " + nextToken + "\"");
		}
	}
	
}
