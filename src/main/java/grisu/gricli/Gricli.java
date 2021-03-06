package grisu.gricli;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import com.beust.jcommander.ParameterException;
import com.google.common.base.Strings;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.view.cli.GrisuCliClient;
import grisu.gricli.command.GricliCommand;
import grisu.gricli.command.GricliCommandFactory;
import grisu.gricli.command.InteractiveLoginCommand;
import grisu.gricli.command.RunCommand;
import grisu.gricli.completors.CompletionCache;
import grisu.gricli.completors.DummyCompletionCache;
import grisu.gricli.environment.GricliEnvironment;
import grisu.gricli.environment.GricliVar;
import grisu.gricli.parser.GricliTokenizer;
import grisu.jcommons.utils.VariousStringHelpers;
import grisu.jcommons.view.cli.CliHelpers;
import grisu.jcommons.view.cli.LineByLineProgressDisplay;
import grisu.settings.Environment;
import grith.jgrith.control.SlcsLoginWrapper;
import grith.jgrith.cred.GridLoginParameters;
import grith.jgrith.plainProxy.LocalProxy;
import jline.ArgumentCompletor;
import jline.ConsoleReader;
import jline.History;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import static grisu.gricli.GricliExitStatus.*;

public class Gricli extends GrisuCliClient<GricliCliParameters> {

	static final Logger myLogger = LoggerFactory.getLogger(Gricli.class
			.getName());

	static final String CONFIG_FILE_PATH = FilenameUtils.concat(Environment
			.getGrisuClientDirectory().getPath(), "gricli.profile");
	static final String SESSION_SETTINGS_PATH = FilenameUtils.concat(
			Environment.getGrisuClientDirectory().getPath(),
			"gricli-session.profile");

	static final String HISTORY_FILE_PATH = FilenameUtils.concat(Environment
			.getGrisuClientDirectory().getPath(), "gricli.hist");

	static final String DEBUG_FILE_PATH = FilenameUtils.concat(Environment
			.getGrisuClientDirectory().getPath(), "gricli.debug");

	public static final String COMPLETION_CACHE_REGISTRY_KEY = "CompletionCache";

	public static CompletionCache completionCache = new DummyCompletionCache();

	static String scriptName = null;

	static private GricliEnvironment env;
	public static final GricliCommandFactory SINGLETON_COMMANDFACTORY = GricliCommandFactory
			.getCommandFactory();

	static private GricliExitStatus exitStatus = SUCCESS;

	// public static final int MINIMUM_PROXY_LIFETIME_BEFORE_RENEW_REQUEST =
	// (3600 * 24 * 9)
	// + (3600 * 23) + 3500;

	// 3 days
	public static final int MINIMUM_PROXY_LIFETIME_BEFORE_RENEW_REQUEST = 259200;

	private static void configLogging() {
		LoggerContext lc2 = (LoggerContext) LoggerFactory.getILoggerFactory();
		// print logback's internal status
		// StatusPrinter.print(lc2);

		// stop javaxws logging
		java.util.logging.LogManager.getLogManager().reset();
		java.util.logging.Logger.getLogger("root").setLevel(Level.ALL);

		String logback = "/etc/gricli/gricli.log.conf.xml";

		if (!new File(logback).exists() || (new File(logback).length() == 0)) {
			logback = Environment.getGrisuClientDirectory()
					+ File.separator
					+ "gricli.log.conf.xml";
		}
		if (new File(logback).exists()
				&& (new File(logback).length() > 0)) {

			LoggerContext lc = (LoggerContext) LoggerFactory
					.getILoggerFactory();

			try {
				JoranConfigurator configurator = new JoranConfigurator();
				configurator.setContext(lc);
				// the context was probably already configured by default
				// configuration
				// rules
				lc.reset();
				configurator.doConfigure(logback);
			} catch (JoranException je) {
				je.printStackTrace();
			}
			StatusPrinter.printInCaseOfErrorsOrWarnings(lc);

		}


	}

	private static void executionLoop() throws IOException {

		if (System.console() == null) {
			run(System.in);
			return;
		}

		if (scriptName != null) {
			myLogger.debug("Executing script: script=[{}]", scriptName);
			List<String> lines = FileUtils.readLines(new File(scriptName));
			for (Integer i=0; i<lines.size(); i++) {
				String line = lines.get(i);
				myLogger.debug("script=[{}] line=[{}]", line,
						Strings.padStart(i.toString(), 3, '0'));
			}
			run(new FileInputStream(scriptName));
			myLogger.debug("Finished script: script=[{}]", scriptName);
			return;
		}

		final ConsoleReader reader = getReader();
		while (true) {

			String prompt = getPrompt();

			final String line = reader.readLine(prompt);

			if (line == null) {
				break;
			}
			final String[] commandsOnOneLine = line.split(";");
			for (final String c : commandsOnOneLine) {
				runCommand(GricliTokenizer.tokenize(c),
						SINGLETON_COMMANDFACTORY, env);
			}

		}
	}

