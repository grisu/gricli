package grisu.gricli.command;

import grisu.gricli.InvalidCommandException;
import grisu.gricli.SyntaxException;
import grisu.gricli.UnknownCommandException;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import jline.ArgumentCompletor;
import jline.MultiCompletor;
import jline.SimpleCompletor;
import jline.Completor;
import jline.NullCompletor;
import org.apache.commons.lang.StringUtils;

public class GricliCommandFactory {
	
	private  List<Class<? extends GricliCommand>> commands = new ArrayList<Class<? extends GricliCommand>>();
	private HashMap<String, Constructor<? extends GricliCommand>> commandMap;
	private HashSet<String> commandSet;
	private Completor tabCompletor;
	
	public static GricliCommandFactory getCustomFactory(Class<? extends GricliCommand>[] commands){
		GricliCommandFactory f = new GricliCommandFactory();
		for (Class<? extends GricliCommand> c: commands){
			f.add(c);
		}
		f.init();
		return f;
	}
	
	public static GricliCommandFactory getStandardFactory(){
		GricliCommandFactory f = new GricliCommandFactory();
		f.add(AddCommand.class);
		f.add(AttachCommand.class);
		f.add(ClearListCommand.class);
		f.add(DownloadJobCommand.class);
		f.add(FilemanagerCommand.class);
		f.add(SetCommand.class);
		f.add(LocalLoginCommand.class);
		f.add(InteractiveLoginCommand.class);
		f.add(NopCommand.class);
		f.add(PrintGlobalsCommand.class);
		f.add(PrintQueuesCommand.class);
		f.add(PrintAppsCommand.class);
		f.add(PrintHostsCommand.class);
		
		f.add(RunCommand.class);
		
		f.add(KillJobCommand.class);
		f.add(CleanJobCommand.class);
		f.add(PrintJobCommand.class);
		f.add(ArchiveJobCommand.class);
		f.add(SubmitCommand.class);
		f.add(DownloadAndCleanCommand.class);
		f.add(WaitJobCommand.class);
		
		f.add(QuitCommand.class);
		f.add(LogoutCommand.class);
		f.add(HelpCommand.class);
		
		// filesystem commands
		f.add(GridLsCommand.class);
		f.add(ClearCacheCommand.class);
		
		//batch commands
		f.add(CreateBatchCommand.class);
		f.add(AddBatchCommand.class);
		f.add(SubmitBatchCommand.class);
		
		f.init();
		
		return f;
	}
	
	public void add(Class<? extends GricliCommand> c){
		commands.add(c);
	}
	
	public List<Class<? extends GricliCommand>> getCommands(){
		return new ArrayList<Class<? extends GricliCommand>>(commands);
	}
	
	public Completor createCompletor(){
		return this.tabCompletor;
	}
	

	public GricliCommandFactory(){		
		this.commands = new ArrayList<Class<? extends GricliCommand>>();	
	}
	
	@SuppressWarnings("unchecked")
	public void init(){
		commandMap = new HashMap<String, Constructor<? extends GricliCommand>>();
		commandSet = new HashSet<String>();
		List<Completor> commandCompletors = new ArrayList<Completor>();
				
		for (Class<? extends GricliCommand> c: commands){
			Constructor<? extends GricliCommand>[] conss = 
				(Constructor<? extends GricliCommand>[])c.getDeclaredConstructors();
			for (Constructor<? extends GricliCommand> cons: conss){
				SyntaxDescription sd = cons.getAnnotation(SyntaxDescription.class);
				
				if (sd == null) {
					continue;
				}
				
				commandSet.add(StringUtils.join(sd.command()," "));
				String[] commandString = new String[sd.command().length + 1];
				System.arraycopy(sd.command(), 0, commandString, 0, sd.command().length);
				
				String argumentNumber = null;
				if (cons.isVarArgs()){
					argumentNumber = "*";
				} 
				else {
					argumentNumber = "" + cons.getGenericParameterTypes().length;
				}
				commandString[sd.command().length] = argumentNumber;
				commandMap.put(StringUtils.join(commandString," "), cons);	
				
				// create tab completor for that command
				List<Completor> simpleCompletors = new ArrayList<Completor>();
				for (String token: sd.command()){
					simpleCompletors.add(new SimpleCompletor(new String[] {token}));
				}
				
				// specialised argument annotations
				if (cons.isAnnotationPresent(AutoComplete.class)){
					Class<? extends Completor>[] argumentCompletors = cons.getAnnotation(AutoComplete.class).completors();				
					for (Class<? extends Completor> argumentCompletor: argumentCompletors){
						try {
							simpleCompletors.add(argumentCompletor.newInstance());
						} catch (InstantiationException e) {
							// cannot happen 
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// cannot happen
							e.printStackTrace();
						}
					}
				}
				
				if (!cons.isVarArgs()){
					simpleCompletors.add(new NullCompletor());
				}
				commandCompletors.add(new ArgumentCompletor(simpleCompletors.toArray(new Completor[] {})));
			}
		}
		
		this.tabCompletor = new MultiCompletor(commandCompletors.toArray(new Completor[] {}));
	}
	

	public GricliCommand create(String[] args) throws SyntaxException {
		
		for (int i = args.length; i >= 0; i--){
			String[] commandWords = new String[i+1];
			String[] argumentWords = new String[i];
			System.arraycopy(args, 0, commandWords, 0, i);
			System.arraycopy(args, 0, argumentWords, 0, i);
			String[] arguments = new String[args.length - i];
			System.arraycopy(args, i, arguments, 0, arguments.length);
			
			// check for variable number of arguments first
			Constructor<? extends GricliCommand> cons;
			commandWords[i] = "*";
			cons = commandMap.get(StringUtils.join(commandWords," "));
			
			if (cons == null){
				commandWords[i] = "" + (args.length - i);
			}
			
			cons = commandMap.get(StringUtils.join(commandWords," "));
			if (cons != null){
				try { // any exception at this stage will be implementation bug
					if (cons.isVarArgs()){
						return cons.newInstance((Object)arguments);
					}
					return cons.newInstance((Object[])arguments);
				} catch (Exception e) {				
					e.printStackTrace();
				} 				
			} 
			// command is known but number of arguments is wrong
			else if (commandSet.contains(StringUtils.join(argumentWords," "))){
				throw new InvalidCommandException("wrong number of arguments");
			}
		}
		throw new UnknownCommandException(StringUtils.join(args," "));
	
	} 
}
