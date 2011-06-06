package grisu.gricli.command;

import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;

public class ErrorCommand implements GricliCommand{
	public GricliEnvironment execute(GricliEnvironment env) throws GricliRuntimeException 
	{throw new GricliRuntimeException("error");}
}
