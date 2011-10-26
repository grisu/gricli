package grisu.gricli.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import grisu.gricli.NotEnoughArgumentsException;
import grisu.gricli.SyntaxException;
import grisu.gricli.TooManyArgumentsException;
import grisu.gricli.UnknownCommandException;
import grisu.gricli.command.TestLanguage.Args1Command;
import grisu.gricli.command.TestLanguage.Args2Command;
import grisu.gricli.command.TestLanguage.InconsistentCommand;
import grisu.gricli.command.TestLanguage.MultiCommand;
import grisu.gricli.command.TestLanguage.SimpleCommand;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class TestCompile {

	GricliCommandFactory empty, trivial, simple, multi, language1, args2;

	@Before
	public void setUp() throws Exception {
		empty = GricliCommandFactory.getCustomFactory(NopCommand.class);
		trivial = GricliCommandFactory.getCustomFactory(SimpleCommand.class);
		simple = GricliCommandFactory.getCustomFactory(Args1Command.class);
		multi = GricliCommandFactory.getCustomFactory(MultiCommand.class);

		language1 = GricliCommandFactory.getCustomFactory(SimpleCommand.class,
				Args1Command.class, Args2Command.class, MultiCommand.class);

		args2 = GricliCommandFactory.getCustomFactory(Args2Command.class);
	}

	@Test(expected = NotEnoughArgumentsException.class)
	public void test0ArgumentsWhen1Excepted() throws Exception {
		language1.create(new String[] { "arg1" });
	}

	@Test(expected = TooManyArgumentsException.class)
	public void test1ArgumentWhen0Expected() throws Exception {
		language1.create(new String[] { "a", "b" });
	}

	@Test(expected = TooManyArgumentsException.class)
	public void test2ArgumentWhen1Expected() throws Exception {
		language1.create(new String[] { "arg1", "a", "b" });
	}

	@Test
	public void testArgs1() throws Exception {
		simple.create(new String[] { "arg1", "one" });
	}

	// test arguments

	@Test(expected = SyntaxException.class)
	public void testArgs1error0() throws Exception {
		simple.create(new String[] { "arg1" });
	}

	@Test(expected = SyntaxException.class)
	public void testArgs1error2() throws Exception {
		simple.create(new String[] { "arg1", "one", "two" });
	}

	// test multiple keywords
	@Test
	public void testArgs2() throws Exception {
		args2.create(new String[] { "c1", "c2", "one" });
	}

	// test command with multiple constructors

	@Test(expected = SyntaxException.class)
	public void testArgs2error0() throws Exception {
		args2.create(new String[] { "c1", "c2" });
	}

	// test when multiple commands available

	@Test
	public void testCreateSimple() throws Exception {
		trivial.create(new String[] { "a" });
	}

	@Test
	public void testEmpty() throws Exception {
		final NopCommand nop = (NopCommand) empty.create(new String[] {});
	}

	// test inconsistent language
	@Test(expected = CompileException.class)
	public void testInconsistent() throws Exception {
		GricliCommandFactory.getCustomFactory(InconsistentCommand.class);
	}

	@Test
	public void testLanguage1() throws Exception {
		assertEquals(SimpleCommand.class, language1
				.create(new String[] { "a" }).getClass());
		assertEquals(Args1Command.class,
				language1.create(new String[] { "arg1", "one" }).getClass());
		assertEquals(MultiCommand.class,
				language1.create(new String[] { "multi" }).getClass());
		assertEquals(MultiCommand.class,
				language1.create(new String[] { "multi", "one" }).getClass());
		assertEquals(MultiCommand.class,
				language1.create(new String[] { "multi", "one", "two" })
						.getClass());
	}

	@Test
	public void testMulti() throws Exception {
		MultiCommand m;
		m = (MultiCommand) multi.create(new String[] { "multi" });
		assertNull(m.one);
		assertNull(m.two);

		m = (MultiCommand) multi.create(new String[] { "multi", "one" });
		assertEquals(m.one, "one");
		assertNull(m.two);

		m = (MultiCommand) multi.create(new String[] { "multi", "one", "two" });
		assertEquals(m.one, "one");
		assertEquals(m.two, "two");
	}

	@Test(expected = UnknownCommandException.class)
	public void testNotEnoughKeywords() throws Exception {
		language1.create(new String[] { "c1" });
	}

	@Test(expected = SyntaxException.class)
	public void testSintaxError() throws Exception {
		trivial.create(new String[] { "a", "b" });
	}

	// test invalid commands
	@Test(expected = UnknownCommandException.class)
	public void testUnknownCommand() throws Exception {
		trivial.create(new String[] { "b" });
	}

	@Test(expected = UnknownCommandException.class)
	public void testUnknownCommandEmpty() throws Exception {
		trivial.create(new String[] { "" });
	}

}
