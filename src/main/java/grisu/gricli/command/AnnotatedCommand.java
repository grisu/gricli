package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

public class AnnotatedCommand implements
GricliCommand {

	private final GricliCommand c;
	private final String annotation;

	public AnnotatedCommand(String annotation, GricliCommand c){
		this.c = c;
		this.annotation = annotation;
		
	}

	public GricliEnvironment execute(GricliEnvironment env)
	throws GricliRuntimeException {
		try {
			return this.c.execute(env);
		} catch (GricliRuntimeException e){
			throw new GricliRuntimeException(annotation + " " + e.getMessage());
		}
	}

}