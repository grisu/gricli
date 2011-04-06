package grisu.gricli;

import grisu.gricli.command.GricliCommand;
import grisu.gricli.command.GricliCommandFactory;
import grisu.gricli.util.CommandlineTokenizer;
import grisu.settings.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import org.apache.commons.lang.StringUtils;
import java.util.List;
import java.util.logging.Level;

import jline.ConsoleReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class Gricli {

	static final String CONFIG_FILE_PATH = FilenameUtils.concat(Environment
			.getGrisuClientDirectory().getPath(), "gricli.profile");

	private GricliEnvironment env;
	private GricliCommand command;

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {

		// stop javaxws logging
		java.util.logging.LogManager.getLogManager().reset();
		java.util.logging.Logger.getLogger("root").setLevel(Level.OFF);

		GricliEnvironment env = new GricliEnvironment();
		ConsoleReader reader = new ConsoleReader();
		GricliCommandFactory f = new GricliCommandFactory();
		reader.addCompletor(f.createCompletor());
		List<String> commands = null;
		try {
			commands = FileUtils.readLines(new File(CONFIG_FILE_PATH));
		} catch (FileNotFoundException fx) {
			commands = new LinkedList<String>();
		}

		for (String c : commands) {
			runCommand(c, f, env);
		}

		while (true) {
			String prompt = env.get("prompt");
			for (String var : env.getGlobalNames()) {
				prompt = StringUtils.replace(prompt, "${" + var + "}",
						env.get(var));

			}
			String line = reader.readLine(prompt);
			if (line==null){
				break;
			}
			runCommand(line, f, env);
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
			System.err.println("syntax error");
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

}
