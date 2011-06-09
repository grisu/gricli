package grisu.gricli.command;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import jline.FileNameCompletor;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.SyntaxException;
import grisu.gricli.parser.GricliTokenizer;

public class RunCommand implements GricliCommand {
	
	private String script;

	@SyntaxDescription(command={"run"}, arguments={"script"})
	@AutoComplete(completors={FileNameCompletor.class})
	public RunCommand(String script) {
		this.script = script;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		
		GricliCommandFactory f = env.getCommandFactory();
		ArrayList<GricliCommand> cl = new ArrayList<GricliCommand>();
		
		try {
			GricliTokenizer tokenizer = new GricliTokenizer(new FileInputStream(script));
			String[] tokens;
				while ((tokens = tokenizer.nextCommand()).length > 0){
					cl.add(f.create(tokens));
				}
		} catch (FileNotFoundException e) {
			throw new GricliRuntimeException("file " + script + " not found ");
		} catch (IOException e){
			throw new GricliRuntimeException("IO error while reading " + script);
		} catch (SyntaxException e){
			throw new GricliRuntimeException("error during parsing of " + script + ": " + e.getMessage() );
		}
		return new CompositeCommand(cl.toArray(new GricliCommand[] {})).execute(env);
	}

}
