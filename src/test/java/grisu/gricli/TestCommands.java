package grisu.gricli;

import java.io.File;
import java.io.IOException;

import grisu.gricli.command.*;

import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

public class TestCommands {
	
	GricliEnvironment env;
	GricliCommandFactory f;
	
    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

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
		String filename = "testAttachWithTilda";
		AttachCommand attach = new AttachCommand(new String[] {"~" + File.pathSeparator + filename});
		boolean fileExists = false;
		File testfile = new File(System.getProperty("home.dir") + File.pathSeparator + filename);
		try {		
			fileExists = testfile.exists();
			if (!fileExists){
				FileUtils.touch(new File(filename));
			} 
			attach.execute(env);
			assertTrue(env.getList("files").size() > 0);
			
		} catch (IOException e) {
			
		} finally {
			/* if (!fileExists){
				testfile.delete();
			} */
		}
	}
	
	@Test
	public void testAttachAbsolutePath() throws Exception {
		String filename = "testAttachAbsolutePath";
		File f = folder.newFile(filename);
		AttachCommand attach = new AttachCommand(new String[] {f.getAbsolutePath()});
		attach.execute(env);
		assertEquals(env.getList("files").get(0),f.getAbsolutePath());
	}
	
	@Test
	public void testAttachTwoFiles() throws Exception {
		String filename1 = "testAttachTwoFiles1";
		String filename2 = "testAttachTwoFiles2";
		
		File f1 = folder.newFile(filename1);
		File f2 = folder.newFile(filename2);
		
		AttachCommand attach = new AttachCommand(new String[] {f1.getAbsolutePath(), f2.getAbsolutePath()});
		attach.execute(env);
		
		assertEquals(env.getList("files").get(0),f1.getAbsolutePath());
		assertEquals(env.getList("files").get(1),f2.getAbsolutePath());
	}
	
	@Test
	public void testAttachNothingAfterSomething() throws Exception {
		String filename = "testAttachNothingAfterSomething";
		File f = folder.newFile(filename);
		
		AttachCommand attach1 = new AttachCommand(new String[] {f.getAbsolutePath()});
		AttachCommand attach2 = new AttachCommand(new String[] {});
		
		attach2.execute(attach1.execute(env));
		
		assertEquals(env.getList("files").size(),0);
	}
	
	@Test(expected=GricliRuntimeException.class)
	public void testAttachNonExistent() throws Exception{
		String filename = folder.getRoot().getCanonicalPath() + File.pathSeparator + "a";
		while (new File(filename).exists()){
			filename += "a";
		}
		AttachCommand attach = new AttachCommand(new String[] {filename});
		attach.execute(env);
	}
	
}
