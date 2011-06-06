package grisu.gricli.command;


import static org.junit.Assert.*;
import grisu.gricli.SyntaxException;
import grisu.gricli.UnknownCommandException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class TestCompile {
	
	GricliCommandFactory trivial,simple,multi,language1,args2;
	
	@Before 
	public void setUp(){
		trivial = GricliCommandFactory.getCustomFactory(SimpleCommand.class);
		simple = GricliCommandFactory.getCustomFactory(Args1Command.class);
		multi = GricliCommandFactory.getCustomFactory(MultiCommand.class);
		
		language1 = GricliCommandFactory.getCustomFactory(SimpleCommand.class, 
				Args1Command.class, MultiCommand.class);
		
		args2 = GricliCommandFactory.getCustomFactory(Args2Command.class);
	}
	
	@Test
	public void testCreateSimple() throws Exception{
		trivial.create(new String[] {"a"});
	}
	
	@Test(expected=SyntaxException.class)
	public void testSintaxError() throws Exception {
		trivial.create(new String[] {"a","b"});
	}
	
	@Test(expected=UnknownCommandException.class)	
	public void testUnknownCommand() throws Exception {
		trivial.create(new String[] {"b"});
	}
	
	@Test(expected=UnknownCommandException.class)	
	public void testUnknownCommandEmpty() throws Exception {
		trivial.create(new String[] {""});
	}
	
	// test arguments
	
	@Test
	public void testArgs1() throws Exception{
		simple.create(new String[] {"arg1","one"});
	}
	
	@Test(expected=SyntaxException.class)
	public void testArgs1error2() throws Exception{
		simple.create(new String[] {"arg1","one","two"});
	}
	
	@Test(expected=SyntaxException.class)
	public void testArgs1error0() throws Exception{
		simple.create(new String[] {"arg1"});
	}
	
	// test command with multiple constructors
	
	@Test
	public void testMulti() throws Exception{
		MultiCommand m ; 
		m = (MultiCommand)multi.create(new String[] {"multi"});
		assertNull(m.one);
		assertNull(m.two);
		
		m = (MultiCommand)multi.create(new String[] {"multi","one"});
		assertEquals(m.one,"one");
		assertNull(m.two);
		
		m = (MultiCommand)multi.create(new String[] {"multi","one","two"});
		assertEquals(m.one,"one");
		assertEquals(m.two,"two");
	}
	
	// test when multiple commands available
	
	@Test
	public void testLanguage1() throws Exception {
		assertEquals(SimpleCommand.class, 
				language1.create(new String[] {"a"}).getClass());
		assertEquals(Args1Command.class, 
				language1.create(new String[] {"arg1","one"}).getClass());
		assertEquals(MultiCommand.class, 
				language1.create(new String[] {"multi"}).getClass());
		assertEquals(MultiCommand.class, 
				language1.create(new String[] {"multi","one"}).getClass());
		assertEquals(MultiCommand.class, 
				language1.create(new String[] {"multi","one","two"}).getClass());
	}
	
	// test multiple keywords
	@Test
	public void testArgs2() throws Exception{
		args2.create(new String[] {"c1","c2","one"});
	}
	
	@Test(expected=SyntaxException.class)
	public void testArgs2error0() throws Exception{
		args2.create(new String[] {"c1","c2"});
	}
	
}
