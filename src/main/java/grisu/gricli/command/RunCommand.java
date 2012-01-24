package grisu.gricli.command;

import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.SyntaxException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.gricli.parser.GricliTokenizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import jline.FileNameCompletor;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

public class RunCommand implements GricliCommand {

	private final String script;

	@SyntaxDescription(command = { "run" }, arguments = { "script" })
	@AutoComplete(completors = { FileNameCompletor.class })
	public RunCommand(String script) {
		if (script.startsWith("~")) {
			script = StringUtils.replace(script, "~",
					System.getProperty("user.home"));
		}
		this.script = script;
	}

	public void execute(GricliEnvironment env)
			throws GricliRuntimeException {

		final ArrayList<GricliCommand> cl = new ArrayList<GricliCommand>();
		GricliTokenizer tokenizer = null;

		try {
			final File f = new File(FilenameUtils.concat(
					env.getCurrentAbsoluteDirectory(), script));
			tokenizer = new GricliTokenizer(new FileInputStream(f));
			String[] tokens;
			while ((tokens = tokenizer.nextCommand()) != null) {
				if ((tokens.length > 0) && (tokens[0].startsWith("#"))) {
					continue;
				}
				final String annotation = "line number "
						+ tokenizer.getLineNumber() + ": ";
				final AnnotatedCommand c = new AnnotatedCommand(annotation,
						Gricli.SINGLETON_COMMANDFACTORY.create(tokens));
				cl.add(c);
			}
		} catch (final FileNotFoundException e) {
			throw new GricliRuntimeException("file " + script + " not found ",
					e);
		} catch (final IOException e) {
			throw new GricliRuntimeException(
					"IO error while reading " + script, e);
		} catch (final SyntaxException e) {
			throw new GricliRuntimeException("error during parsing of "
					+ script + " on line " + tokenizer.getLineNumber() + "  :"
					+ e.getMessage());
		}
		new CompositeCommand(cl.toArray(new GricliCommand[] {}))
		.execute(env);
	}

}
