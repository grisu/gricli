package grisu.gricli.command;

import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.SyntaxException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.gricli.parser.GricliTokenizer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import jline.FileNameCompletor;

import org.apache.commons.lang.StringUtils;

public class RunCommand implements GricliCommand {

	private final String script;

	@SyntaxDescription(command={"run"}, arguments={"script"})
	@AutoComplete(completors={FileNameCompletor.class})
	public RunCommand(String script) {
		if (script.startsWith("~")){
			script = StringUtils.replace(
					script, "~", System.getProperty("user.home"));
		}
		this.script = script;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {


		ArrayList<GricliCommand> cl = new ArrayList<GricliCommand>();

		try {
			GricliTokenizer tokenizer = new GricliTokenizer(new FileInputStream(script));
			String[] tokens;
			while ((tokens = tokenizer.nextCommand()) != null){
				cl.add(Gricli.SINGLETON_COMMANDFACTORY.create(tokens));
			}
		} catch (FileNotFoundException e) {
			throw new GricliRuntimeException("file " + script + " not found ", e);
		} catch (IOException e){
			throw new GricliRuntimeException(
					"IO error while reading " + script, e);
		} catch (SyntaxException e){
			throw new GricliRuntimeException("error during parsing of "
					+ script + ": " + e.getMessage(), e);
		}
		return new CompositeCommand(cl.toArray(new GricliCommand[] {})).execute(env);
	}

}
