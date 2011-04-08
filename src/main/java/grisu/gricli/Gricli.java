package grisu.gricli;

import grisu.gricli.command.GricliCommand;
import grisu.gricli.command.GricliCommandFactory;
import grisu.gricli.command.InteractiveLoginCommand;
import grisu.gricli.util.CommandlineTokenizer;
import grisu.settings.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import org.apache.commons.lang.StringUtils;
import java.util.List;
import java.util.logging.Level;

import jline.ArgumentCompletor;
import jline.ConsoleReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Gricli {

	static final String CONFIG_FILE_PATH = FilenameUtils.concat(Environment
			.getGrisuClientDirectory().getPath(), "gricli.profile");
	
	static private GricliEnvironment env = new GricliEnvironment();
	static private GricliCommandFactory f = new GricliCommandFactory();

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws IOException,GricliException {

		// stop javaxws logging
		java.util.logging.LogManager.getLogManager().reset();
		java.util.logging.Logger.getLogger("root").setLevel(Level.OFF);
		
		CommandLineParser parser = new PosixParser();
		Options options = new Options();
		options.addOption(OptionBuilder.withLongOpt("nologin").withDescription("disables login at the start").create('n'));
		options.addOption(OptionBuilder.withLongOpt("backend").hasArg().withArgName("backend").withDescription("change backend").create('b'));
		try {
			CommandLine cl = parser.parse(options, args);
			if (!cl.hasOption('n')){
				String backend = cl.getOptionValue('b');
				backend = (backend != null)?backend:"BeSTGRID";
				new InteractiveLoginCommand(backend).execute(env);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		parseConfig();
		executionLoop();
	}
	
	private static void executionLoop() throws IOException{
		while (true) {
			
			ConsoleReader reader = getReader();
						
			String prompt = getPrompt();
			String line = reader.readLine(prompt);
			if (line==null){
				break;
			}
			String[] commandsOnOneLine = line.split(";");
			for (String c: commandsOnOneLine){
				runCommand(c, f, env);
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
	
	private static void parseConfig() throws IOException{
		List<String> commands = null;
		try {
			commands = FileUtils.readLines(new File(CONFIG_FILE_PATH));
		} catch (FileNotFoundException fx) {
			commands = new LinkedList<String>();
		}

		for (String c : commands) {
			runCommand(c, f, env);
		}
	}

	private static void runCommand(String c, GricliCommandFactory f,
			GricliEnvironment env) {
		Exception error = null;
		try {
			String[] arguments = CommandlineTokenizer.tokenize(c);
			GricliCommand command = f.create(arguments);
			command.execute(env);
			
		} catch (InvalidCommandException ex) {
			System.out.println(ex.getMessage());
		} catch (UnknownCommandException ex) {
			error = ex;
			System.err
					.println("command " + ex.getMessage() + " does not exist");
		} catch (SyntaxException ex) {
			error = ex;
			System.err.println("syntax error "+ ex.getMessage());
		} catch (LoginRequiredException ex) {
			error = ex;
			System.err.println("this command requires you to login first");
		} catch (GricliSetValueException ex) {
			error = ex;
			System.err.println("variable " + ex.getVar() + " cannot be set to "
					+ ex.getValue());
			System.err.println("reason: " + ex.getReason());
		} catch (GricliRuntimeException ex) {
			error = ex;
			System.err.println(ex.getMessage());
		} finally {
			if ("true".equals(env.get("debug")) && (error != null)){
				error.printStackTrace();
			}
		}
	}
	
	private static ConsoleReader getReader() throws IOException {
		ConsoleReader reader = new ConsoleReader();
		
		ArgumentCompletor completor = new ArgumentCompletor(f.createCompletor(), new SemicolonDelimiter());
		completor.setStrict(false);
		reader.addCompletor(completor);
		return reader;
	}

}

class SemicolonDelimiter extends ArgumentCompletor.AbstractArgumentDelimiter {

	public boolean isDelimiterChar(String s, int i) {
		return (s!=null && s.length() > i && (s.charAt(i) == ';'));
	}
	
}
