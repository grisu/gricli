package grisu.gricli.command;

import grisu.gricli.SyntaxException;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.MultiCompletor;
import jline.NullCompletor;
import jline.SimpleCompletor;

import org.apache.log4j.Logger;

public class GricliCommandFactory {

	private static Logger myLogger = Logger
			.getLogger(GricliCommandFactory.class.getName());

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
		f.add(PrintQueueCommand.class);
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
		f.add(AproposCommand.class);
		f.add(AboutCommand.class);

		// filesystem commands
		f.add(GridLsCommand.class);
		f.add(ClearCacheCommand.class);
		f.add(PwdCommand.class);
		f.add(ChdirCommand.class);
		f.add(LsCommand.class);
		f.add(CpCommand.class);

		//batch commands
		f.add(CreateBatchCommand.class);
		f.add(AddBatchCommand.class);
		f.add(SubmitBatchCommand.class);

		// other commands
		f.add(ExecCommand.class);

		f.add(ViewCommand.class);

		try {
			f.init();
		} catch (CompileException e) {
			// shouldn't happen
			myLogger.error(e);
		}

		return f;
	}
	private  List<Class<? extends GricliCommand>> commands = new ArrayList<Class<? extends GricliCommand>>();

	/** private HashMap<String, Constructor<? extends GricliCommand>> commandMap;
	private HashSet<String> commandSet; **/
	private Completor tabCompletor;

	private CommandCreator creator;

	public GricliCommandFactory(){
		this.commands = new ArrayList<Class<? extends GricliCommand>>();
	}

	public void add(Class<? extends GricliCommand> c){
		commands.add(c);
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


	public GricliCommand create(String[] args) throws SyntaxException {
		return creator.create(args);
	}

	public Completor createCompletor(){
		return this.tabCompletor;
	}

	public List<Class<? extends GricliCommand>> getCommands(){
		return new ArrayList<Class<? extends GricliCommand>>(commands);
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
					myLogger.error(e);
				} catch (IllegalAccessException e) {
					myLogger.error(e);
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
}
