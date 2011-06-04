package grisu.gricli.parser;

import static org.junit.Assert.*;
import static grisu.gricli.parser.GricliTokenizer.*;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

public class TestParsing {
	
	private GricliTokenizer tokenizer;

	private GricliTokenizer str2Tokenizer(String s){
		return new GricliTokenizer(IOUtils.toInputStream(s));
	}

	@Test
	public void testNextCommandSingle() throws IOException{
		assertArrayEquals(str2Tokenizer("aaa bbb").nextCommand(),
				new String[] {"aaa","bbb"});
	}
	
	@Test
	public void testNextCommandLines() throws IOException{
		tokenizer = str2Tokenizer("aaa aaa\nbbb bbb");
		assertArrayEquals(tokenizer.nextCommand(),
				new String[] {"aaa","aaa"});
		assertArrayEquals(tokenizer.nextCommand(),
				new String[] {"bbb","bbb"});
		
		tokenizer = str2Tokenizer("aaa aaa\n\nbbb bbb");
		assertArrayEquals(tokenizer.nextCommand(),
				new String[] {"aaa","aaa"});
		assertArrayEquals(tokenizer.nextCommand(),
				new String[] {});
	}
	
	@Test
	public void testNextCommandSemicolon() throws IOException{
		tokenizer = str2Tokenizer("aaa aaa; bbb bbb");
		assertArrayEquals(tokenizer.nextCommand(),
				new String[] {"aaa","aaa"});	
		assertArrayEquals(tokenizer.nextCommand(),
				new String[] {"bbb","bbb"});
	}
	
	@Test
	public void testNextCommandMultiple() throws IOException{
		tokenizer = str2Tokenizer("aaa aaa; bbb bbb\nccc");
		assertArrayEquals(tokenizer.nextCommand(),
				new String[] {"aaa","aaa"});	
		assertArrayEquals(tokenizer.nextCommand(),
				new String[] {"bbb","bbb"});
		assertArrayEquals(tokenizer.nextCommand(),
				new String[] {"ccc"});
	}

	@Test
	public void testTokenizeNull() {
		assertNull(tokenize(null));
	}
	
	@Test
	public void testTokenizeEmpty(){
		assertArrayEquals(tokenize(""),new String[] {});
	}
	
	@Test
	public void testTokenizeSingle(){
		assertArrayEquals(tokenize("a"),new String[] {"a"});
		assertArrayEquals(tokenize("abc"),new String[] {"abc"});
		assertArrayEquals(tokenize("a#b"),new String[] {"a#b"});
		assertArrayEquals(tokenize("\"a\""),new String[] {"a"});
	}
	
	@Test
	public void testTokenizerMultiple(){
		assertArrayEquals(tokenize("a b"),new String[] {"a", "b"});
		assertArrayEquals(tokenize("aa bb"),new String[] {"aa", "bb"});
		assertArrayEquals(tokenize("aa bb \"cc\""),new String[] {"aa", "bb","cc"});
		assertArrayEquals(tokenize("aa bb c#c"),new String[] {"aa", "bb","c#c"});
	}
	
	@Test
	@Ignore("not implemented")
	public void testBrokenSingle(){
		tokenize("\"");
	}

}
