package grisu.gricli.parser;

import static grisu.gricli.parser.GricliTokenizer.tokenize;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class TestParsing {

	private GricliTokenizer tokenizer;

	private GricliTokenizer str2Tokenizer(String s) {
		return new GricliTokenizer(IOUtils.toInputStream(s));
	}

	@Test
	public void testEmptyLineEscape() {
		assertEquals("\"\"", GricliTokenizer.escape(""));
	}

	@Test
	public void testEquals() throws Exception {
		tokenizer = str2Tokenizer("a x=y\n");
		final String[] ts = tokenizer.nextCommand();
		assertArrayEquals(ts, new String[] { "a", "x=y" });
	}

	// test line numbers
	@Test
	public void testFirstLine() throws Exception {
		tokenizer = str2Tokenizer("aaa bbb");
		assertEquals(tokenizer.getLineNumber(), 0);
		tokenizer.nextCommand();
		assertEquals(tokenizer.getLineNumber(), 0);
	}

	@Test
	public void testNextCommandLines() throws IOException {
		tokenizer = str2Tokenizer("aaa aaa\nbbb bbb");
		assertArrayEquals(tokenizer.nextCommand(),
				new String[] { "aaa", "aaa" });
		assertArrayEquals(tokenizer.nextCommand(),
				new String[] { "bbb", "bbb" });

		tokenizer = str2Tokenizer("aaa aaa\n\nbbb bbb");
		assertArrayEquals(tokenizer.nextCommand(),
				new String[] { "aaa", "aaa" });
		assertArrayEquals(tokenizer.nextCommand(), new String[] {});
	}

	@Test
	public void testNextCommandMultiple() throws IOException {
		tokenizer = str2Tokenizer("aaa aaa; bbb bbb\nccc");
		assertArrayEquals(tokenizer.nextCommand(),
				new String[] { "aaa", "aaa" });
		assertArrayEquals(tokenizer.nextCommand(),
				new String[] { "bbb", "bbb" });
		assertArrayEquals(tokenizer.nextCommand(), new String[] { "ccc" });
	}

	@Test
	public void testNextCommandSemicolon() throws IOException {
		tokenizer = str2Tokenizer("aaa aaa; bbb bbb");
		assertArrayEquals(tokenizer.nextCommand(),
				new String[] { "aaa", "aaa" });
		assertArrayEquals(tokenizer.nextCommand(),
				new String[] { "bbb", "bbb" });
	}

	@Test
	public void testNextCommandSingle() throws IOException {
		assertArrayEquals(str2Tokenizer("aaa bbb").nextCommand(), new String[] {
				"aaa", "bbb" });
	}

	// test escape
	@Test
	public void testNullEscape() {
		assertNull(GricliTokenizer.escape(null));
	}

	@Test
	public void testSecondLine() throws Exception {
		tokenizer = str2Tokenizer("aaa bbb\n");
		tokenizer.nextCommand();
		assertEquals(tokenizer.getLineNumber(), 1);
	}

	@Test
	public void testSpaceEscape() {
		assertEquals("\"a b\"", GricliTokenizer.escape("a b"));
	}

	@Test
	public void testThirdLine() throws Exception {
		tokenizer = str2Tokenizer("aaa bbb\naaa bbb\n");
		tokenizer.nextCommand();
		tokenizer.nextCommand();
		assertEquals(tokenizer.getLineNumber(), 2);
	}

	@Test
	public void testTokenizeEmpty() {
		assertArrayEquals(tokenize(""), new String[] {});
	}

	@Test
	public void testTokenizeNull() {
		assertNull(tokenize(null));
	}

	@Test
	public void testTokenizerMultiple() {
		assertArrayEquals(tokenize("a b"), new String[] { "a", "b" });
		assertArrayEquals(tokenize("aa bb"), new String[] { "aa", "bb" });
		assertArrayEquals(tokenize("aa bb \"cc\""), new String[] { "aa", "bb",
				"cc" });
		assertArrayEquals(tokenize("aa bb c#c"), new String[] { "aa", "bb",
				"c#c" });
	}

	// test special characters
	@Test
	public void testTokenizerTilda() {
		assertArrayEquals(tokenize("attach ~/test"), new String[] { "attach",
				"~/test" });
	}

	@Test
	public void testTokenizeSingle() {
		assertArrayEquals(tokenize("a"), new String[] { "a" });
		assertArrayEquals(tokenize("abc"), new String[] { "abc" });
		assertArrayEquals(tokenize("a#b"), new String[] { "a#b" });
		assertArrayEquals(tokenize("\"a\""), new String[] { "a" });
	}

	@Test
	public void testTrivialEscape() {
		assertEquals("a", GricliTokenizer.escape("a"));
	}

}