	private static String generateSession(GricliEnvironment env) {
		String result = "";
		for (final GricliVar<?> var : env.getVariables()) {
			if (var.isPersistent()) {
				final Object value = var.get();
				if (value == null) {
					result += "unset " + var.getName() + "\n";
				} else {
					result += "set " + var.getName() + " "
							+ GricliTokenizer.escape(var.marshall()) + "\n";
				}
			}
		}
		return result;
	}

	private static String getPrompt() {
		String prompt = env.prompt.get();

		// check whether there are new notifications for users.
		if (env.getNotifications().size() > 0) {
			prompt = "(*" + env.getNotifications().size() + ") " + prompt;
		}

		/*
		 * will have to restore this function later for (String var :
		 * env.getGlobalNames()) { prompt = StringUtils.replace(prompt, "${" +
		 * var + "}", env.get(var));
		 *
		 * }
		 */
		return prompt;
	}

	private static ConsoleReader getReader() throws IOException {
		// ConsoleReader reader = new ConsoleReader();
		final ConsoleReader reader = CliHelpers.getConsoleReader();
		reader.setHistory(new History(new File(HISTORY_FILE_PATH)));

		final ArgumentCompletor completor = new ArgumentCompletor(
				SINGLETON_COMMANDFACTORY.createCompletor(),
				new SemicolonDelimiter());
		completor.setStrict(false);
		reader.addCompletor(completor);
		return reader;
	}

	private static boolean login(GricliEnvironment env,
			GrisuCliClient<GricliCliParameters> client) {

		try {
			GricliCommand login = null;
			// if (System.console() != null) {
			login = new InteractiveLoginCommand(client);
			// } else {
			// login = new LocalLoginCommand(client.getCliParameters()
			// .getBackend());
			// }
			login.execute(env);

			prepareLogging(env);

			return true;
		} catch (final Exception ex) {
			myLogger.error("Login exception", ex);
			Throwable t = ex;
			while (t.getCause() != null) {
				t = t.getCause();
			}
			System.err.println(t.getLocalizedMessage());
			return false;
		}
	}


	// private static boolean login(GricliEnvironment env, String backend,
	// String credConfig) {
	//
	// try {
	// GricliCommand login = null;
	// login = new LocalLoginCommand(backend, credConfig, null);
	//
	// login.execute(env);
	//
	// prepareLogging(env);
	//
	// return true;
	// } catch (final Exception ex) {
	// myLogger.error("Login exception", ex);
	// Throwable t = ex;
	// while (t.getCause() != null) {
	// t = t.getCause();
	// }
	// System.err.println(t.getLocalizedMessage());
	// return false;
	// }
	//
	// }

	@SuppressWarnings("static-access")
	public static void main(String[] args) {

		// try {
		// Thread.sleep(3000);
		// } catch (InterruptedException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// System.out.println("STARTING...");
		// System.setProperty(
		// CommonGridProperties.Property.DAEMONIZE_GRID_SESSION.toString(),
		// "false");

		Gricli g = null;
		try {
			g = new Gricli(args);
		} catch (Exception e) {
			System.err.println("Could not start gricli: "
					+
					e.getLocalizedMessage());
			System.exit(1);

		}

		g.run();

	}

