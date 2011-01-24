package org.bestgrid.grisu.client.gricli;

import java.io.File;
import java.io.FileNotFoundException;
import org.bestgrid.grisu.client.gricli.command.GricliCommandFactory;
import org.bestgrid.grisu.client.gricli.command.GricliCommand;
import java.io.IOException;
import java.util.LinkedList;
import org.apache.commons.lang.StringUtils;
import java.util.List;
import java.util.logging.Level;

import org.vpac.grisu.settings.Environment;
import jline.ConsoleReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bestgrid.grisu.client.gricli.util.CommandlineTokenizer;

public class Gricli {

	static final String CONFIG_FILE_PATH = FilenameUtils.concat(Environment
			.getGrisuClientDirectory().getPath(), "gricli.profile");

	private GricliEnvironment env;
	private GricliCommand command;

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
			runCommand(line, f, env);
		}
	}

	private static void runCommand(String c, GricliCommandFactory f,
			GricliEnvironment env) {
		try {
			String[] arguments = CommandlineTokenizer.tokenize(c);
			GricliCommand command = f.create(arguments);
			command.execute(env);
		} catch (InvalidCommandException ex) {
			System.out.println(ex.getMessage());
		} catch (UnknownCommandException ex) {
			System.err
					.println("command " + ex.getMessage() + " does not exist");
		} catch (SyntaxException ex) {
			System.err.println("syntax error");
			ex.printStackTrace();
		} catch (LoginRequiredException ex) {
			System.err.println("this command requires you to login first");
		} catch (GricliSetValueException ex) {
			System.err.println("variable " + ex.getVar() + " cannot be set to "
					+ ex.getValue());
			System.err.println("reason: " + ex.getReason());
		} catch (GricliRuntimeException ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

}
