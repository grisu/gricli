package grisu.gricli;

import static org.junit.Assert.*;
import grisu.gricli.command.AddCommand;
import grisu.gricli.command.AttachCommand;
import grisu.gricli.command.ChdirCommand;
import grisu.gricli.command.GricliCommandFactory;
import grisu.gricli.command.RunCommand;
import grisu.gricli.command.SetCommand;
import grisu.gricli.command.SubmitCommand;
import grisu.gricli.environment.GricliEnvironment;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestCommands {

	GricliEnvironment env;
	GricliCommandFactory f;

	@Rule
	public TemporaryFolder folder= new TemporaryFolder();

	@Before
	public void setUp(){
		env = new GricliEnvironment();
	}

	@Test(expected=GricliRuntimeException.class)
	@Ignore
	public void testAddError() throws Exception{
		AddCommand c = new AddCommand("x","y");
		c.execute(env);
	}

	@Test
	public void testAttachAbsolutePath() throws Exception {
		String filename = "testAttachAbsolutePath";
		File f = folder.newFile(filename);
		AttachCommand attach = new AttachCommand(new String[] {f.getAbsolutePath()});
		attach.execute(env);
		assertEquals(env.files.get().get(0),f.getAbsolutePath());
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


	@Test
	public void testAttachNothingAfterSomething() throws Exception {
		String filename = "testAttachNothingAfterSomething";
		File f = folder.newFile(filename);

		AttachCommand attach1 = new AttachCommand(new String[] {f.getAbsolutePath()});
		AttachCommand attach2 = new AttachCommand(new String[] {});

		attach2.execute(attach1.execute(env));
		
		assertEquals(env.files.get().size(), 1);

	}


	@Test
	public void testAttachTwoFiles() throws Exception {
		String filename1 = "testAttachTwoFiles1";
		String filename2 = "testAttachTwoFiles2";

		File f1 = folder.newFile(filename1);
		File f2 = folder.newFile(filename2);

		AttachCommand attach = new AttachCommand(new String[] {f1.getAbsolutePath(), f2.getAbsolutePath()});
		attach.execute(env);

		assertEquals(env.files.get().get(0),f1.getAbsolutePath());
		assertEquals(env.files.get().get(1),f2.getAbsolutePath());
	}

	@Test
	public void testAttachWithTilda() throws Exception{
		String filename = "testAttachWithTilda";
		String path = System.getProperty("user.home") + System.getProperty("file.separator") + filename;
		AttachCommand attach = new AttachCommand(new String[] {"~" + System.getProperty("file.separator") + filename});
		boolean fileExists = false;
		File testfile = new File(path);
		try {
			fileExists = testfile.exists();
			if (!fileExists){
				FileUtils.touch(new File(path));
				assertTrue(testfile.exists());
			}
			attach.execute(env);
			assertEquals(env.files.get().get(0), path);

		} catch (IOException e) {

		} finally {
			if (!fileExists){
				testfile.delete();
			}
		}
	}


	@Test
	/**
	 * @author Sina Masoud-Ansari
	 *
	 * Made this as testAttachWithTilda was not working as expected
	 * (has been fixed now)
	 */
	public void testAttachWithTilda_2() throws Exception {
		String rand = ""+(int)(Math.random()*10000);
		String partname = File.pathSeparator + "testAttachFromHomeDir_"+rand;
		String filename = System.getProperty("user.home")+partname;
		String shortname = "~" + partname;
		File f = null;
		try {
			f = new File(filename);
			f.createNewFile();
			assertTrue(f.exists());
			AttachCommand attach = new AttachCommand(new String[] {shortname});
			attach.execute(env);
			assertEquals(env.files.get().get(0), f.getAbsolutePath());
		} catch (GricliException e) {
			System.err.println(e.getMessage());
			fail();
		} catch (IOException e) {
			//e.printStackTrace();
		} finally {
			if ((f != null) && f.exists()){
				try {
					f.delete();
				} catch (SecurityException e) {
					System.err.println("Unable to delete temp file: "+
							f.getAbsolutePath());
				}
			}
		}
	}

	@Test
	public void testCdToHomeDir() throws Exception {
		ChdirCommand cd = new ChdirCommand("~");
		cd.execute(env);

		File home = new File(System.getProperty("user.home"));
		File current = new File(System.getProperty("user.dir"));

		assertEquals(home.getCanonicalPath(),current.getCanonicalPath());
	}

	@Test
	public void testChdir() throws Exception {
		String dir = folder.getRoot().getCanonicalPath();

		ChdirCommand cd = new ChdirCommand(dir);
		cd.execute(env);

		File cFile = new File(System.getProperty("user.dir"));
		assertEquals(dir, cFile.getCanonicalPath());
	}

	@Test
	public void testRunWithEmptyLines() throws Exception {
		List<String> script = new LinkedList<String>();
		script.add("set description ''");
		script.add("");
		script.add("set jobname hello");

		File f = folder.newFile("testRun1.script");
		String scriptName = f.getCanonicalPath();
		FileUtils.writeLines(f,script);

		RunCommand c = new RunCommand(scriptName);
		c.execute(env);

		assertEquals(env.jobname.get(),"hello");
	}
	
	@Test
	public void testRunWithComments() throws Exception {
		List<String> script = new LinkedList<String>();
		script.add("# this is comment");
		File f = folder.newFile("testRun2.script");
		String scriptName = f.getCanonicalPath();
		FileUtils.writeLines(f,script);
		
		RunCommand c = new RunCommand(scriptName);
		c.execute(env);
	}

	@Test
	public void testRunWithNoEndOfLine() throws Exception {
		File f = folder.newFile("testRun3.script");
		String scriptName = f.getCanonicalPath();
		FileUtils.writeByteArrayToFile(f, "set jobname hello".getBytes());

		RunCommand c = new RunCommand(scriptName);
		c.execute(env);

		assertEquals(env.jobname.get(),"hello");
	}

	@Test
	public void testRunWithTilda() throws Exception{
		String rand = ""+(int)(Math.random()*10000);
		String partname = File.pathSeparator + "testRunWithTilda_"+rand;
		String filename = System.getProperty("user.home")+partname;
		String shortname = "~" + partname;
		File f = null;
		try {
			f = new File(filename);
			f.createNewFile();
			assertTrue(f.exists());
			RunCommand run = new RunCommand(shortname);
			run.execute(env);
		} catch (IOException ex){

		} finally {
			if ((f != null) && f.exists()){
				try {
					f.delete();
				} catch (SecurityException e) {
					System.err.println("Unable to delete temp file: "+
							f.getAbsolutePath());
				}
			}
		}
	}

	@Test
	public void testSetDirAsHome() throws Exception {
		SetCommand set = new SetCommand("dir", "~");
		set.execute(env);
		assertEquals(env.dir.toString(),"~");
	}
	
	@Test(expected=GricliSetValueException.class)
	public void testSetJobNameWithSpaces() throws Exception {
		env.jobname.set("job name with spaces");
	}
	
	// testing submit commands
	
	@Test
	public void testSimpleSubmitCmd() throws Exception {
		SubmitCommand submit = new SubmitCommand("java","-version");
		assertEquals("java -version", submit.getCommandline());
	}
	
	@Test
	public void testSubmitCmdWithTilda() throws Exception {
		SubmitCommand submit = new SubmitCommand("java","-version", "&");
		assertEquals("java -version", submit.getCommandline());
	}
	
	@Test
	public void testSubmitWithSpaces() throws Exception {
		SubmitCommand submit = new SubmitCommand("cat","file with spaces");
		assertEquals("cat \"file with spaces\"", submit.getCommandline());
	}
	
	@Test
	public void testSubmitWithQuotes() throws Exception {
		SubmitCommand submit = new SubmitCommand("crazyquotes","\"a\"");
		assertEquals("crazyquotes \"\\\"a\\\"\"", submit.getCommandline());
	}
	
	// test set and unset commands
	
	@Test
	public void testUnsetRightVar() throws Exception{
		SetCommand unset = new SetCommand("queue");
		unset.execute(env);
		assertNull(env.queue.get());
	}
	
	@Test(expected=GricliSetValueException.class)
	public void testUnsetWrongVar() throws Exception {
		SetCommand unset = new SetCommand("cpus");
		unset.execute(env);
	}
	
	@Test
	public void testUnsetHostCount() throws Exception{
		SetCommand unset = new SetCommand("hostCount");
		unset.execute(env);
		assertNull(env.queue.get());
	}
	
	@Test(expected=GricliSetValueException.class)
	public void testUnsetMemory() throws Exception {
		SetCommand unset = new SetCommand("memory");
		unset.execute(env);
	}
	
	@Test
	public void testUnsetFiles() throws Exception {
		SetCommand unset = new SetCommand("files");
		unset.execute(env);
		assertEquals(env.files.get().size(),0);
	}
	
	@Test(expected=GricliSetValueException.class)
	public void testUnsetWalltime() throws Exception {
		SetCommand unset = new SetCommand("walltime");
		unset.execute(env);
	}
	
	@Test(expected=GricliSetValueException.class)
	public void testUnsetJobname() throws Exception {
		SetCommand unset = new SetCommand("jobname");
		unset.execute(env);
	}
	
	@Test(expected=GricliSetValueException.class)
	public void testUnsetDir() throws Exception {
		SetCommand unset = new SetCommand("dir");
		unset.execute(env);
	}

}