	private static void prepareLogging(GricliEnvironment env) {
		try {
			final String dn = env.getServiceInterface().getDN();
			MDC.put("dn", dn);
			String un = VariousStringHelpers.getCN(dn);
			MDC.put("user", un);
		} catch (final Exception e) {
			myLogger.error(e.getLocalizedMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private static void run(InputStream in) throws IOException {

		final GricliTokenizer t = new GricliTokenizer(in);
		String[] tokens;
		while ((tokens = t.nextCommand()) != null) {
			runCommand(tokens, SINGLETON_COMMANDFACTORY, env);

		}
	}

	private static void runCommand(String[] c, GricliCommandFactory f,
			GricliEnvironment env) {

		if ((c == null) || (c.length == 0)) {
			return;
		}

		Throwable error = null;
		Date start = null;
		Date end = null;
		try {


			start = new Date();
			String cmdId = c[0] + "_" + start.getTime();
			MDC.put("cmdid", cmdId);

			final GricliCommand command = f.create(c);

			if ((c == null) || (c.length == 0)) {
				return;
			}

			myLogger.info("Executing command: " + "command=[{}]",
					StringUtils.join(c, " "));
			command.execute(env);

			exitStatus = SUCCESS;

		} catch (final InvalidCommandException ex) {
			exitStatus = SYNTAX;
			error = ex;
			System.out.println(ex.getMessage());
		} catch (final UnknownCommandException ex) {
			exitStatus = SYNTAX;
			error = ex;
			System.err.println(ex.getMessage());
		} catch (final SyntaxException ex) {
			exitStatus = SYNTAX;
			error = ex;
			System.err.println("syntax error " + ex.getMessage());
		} catch (final LoginRequiredException ex) {
			exitStatus = LOGIN;
			error = ex;
			System.err.println("this command requires you to login first");
		} catch (final GricliSetValueException ex) {
			exitStatus = RUNTIME;
			error = ex;
			if (StringUtils.isBlank(ex.getValue())) {

				System.err.println("Global '" + ex.getVar()
						+ "' cannot be unset.");
			} else {
				System.err.println("variable " + ex.getVar()
						+ " cannot be set to " + ex.getValue());
				System.err.println("reason: " + ex.getReason());
			}
		} catch (final GricliRuntimeException ex) {
			exitStatus = RUNTIME;
			Throwable exc = ex;
			while (exc.getCause() != null) {
				exc = exc.getCause();
			}
			error = exc;
			System.err.println(exc.getMessage());
		} catch (final RuntimeException ex) {
			exitStatus = RUNTIME;
			error = ex;
			System.err
			.println("command failed. Either connection to server failed, or this is gricli bug. "
					+ "Please send "
					+ DEBUG_FILE_PATH
					+ " to eresearch-admin@auckland.ac.nz together with description of what triggered the problem");
		} finally {

			end = new Date();
			if (start == null) {
				start = end;
			}

			long duration = end.getTime() - start.getTime();

			if (error != null) {
				myLogger.error(error.getLocalizedMessage(), error);
				if (env.debug.get() && (error != null)) {
					error.printStackTrace();
				}
				myLogger.info("Finished command: "
						+ "command=[{}] [failed] error=[{}] duration=[{}]",
						new Object[] { StringUtils.join(c, " "),
								error.getLocalizedMessage(), duration });
			} else {
				myLogger.info("Finished command: "
						+ "command=[{}] duration=[{}]",
						StringUtils.join(c, " "), duration);
			}
			MDC.remove("cmdid");
		}
	}

	public static void shutdown(GricliEnvironment env) {
		try {
			final File f = new File(SESSION_SETTINGS_PATH);
			final String session = generateSession(env);
			FileUtils.writeStringToFile(f, session);
		} catch (final IOException ex) {
			myLogger.error(ex.getLocalizedMessage(), ex);
			env.printError("warning: could not save session");
		}
	}

	private static void startSession() throws IOException {
		try {
			if (new File(SESSION_SETTINGS_PATH).exists()) {
				new RunCommand(SESSION_SETTINGS_PATH).execute(env);
			}
			if (new File(CONFIG_FILE_PATH).exists()) {
				new RunCommand(CONFIG_FILE_PATH).execute(env);
			}
		} catch (final GricliRuntimeException ex) {
			// config does not exist
			env.printError(ex.getMessage());
		}
		executionLoop();
		shutdown(env);
		System.exit(exitStatus.getStatus());
	}

	// private final String[] args;

	public Gricli(String[] args) throws Exception {
		super(new GricliCliParameters(), args);
		// this.args = args;
	}

	@Override
	public void run() {


		configLogging();


		try {
			LoginManager.initGrisuClient("gricli");
		} catch (Exception e) {
			System.err.println("Can't initialize environment: "
					+ e.getLocalizedMessage());
			System.exit(1);
		}


		Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());

		CliHelpers.setProgressDisplay(new LineByLineProgressDisplay());



		final Thread t = new Thread() {
			@Override
			public void run() {
				try {
					myLogger.debug("Preloading idps...");
					SlcsLoginWrapper.getAllIdps();
				} catch (final Throwable e) {
					myLogger.error(e.getLocalizedMessage(), e);
				}
			}
		};
		t.setDaemon(true);
		t.setName("preloadIdpsThread");

		if (LocalProxy
				.validGridProxyExists(MINIMUM_PROXY_LIFETIME_BEFORE_RENEW_REQUEST / 60)) {
			// not that important, still, we want it to load in case of renew
			// session
			t.setPriority(Thread.MIN_PRIORITY);
		}
		t.start();

		try {
			GricliCliParameters gcp = getCliParameters();

			env = new GricliEnvironment();
			SigintHandler.install(env);

			GridLoginParameters glp;
			glp = GridLoginParameters.createFromGridCliParameters(gcp);


			if (glp.isNologin()) {
				startSession();
			}


			if (!login(env, this)) {
				System.exit(LOGIN.getStatus());
			}

			scriptName = gcp.getScript();

			startSession();
		} catch (ParameterException pe) {
			// throw new ParseException(pe.getLocalizedMessage());
			pe.printStackTrace();

		} catch (final Throwable th) {
			th.printStackTrace();
			System.err
			.println("Something went terribly wrong.  Please check if you have internet connection, and your firewall settings."
					+ " If you think there is nothing wrong with your connection, send "
					+ DEBUG_FILE_PATH
					+ " to eresearch-admin@auckland.ac.nz together with description of what you are trying to do.");
			myLogger.error("something went terribly wrong ", th);
		}
	}


}

class SemicolonDelimiter extends ArgumentCompletor.AbstractArgumentDelimiter {

	@Override
	public boolean isDelimiterChar(String s, int i) {
		return ((s != null) && (s.length() > i) && (s.charAt(i) == ';'));
	}

}
