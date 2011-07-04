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

public class Gricli {

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
	static private GricliCommandFactory f = GricliCommandFactory.getStandardFactory();

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
				runCommand(GricliTokenizer.tokenize(c), f, env);
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

		ArgumentCompletor completor = new ArgumentCompletor(f.createCompletor(), new SemicolonDelimiter());
		completor.setStrict(false);
		reader.addCompletor(completor);
		return reader;
	}

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws IOException,GricliException {

		// stop javaxws logging
		java.util.logging.LogManager.getLogManager().reset();
		java.util.logging.Logger.getLogger("root").setLevel(Level.ALL);

		env = new GricliEnvironment(f);

		CommandLineParser parser = new PosixParser();
		Options options = new Options();
		options.addOption(OptionBuilder.withLongOpt("nologin").withDescription("disables login at the start").create('n'));
		options.addOption(OptionBuilder.withLongOpt("backend").hasArg().withArgName("backend").withDescription("change backend").create('b'));
		options.addOption(OptionBuilder.withLongOpt("script").hasArg().withArgName("file").withDescription("execute script").create('f'));
		try {
			CommandLine cl = parser.parse(options, args);
			if (!cl.hasOption('n')){
				String backend = cl.getOptionValue('b');
				backend = (backend != null)?backend:"BeSTGRID";
				new InteractiveLoginCommand(backend).execute(env);
			}

			if (cl.hasOption('f')){
				scriptName = cl.getOptionValue('f');
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			run(new FileInputStream(CONFIG_FILE_PATH));
		} catch (IOException ex){
			// config does not exist
		}
		executionLoop();
		System.exit(exitStatus.getStatus());
	}

	@SuppressWarnings("unchecked")
	private static void run(InputStream in) throws IOException{

		GricliTokenizer t = new GricliTokenizer(in);
		String[] tokens;
		while ((tokens = t.nextCommand()) != null){
			runCommand(tokens,f,env);
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
