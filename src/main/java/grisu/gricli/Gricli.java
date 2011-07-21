package grisu.gricli;

import static grisu.gricli.GricliExitStatus.LOGIN;
import static grisu.gricli.GricliExitStatus.RUNTIME;
import static grisu.gricli.GricliExitStatus.SUCCESS;
import static grisu.gricli.GricliExitStatus.SYNTAX;
import grisu.frontend.view.cli.CliHelpers;
import grisu.gricli.command.GricliCommand;
import grisu.gricli.command.GricliCommandFactory;
import grisu.gricli.command.InteractiveLoginCommand;
import grisu.gricli.completors.CompletionCache;
import grisu.gricli.completors.DummyCompletionCache;
import grisu.gricli.parser.GricliTokenizer;
import grisu.settings.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import jline.ArgumentCompletor;
import jline.ConsoleReader;
import jline.History;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class Gricli {

	static final Logger myLogger = Logger.getLogger(Gricli.class.getName());

	static final String CONFIG_FILE_PATH = FilenameUtils.concat(Environment
			.getGrisuClientDirectory().getPath(), "gricli.profile");

	static final String HISTORY_FILE_PATH = FilenameUtils.concat(Environment.getGrisuClientDirectory().getPath() ,
			"gricli.hist");

	static final String DEBUG_FILE_PATH = FilenameUtils.concat(Environment.getGrisuClientDirectory().getPath() ,
			"gricli.debug");

	public static final String COMPLETION_CACHE_REGISTRY_KEY = "CompletionCache";

	public static CompletionCache completionCache = new DummyCompletionCache();

	static String scriptName = null;

	static private GricliEnvironment env;
	public static final GricliCommandFactory SINGLETON_COMMANDFACTORY = GricliCommandFactory
			.getStandardFactory();

	static private GricliExitStatus exitStatus = SUCCESS;

	private static void executionLoop() throws IOException{

		if (System.console() == null){
			run(System.in);
			return;
		}

		if (scriptName != null){
			run(new FileInputStream(scriptName));
			return;
		}

		ConsoleReader reader = getReader();
		while (true) {

			String prompt = getPrompt();
			String line = reader.readLine(prompt);

			if (line==null){
				break;
			}
			String[] commandsOnOneLine = line.split(";");
			for (String c: commandsOnOneLine){
				myLogger.info("gricli-audit-command username=" + System.getProperty("user.name") + "command=" +c );
				runCommand(GricliTokenizer.tokenize(c),
						SINGLETON_COMMANDFACTORY, env);
			}
		}
	}

	private static  String getPrompt(){
		String prompt = env.get("prompt");
		for (String var : env.getGlobalNames()) {
			prompt = StringUtils.replace(prompt, "${" + var + "}",
					env.get(var));

		}
		return prompt;
	}

	private static ConsoleReader getReader() throws IOException {
		//		ConsoleReader reader = new ConsoleReader();
		ConsoleReader reader = CliHelpers.getConsoleReader();
		reader.setHistory(new History(new File(HISTORY_FILE_PATH)));

		ArgumentCompletor completor = new ArgumentCompletor(
				SINGLETON_COMMANDFACTORY.createCompletor(),
				new SemicolonDelimiter());
		completor.setStrict(false);
		reader.addCompletor(completor);
		return reader;
	}
	
	private static void login(GricliEnvironment env, String backend){
		
		try {
			new InteractiveLoginCommand(backend).execute(env);
		} catch (GricliException ex){
			myLogger.error("login exception", ex);
			System.err.println("Cannot login to " + backend);
		}
	}

	@SuppressWarnings("static-access")
	public static void main(String[] args) {

		try {

			// stop javaxws logging
			java.util.logging.LogManager.getLogManager().reset();
			java.util.logging.Logger.getLogger("root").setLevel(Level.ALL);

			String log4jPath = "/etc/gricli/gricli-log4j.xml";
			if (new File(log4jPath).exists()
					&& (new File(log4jPath).length() > 0)) {
				try {
					DOMConfigurator.configure(log4jPath);
				} catch (Exception e) {
					myLogger.error(e);
				}
			}

			env = new GricliEnvironment();

			CommandLineParser parser = new PosixParser();
			Options options = new Options();
			options
					.addOption(OptionBuilder.withLongOpt("nologin")
							.withDescription("disables login at the start")
							.create('n'));
			options.addOption(OptionBuilder.withLongOpt("backend").hasArg()
					.withArgName("backend").withDescription("change backend")
					.create('b'));
			options.addOption(OptionBuilder.withLongOpt("script").hasArg()
					.withArgName("file").withDescription("execute script")
					.create('f'));
			try {
				CommandLine cl = parser.parse(options, args);
				if (!cl.hasOption('n')) {
					String backend = cl.getOptionValue('b');
					backend = (backend != null) ? backend : "BeSTGRID";
					login(env,backend);
				}

				if (cl.hasOption('f')) {
					scriptName = cl.getOptionValue('f');
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				run(new FileInputStream(CONFIG_FILE_PATH));
			} catch (IOException ex) {
				// config does not exist
			}
			executionLoop();
			System.exit(exitStatus.getStatus());
		} catch (Throwable th) {
			System.err.println("Something went terribly wrong.  Please check if you have internet connection, and your firewall settings." +
					" If you think there is nothing wrong with your connection, send " + DEBUG_FILE_PATH + 
					" to eresearch-admin@auckland.ac.nz together with description of what you are trying to do.");
			myLogger.error("something went terribly wrong ",th);
		}
	}

	@SuppressWarnings("unchecked")
	private static void run(InputStream in) throws IOException{

		GricliTokenizer t = new GricliTokenizer(in);
		String[] tokens;
		while ((tokens = t.nextCommand()) != null){
			runCommand(tokens, SINGLETON_COMMANDFACTORY, env);
		}
	}

	private static void runCommand(String[] c, GricliCommandFactory f,
			GricliEnvironment env) {
		Exception error = null;
		try {
			GricliCommand command = f.create(c);
			command.execute(env);
			exitStatus = SUCCESS;

		} catch (InvalidCommandException ex) {
			exitStatus = SYNTAX;
			error = ex;
			System.out.println(ex.getMessage());
		} catch (UnknownCommandException ex) {
			exitStatus = SYNTAX;
			error = ex;
			System.err.println(ex.getMessage());
		} catch (SyntaxException ex) {
			exitStatus = SYNTAX;
			error = ex;
			System.err.println("syntax error "+ ex.getMessage());
		} catch (LoginRequiredException ex) {
			exitStatus = LOGIN;
			error = ex;
			System.err.println("this command requires you to login first");
		} catch (GricliSetValueException ex) {
			exitStatus = RUNTIME;
			error = ex;
			System.err.println("variable " + ex.getVar() + " cannot be set to "
					+ ex.getValue());
			System.err.println("reason: " + ex.getReason());
		} catch (GricliRuntimeException ex) {
			exitStatus = RUNTIME;
			error = ex;
			System.err.println(ex.getMessage());
		} catch (RuntimeException ex){
			exitStatus = RUNTIME;
			error = ex;
			System.err.println("command failed. Either connection to server failed, or this is gricli bug. " +
					"Please send " + DEBUG_FILE_PATH +
					" to eresearch-admin@auckland.ac.nz together with description of what triggered the problem");
		}
		finally {
			myLogger.error(error);
			if ("true".equals(env.get("debug")) && (error != null)){
				error.printStackTrace();
			}
		}
	}

}

class SemicolonDelimiter extends ArgumentCompletor.AbstractArgumentDelimiter {

	@Override
	public boolean isDelimiterChar(String s, int i) {
		return ((s!=null) && (s.length() > i) && (s.charAt(i) == ';'));
	}

}
