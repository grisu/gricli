package grisu.gricli.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;

public class CommandlineTokenizer {
	
	private InputStream in;

	public CommandlineTokenizer(InputStream in){
		this.in = in;
	}
	
	public String[] nextCommand() throws IOException{
		
		StringBuffer command = new StringBuffer();
		int c;
		while ((c = in.read()) != -1){
			if (c != '\n' && c != ';'){
				command.append((char)c);
			} else {
				return tokenize(command.toString());
			}
		}
		
		in.close();
		
		return tokenize(command.toString());
	}

	public static String[] tokenize(String str) {
		StreamTokenizer st = new StreamTokenizer(
				new BufferedReader(new InputStreamReader(
						new ByteArrayInputStream(str.getBytes()))));
		st.resetSyntax();
		st.quoteChar('"');
		st.quoteChar('\'');
		st.wordChars('a', 'z');
		st.wordChars('A', 'Z');
		st.wordChars('0', '9');
		st.wordChars('-', '-');
		st.wordChars('/', '/');
		st.wordChars(':', ':');
		st.wordChars('@', '@');
		st.wordChars('_', '_');
		st.wordChars('.', '.');
		st.wordChars('*', '*');
		st.wordChars('?', '?');
		st.wordChars('#','#');
		st.wordChars('&','&');
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
}
