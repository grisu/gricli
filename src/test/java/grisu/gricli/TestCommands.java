package grisu.gricli;

import java.io.File;
import java.io.IOException;

import grisu.gricli.command.*;

import org.apache.commons.io.FileUtils;
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
	
	@Test
	public void testAttachWithTilda() throws Exception{
		String filename = "~/test";
		AttachCommand attach = new AttachCommand(new String[] {filename});
		boolean fileExists = false;
		File testfile = new File(filename);
		try {		
			fileExists = testfile.exists();
			if (!fileExists){
				FileUtils.touch(new File(filename));
			} 
		} catch (IOException e) {
		} finally {
			attach.execute(env);
			if (!fileExists){
				testfile.delete();
			}
		}
		assertTrue(env.getList("files").size() > 0);
	}
	
	@Test(expected=GricliRuntimeException.class)
	public void testAttachNonExistent() throws Exception{
		String filename = "/tmp/a";
		while (new File(filename).exists()){
			filename += "a";
		}
		AttachCommand attach = new AttachCommand(new String[] {filename});
		attach.execute(env);
	}
	
}
