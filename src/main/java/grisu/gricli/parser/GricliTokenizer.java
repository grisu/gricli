package grisu.gricli.parser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;

import org.apache.commons.lang.StringEscapeUtils;

public class GricliTokenizer {
	
	private int lineNumber = 0;
	
	public int getLineNumber(){
		return this.lineNumber;
	}

	public static String[] tokenize(String str) {

		if (str == null) {
			return null;
		}

		StreamTokenizer st = new StreamTokenizer(
				new BufferedReader(new InputStreamReader(
						new ByteArrayInputStream(str.getBytes()))));
		st.resetSyntax();
		st.quoteChar('"');
		st.quoteChar('\'');
		st.wordChars('a', 'z');
		st.wordChars('A', 'Z');
		st.wordChars('0', '9');
		st.wordChars('=', '=');
		st.wordChars('-', '-');
		st.wordChars('/', '/');
		st.wordChars(':', ':');
		st.wordChars('@', '@');
		st.wordChars('_', '_');
		st.wordChars('.', '.');
		st.wordChars('*', '*');
		st.wordChars('?', '?');
		st.wordChars('#', '#');
		st.wordChars('$', '$');
		st.wordChars('&', '&');
		st.wordChars('~', '~');
		st.whitespaceChars(' ', ' ');

		ArrayList<String> argumentList = new ArrayList<String>();
		try {
			while (st.nextToken() != StreamTokenizer.TT_EOF) {
				String arg = (st.sval == null) ? st.nval + " " : st.sval;
				argumentList.add(st.sval);
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		return argumentList.toArray(new String[] {});

	}

	private final InputStream in;

	public GricliTokenizer(InputStream in) {
		this.in = in;
	}
	
	public static String escape(String s){
		if (s == null){
			return null;
		} else {
			if (s.contains(" ") || s.length() == 0){
				return "\"" + StringEscapeUtils.escapeJava(s) + "\"";
			} else {
				return s;
			}
		}
	}

	public String[] nextCommand() throws IOException {

		StringBuffer command = new StringBuffer();
		int c;
		c = in.read();
		if (c == -1) {
			return null;
		}
		while (c != -1) {
			if ((c != '\r') && (c != '\n') && (c != ';')) {
				command.append((char) c);
			} else {
				if (c != ';'){
					this.lineNumber++;
				}
				return tokenize(command.toString());
			}
			c = in.read();
		}

		return tokenize(command.toString());
	}
}
