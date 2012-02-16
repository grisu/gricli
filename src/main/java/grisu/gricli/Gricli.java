package grisu.gricli;

import static grisu.gricli.GricliExitStatus.LOGIN;
import static grisu.gricli.GricliExitStatus.RUNTIME;
import static grisu.gricli.GricliExitStatus.SUCCESS;
import static grisu.gricli.GricliExitStatus.SYNTAX;
import grisu.frontend.control.login.LoginManager;
import grisu.gricli.command.GricliCommand;
import grisu.gricli.command.GricliCommandFactory;
import grisu.gricli.command.InteractiveLoginCommand;
import grisu.gricli.command.LocalLoginCommand;
import grisu.gricli.command.RunCommand;
import grisu.gricli.completors.CompletionCache;
import grisu.gricli.completors.DummyCompletionCache;
import grisu.gricli.environment.GricliEnvironment;
import grisu.gricli.environment.GricliVar;
import grisu.gricli.parser.GricliTokenizer;
import grisu.jcommons.utils.EnvironmentVariableHelpers;
import grisu.jcommons.utils.VariousStringHelpers;
import grisu.jcommons.view.cli.CliHelpers;
import grisu.jcommons.view.cli.LineByLineProgressDisplay;
import grisu.settings.Environment;
import grith.jgrith.control.SlcsLoginWrapper;
import grith.jgrith.plainProxy.LocalProxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import jline.ArgumentCompletor;
import jline.ConsoleReader;
import jline.History;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.python.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

public class Gricli {

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
			.getStandardFactory();

	static private GricliExitStatus exitStatus = SUCCESS;

	// public static final int MINIMUM_PROXY_LIFETIME_BEFORE_RENEW_REQUEST =
	// (3600 * 24 * 9)
	// + (3600 * 23) + 3500;

	// 3 days
	public static final int MINIMUM_PROXY_LIFETIME_BEFORE_RENEW_REQUEST = 259200;

	private static void configLogging() {
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

	private static boolean login(GricliEnvironment env, String backend,
			boolean x509, String username, String idp) {

		try {
			GricliCommand login = null;
			if (System.console() != null) {
				login = new InteractiveLoginCommand(backend, x509, username,
						idp);
			} else {
				login = new LocalLoginCommand(backend);
			}
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

	private static boolean login(GricliEnvironment env, String backend,
			String credConfig) {

		try {
			GricliCommand login = null;
			login = new LocalLoginCommand(backend, credConfig, null);

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

	@SuppressWarnings("static-access")
	public static void main(String[] args) {

		configLogging();

		Thread.currentThread().setName("main");

		LoginManager.setClientName("gricli");

		LoginManager.setClientVersion(grisu.jcommons.utils.Version
				.get("gricli"));

		EnvironmentVariableHelpers.loadEnvironmentVariablesToSystemProperties();

		Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());

		CliHelpers.setProgressDisplay(new LineByLineProgressDisplay());


		// MDC.put("local_user", System.getProperty("user.name"));
		// MDC.put("gricli-version", Version.get("gricli"));

		LoginManager.initEnvironment();

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

			env = new GricliEnvironment();
			SigintHandler.install(env);

			final CommandLineParser parser = new PosixParser();
			CommandLine cl = null;
			final Options options = new Options();
			options.addOption(OptionBuilder.withLongOpt("nologin")
					.withDescription("disables login at the start").create('n'));
			options.addOption(OptionBuilder.withLongOpt("backend").hasArg()
					.withArgName("backend").withDescription("change backend")
					.create('b'));
			options.addOption(OptionBuilder.withLongOpt("script").hasArg()
					.withArgName("file").withDescription("execute script")
					.create('f'));
			options.addOption(OptionBuilder.withLongOpt("username").hasArg()
					.withArgName("username")
					.withDescription("institution or myproxy username")
					.create("u"));
			options.addOption(OptionBuilder.withLongOpt("institution").hasArg()
					.withArgName("institution_name")
					.withDescription("institution name").create("i"));
			options.addOption(OptionBuilder.withLongOpt("x509")
					.withDescription("x509 certificate login").create("x"));
			options.addOption(OptionBuilder.withLongOpt("credential").hasArg()
					.withDescription("credential config file").create("c"));
			options.addOption(OptionBuilder.withLongOpt("myproxy").hasArg()
					.withDescription("MyProxy host to use)").create("m"));

			try {
				cl = parser.parse(options, args);

				// setting default myproxy server
				if (cl.hasOption('m')) {

					String host = cl.getOptionValue('m');
					LoginManager.myProxyHost = host;

				}

				if (!cl.hasOption('n')) {

					if (cl.hasOption("c")) {

						if (cl.hasOption("u") || cl.hasOption("x")
								|| cl.hasOption("i")) {
							env.printError("Credential config file specified, ignoring -u, -x and -i options...");
						}

						final String credConfigFile = cl.getOptionValue("c");

						if (StringUtils.isBlank(credConfigFile)) {
							throw new ParseException(
									"No credential config file specified.");
						}

						String backend = cl.getOptionValue('b');
						backend = (backend != null) ? backend : "BeSTGRID";

						if (!login(env, backend, credConfigFile)) {
							System.exit(LOGIN.getStatus());
						}

					} else {

						// checking whether login parameters make sense...
						if (cl.hasOption("u")) {
							// means myproxy or shib login
							if (cl.hasOption("x")) {
								throw new ParseException(
										"X509 login and other login method specified. Please use only one.");
							}
						}
						if (cl.hasOption("x")) {
							if (cl.hasOption("i")) {
								throw new ParseException(
										"X509 login and idp login methods specified. Please use only one.");
							}
						}

						final String username = cl.getOptionValue("u");

						final String idp = cl.getOptionValue("i");

						final boolean x509 = cl.hasOption("x");

						String backend = cl.getOptionValue('b');
						backend = (backend != null) ? backend : "BeSTGRID";
						if (!login(env, backend, x509, username, idp)) {
							System.exit(LOGIN.getStatus());
						}
					}
				}

				if (cl.hasOption('f')) {
					scriptName = cl.getOptionValue('f');
				}

			} catch (final ParseException e) {
				myLogger.error(e.getLocalizedMessage(), e);
				new HelpFormatter().printHelp("griclish ", options);
				System.exit(SYNTAX.getStatus());
			}

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


}

class SemicolonDelimiter extends ArgumentCompletor.AbstractArgumentDelimiter {

	@Override
	public boolean isDelimiterChar(String s, int i) {
		return ((s != null) && (s.length() > i) && (s.charAt(i) == ';'));
	}

}
