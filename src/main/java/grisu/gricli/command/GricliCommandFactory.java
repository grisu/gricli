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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GricliCommandFactory {

	private static Logger myLogger = LoggerFactory
			.getLogger(GricliCommandFactory.class);

	public static GricliCommandFactory getCustomFactory(
			Class<? extends GricliCommand>... commands) throws CompileException {
		final GricliCommandFactory f = new GricliCommandFactory();
		for (final Class<? extends GricliCommand> c : commands) {
			f.add(c);
		}
		f.init();
		return f;
	}

	public static GricliCommandFactory getStandardFactory() {
		final GricliCommandFactory f = new GricliCommandFactory();
		f.add(AddCommand.class);
		f.add(AttachCommand.class);
		f.add(DownloadJobCommand.class);
		f.add(FilemanagerCommand.class);
		f.add(SetCommand.class);
		f.add(LocalLoginCommand.class);
		f.add(InteractiveLoginCommand.class);
		f.add(NopCommand.class);

		// print commands
		f.add(PrintGlobalsCommand.class);
		f.add(PrintQueueCommand.class);
		f.add(PrintQueuesCommand.class);
		f.add(PrintAppsCommand.class);
		f.add(PrintGroupsCommand.class);

		f.add(RunCommand.class);

		f.add(KillJobCommand.class);
		f.add(CleanJobCommand.class);
		f.add(PrintJobCommand.class);
		f.add(ArchiveJobCommand.class);
		f.add(SubmitCommand.class);
		f.add(DownloadAndCleanCommand.class);
		f.add(WaitJobCommand.class);
		f.add(StatusCommand.class);

		f.add(QuitCommand.class);
		f.add(ExitCommand.class);
		f.add(LogoutCommand.class);
		f.add(HelpCommand.class);
		f.add(AproposCommand.class);
		f.add(AboutCommand.class);

		// filesystem commands
		// f.add(GridLsCommand.class);
		f.add(ClearCacheCommand.class);
		f.add(PwdCommand.class);
		f.add(ChdirCommand.class);
		f.add(LsCommand.class);
		// f.add(CpCommand.class);

		// batch commands
		f.add(CreateBatchCommand.class);
		f.add(AddBatchCommand.class);
		f.add(SubmitBatchCommand.class);

		// other commands
		f.add(ExecCommand.class);
		f.add(RefreshProxyCommand.class);
		f.add(ViewCommand.class);
		f.add(PrintMessagesCommand.class);
		f.add(PrintTasksCommand.class);
		f.add(PrintTaskCommand.class);

		try {
			f.init();
		} catch (final CompileException e) {
			// shouldn't happen
			myLogger.error(e.getLocalizedMessage(), e);
		}

		return f;
	}

	private List<Class<? extends GricliCommand>> commands = new ArrayList<Class<? extends GricliCommand>>();

	/**
	 * private HashMap<String, Constructor<? extends GricliCommand>> commandMap;
	 * private HashSet<String> commandSet;
	 **/
	private Completor tabCompletor;

	private CommandCreator creator;

	public GricliCommandFactory() {
		this.commands = new ArrayList<Class<? extends GricliCommand>>();
	}

	public void add(Class<? extends GricliCommand> c) {
		commands.add(c);
	}

	private void addCommand(Constructor<? extends GricliCommand> cons,
			CommandCreator creator) throws CompileException {
		final SyntaxDescription sd = cons
				.getAnnotation(SyntaxDescription.class);

		for (final String keyword : sd.command()) {
			creator = creator.addKeyword(keyword);
		}
		if (cons.isVarArgs()) {
			creator = creator.addVarArg("vararg");
		} else {

			for (final String arg : sd.arguments()) {
				creator = creator.addArgument(arg);
			}
		}
		creator.addConstructor(cons);
	}

	public GricliCommand create(String[] args) throws SyntaxException {

		return creator.create(args);
	}

	public Completor createCompletor() {
		return this.tabCompletor;
	}

	public List<Class<? extends GricliCommand>> getCommands() {
		return new ArrayList<Class<? extends GricliCommand>>(commands);
	}

	private ArgumentCompletor getTabCompletor(SyntaxDescription sd,
			AutoComplete auto, boolean isVar) {
		final List<Completor> simpleCompletors = new ArrayList<Completor>();
		for (final String token : sd.command()) {
			simpleCompletors.add(new SimpleCompletor(new String[] { token }));
		}

		// specialised argument annotations
		if (auto != null) {
			final Class<? extends Completor>[] argumentCompletors = auto
					.completors();
			for (final Class<? extends Completor> argumentCompletor : argumentCompletors) {
				try {
					simpleCompletors.add(argumentCompletor.newInstance());
				} catch (final InstantiationException e) {
					myLogger.error(e.getLocalizedMessage(), e);
				} catch (final IllegalAccessException e) {
					myLogger.error(e.getLocalizedMessage(), e);
				}
			}
		}

		if (!isVar) {
			simpleCompletors.add(new NullCompletor());
		}

		return new ArgumentCompletor(
				simpleCompletors.toArray(new Completor[] {}));
	}

	@SuppressWarnings("unchecked")
	public void init() throws CompileException {

		this.creator = new CommandCreator();
		final List<Completor> commandCompletors = new ArrayList<Completor>();

		for (final Class<? extends GricliCommand> c : commands) {
			final Constructor<? extends GricliCommand>[] conss = (Constructor<? extends GricliCommand>[]) c
					.getDeclaredConstructors();
			for (final Constructor<? extends GricliCommand> cons : conss) {
				final SyntaxDescription sd = cons
						.getAnnotation(SyntaxDescription.class);

				if (sd == null) {
					continue;
				}

				addCommand(cons, creator);
				commandCompletors.add(getTabCompletor(sd,
						cons.getAnnotation(AutoComplete.class),
						cons.isVarArgs()));
			}
		}

		this.tabCompletor = new MultiCompletor(
				commandCompletors.toArray(new Completor[] {}));
	}
}
