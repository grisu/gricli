package grisu.gricli;

import grisu.gricli.command.*;

import org.junit.*;
import static org.junit.Assert.*;

public class TestCommands {
	
	GricliEnvironment env;
	GricliCommandFactory f;

	@Before
	public void setUp(){
		f = GricliCommandFactory.getStandardFactory();
		env = new GricliEnvironment(f);
	}
	
	@Test(expected=GricliRuntimeException.class)
	public void testAddError() throws Exception{
		AddCommand c = new AddCommand("x","y");
		c.execute(env);
	}
	
}
