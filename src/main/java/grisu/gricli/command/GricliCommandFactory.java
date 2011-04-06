package grisu.gricli.command;

import grisu.gricli.SyntaxException;
import grisu.gricli.UnknownCommandException;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jline.ArgumentCompletor;
import jline.MultiCompletor;
import jline.SimpleCompletor;
import jline.Completor;
import jline.NullCompletor;
import org.apache.commons.lang.StringUtils;

public class GricliCommandFactory {
	
	public static List<Class<? extends GricliCommand>> commands = new ArrayList<Class<? extends GricliCommand>>();
	private HashMap<String, Constructor<? extends GricliCommand>> commandMap;
	private Completor tabCompletor;
	
	static {
		commands.add(AddGlobalCommand.class);
		commands.add(AttachCommand.class);
		commands.add(ClearListCommand.class);
		commands.add(DownloadJobCommand.class);
		commands.add(FilemanagerCommand.class);
		commands.add(SetGlobalCommand.class);
		commands.add(LocalLoginCommand.class);
		commands.add(InteractiveLoginCommand.class);
		commands.add(NopCommand.class);
		commands.add(PrintGlobalsCommand.class);
		commands.add(PrintQueuesCommand.class);
		commands.add(PrintAppsCommand.class);
		commands.add(PrintHostsCommand.class);
		commands.add(PrintJobCommand.class);
		commands.add(SubmitCmdCommand.class);
		commands.add(QuitCommand.class);
		commands.add(HelpCommand.class);
		
	}
	
	public Completor createCompletor(){
		return this.tabCompletor;
	}
	
	public GricliCommandFactory(){
		
		commandMap = new HashMap<String, Constructor<? extends GricliCommand>>();
		List<Completor> commandCompletors = new ArrayList<Completor>();
				
		for (Class<? extends GricliCommand> c: commands){
			Constructor<? extends GricliCommand>[] conss = (Constructor<? extends GricliCommand>[])c.getDeclaredConstructors();
			for (Constructor<? extends GricliCommand> cons: conss){
				SyntaxDescription ca = cons.getAnnotation(SyntaxDescription.class);
				
				if (ca == null) {
					continue;
				}
				
				String[] commandString = new String[ca.command().length + 1];
				System.arraycopy(ca.command(), 0, commandString, 0, ca.command().length);				
				commandString[ca.command().length] = "" + cons.getGenericParameterTypes().length;
				commandMap.put(StringUtils.join(commandString," "), cons);	
				
				// create tab completor for that command
				List<Completor> simpleCompletors = new ArrayList<Completor>();
				for (String token: ca.command()){
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
				
				simpleCompletors.add(new NullCompletor()); 
				commandCompletors.add(new ArgumentCompletor(simpleCompletors.toArray(new Completor[] {})));
			}
		}
		
		this.tabCompletor = new MultiCompletor(commandCompletors.toArray(new Completor[] {}));
	}

	public GricliCommand create(String[] args) throws SyntaxException {
		
		for (int i = args.length; i >= 0; i--){
			String[] commandWords = new String[i+1];
			System.arraycopy(args, 0, commandWords, 0, i);
			String[] arguments = new String[args.length - i];
			System.arraycopy(args, i, arguments, 0, arguments.length);
			
			commandWords[i] = "" + (args.length - i);
			Constructor<? extends GricliCommand> cons = commandMap.get(StringUtils.join(commandWords," "));
			if (cons != null){
				try { // any exception at this stage will be implementation bug
					return cons.newInstance((Object[])arguments);
				} catch (Exception e) {				
					e.printStackTrace();
				} 				
			}
		}
		throw new UnknownCommandException(StringUtils.join(args," "));
	
	} 
}
