package grisu.gricli.command;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;

import grisu.gricli.SyntaxException;

import org.junit.Before;
import org.junit.Test;

public class TestCommandCreators {
	
	CommandCreator c;
	Constructor<? extends GricliCommand> cons1,cons2,cons3,cons4;
	
	@Before
	public void setUp(){
		c = new CommandCreator();
		
		cons1 = (Constructor<? extends GricliCommand>) SimpleCommand.class.getConstructors()[0];
		cons2 = (Constructor<? extends GricliCommand>) Args1Command.class.getConstructors()[0];
		cons3 = (Constructor<? extends GricliCommand>) VarArgsCommand.class.getConstructors()[0];
	}

	@Test(expected=SyntaxException.class)
	public void testEmpty() throws Exception{
		c.create(new String[] {});
	}
	
	@Test
	public void testSimpleKeyword() throws Exception {
		c.addKeyword("print");

	}
		
	@Test
	public void testDoubleKeyword() throws Exception {
		c.addKeyword("print").addKeyword("globals");
		
	}
	
	@Test
	public void testArgs() throws Exception {
		c.addKeyword("gls").addArgument("filelist");
	}
	
	@Test
	public void testSimpleCommand() throws Exception {
		c.addKeyword("a").addConstructor(cons1);
		GricliCommand command = c.create(new String[] {"a"});
		assertEquals(SimpleCommand.class,command.getClass());
	}
	
	@Test(expected=SyntaxException.class)
	public void testWrongNumberOfArguments() throws Exception {
		c.addKeyword("a").addArgument("one").addConstructor(cons2);
		c.create(new String[] {"a","b","c"});
	}
	
	@Test
	public void testTwoCommands() throws Exception {		
		CommandCreator c1 = c.addKeyword("a").addConstructor(cons1);
		CommandCreator c2 = c.addKeyword("arg1").addArgument("one").addConstructor(cons2);
		
		assertEquals(SimpleCommand.class,c.create(new String[] {"a"}).getClass());
		assertEquals(Args1Command.class,c.create(new String[] {"arg1","1"}).getClass());
		assertEquals(Args1Command.class,c.create(new String[] {"arg1","2"}).getClass());
	}
	
	@Test(expected=SyntaxException.class)
	public void testWrongCommandCall() throws Exception {
		 c.addKeyword("a").addConstructor(cons1);
		 c.create(new String[] {"b"});
	}
	
	@Test(expected=CompileException.class)
	public void testInvalidLanguage() throws Exception{
		c.addKeyword("a").addKeyword("b");
		c.addKeyword("a").addArgument("files");
	}
	
	@Test
	public void testVarArgs() throws Exception {
		c.addKeyword("var").addVarArg("files").addConstructor(cons3);
		assertEquals(VarArgsCommand.class, c.create(new String[] {"var","file1","file2"}).getClass());
	}
	
	
}
