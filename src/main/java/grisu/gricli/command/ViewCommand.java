
package grisu.gricli.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import java.awt.event.KeyEvent;
import jline.Terminal;

import org.apache.commons.io.FileUtils;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

public class ViewCommand implements GricliCommand {

	private String filename;

	@SyntaxDescription(command = { "view" },arguments={"filename"})
	public ViewCommand(String filename) {
		this.filename = filename;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		try {
			 BufferedReader in
			   = new BufferedReader(new FileReader(this.filename));
			 
			Terminal t = Terminal.getTerminal();
			int h = t.getTerminalHeight();
			int w = t.getTerminalWidth();
			
			String line = "";		
			
			int ch = 0;
			int c = 0;
			
			while ((line = in.readLine()) != null){
				
				if (ch >= h){
					while (true){
						
						c = t.readVirtualKey(System.in);
						if (c == jline.UnixTerminal.ARROW_DOWN){
							break;
						} else if (c == jline.UnixTerminal.END_CODE){
							ch = 0;
							break;
						} else {
							System.out.println(c);
						}
						
					}
					c = 0;
					
				} else {
					ch++;
				}
				env.printMessage(line.replaceAll("\\p{Cntrl}", "?"));
				
			}
			
			
			return env;
		} catch (IOException ex){
			throw new GricliRuntimeException("file " + this.filename + "not found");
		}
	}

}
