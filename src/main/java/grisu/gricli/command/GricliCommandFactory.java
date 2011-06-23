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
	/** private HashMap<String, Constructor<? extends GricliCommand>> commandMap;
	private HashSet<String> commandSet; **/
	private Completor tabCompletor;
	private CommandCreator creator;
	
	public static GricliCommandFactory getCustomFactory(Class<? extends GricliCommand>... commands ) throws CompileException{
		GricliCommandFactory f = new GricliCommandFactory();
		for (Class<? extends GricliCommand> c: commands){
			f.add(c);
		}
		f.init();
		return f;
	}
	
	public static GricliCommandFactory getStandardFactory() {
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
		
		// print commands
		f.add(PrintGlobalsCommand.class);
		f.add(PrintQueuesCommand.class);
		f.add(PrintAppsCommand.class);
		f.add(PrintHostsCommand.class);
		f.add(PrintGroupsCommand.class);
		
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
		f.add(AboutCommand.class);
		
		// filesystem commands
		f.add(GridLsCommand.class);
		f.add(ClearCacheCommand.class);
		f.add(PwdCommand.class);
		f.add(ChdirCommand.class);
		
		//batch commands
		f.add(CreateBatchCommand.class);
		f.add(AddBatchCommand.class);
		f.add(SubmitBatchCommand.class);
		
		try {
			f.init();
		} catch (CompileException e) {
			// shouldn't happen
			e.printStackTrace();
		}
		
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
	
	private void addCommand(Constructor<? extends GricliCommand> cons, CommandCreator creator)
		throws CompileException{
		SyntaxDescription sd = cons.getAnnotation(SyntaxDescription.class);

		for (String keyword: sd.command()){
			creator = creator.addKeyword(keyword);
		}
		if (cons.isVarArgs()){
			creator = creator.addVarArg("vararg");
		} else {

			for (String arg: sd.arguments()){
				creator = creator.addArgument(arg);
			}
		}
		creator.addConstructor(cons);
	}
	
	private ArgumentCompletor getTabCompletor(SyntaxDescription sd, AutoComplete auto, boolean isVar){
		List<Completor> simpleCompletors = new ArrayList<Completor>();
		for (String token: sd.command()){
			simpleCompletors.add(new SimpleCompletor(new String[] {token}));
		}

		// specialised argument annotations
		if (auto != null){
			Class<? extends Completor>[] argumentCompletors = auto.completors();				
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

		if (!isVar){
			simpleCompletors.add(new NullCompletor());
		}
		
		return new ArgumentCompletor(simpleCompletors.toArray(new Completor[] {}));
	}
	
	@SuppressWarnings("unchecked")
	public void init() throws CompileException{

		this.creator = new CommandCreator();
		List<Completor> commandCompletors = new ArrayList<Completor>();

		for (Class<? extends GricliCommand> c: commands){
			Constructor<? extends GricliCommand>[] conss = 
				(Constructor<? extends GricliCommand>[])c.getDeclaredConstructors();
			for (Constructor<? extends GricliCommand> cons: conss){
				SyntaxDescription sd = cons.getAnnotation(SyntaxDescription.class);

				if (sd == null) {
					continue;
				}

				addCommand(cons,creator);
				commandCompletors.add(getTabCompletor(sd,cons.getAnnotation(AutoComplete.class),cons.isVarArgs()));
			}
		}

		this.tabCompletor = new MultiCompletor(commandCompletors.toArray(new Completor[] {}));
	}
	
	public GricliCommand create(String[] args) throws SyntaxException {
		return creator.create(args);
	}
}
