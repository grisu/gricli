package grisu.gricli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void setUp() {
		env = new GricliEnvironment();
	}

	@Test(expected = GricliRuntimeException.class)
	@Ignore
	public void testAddError() throws Exception {
		final AddCommand c = new AddCommand("x", "y");
		c.execute(env);
	}

	@Test
	public void testAttachAbsolutePath() throws Exception {
		final String filename = "testAttachAbsolutePath";
		final File f = folder.newFile(filename);
		final AttachCommand attach = new AttachCommand(
				new String[] { f.getAbsolutePath() });
		attach.execute(env);
		assertEquals(env.files.get().get(0), f.getAbsolutePath());
	}

	@Test(expected = GricliRuntimeException.class)
	public void testAttachNonExistent() throws Exception {
		String filename = folder.getRoot().getCanonicalPath()
				+ File.pathSeparator + "a";
		while (new File(filename).exists()) {
			filename += "a";
		}
		final AttachCommand attach = new AttachCommand(
				new String[] { filename });
		attach.execute(env);
	}

	@Test
	public void testAttachNothingAfterSomething() throws Exception {
		final String filename = "testAttachNothingAfterSomething";
		final File f = folder.newFile(filename);

		final AttachCommand attach1 = new AttachCommand(
				new String[] { f.getAbsolutePath() });
		final AttachCommand attach2 = new AttachCommand(new String[] {});

		attach1.execute(env);
		attach2.execute(env);

		assertEquals(env.files.get().size(), 1);

	}

	@Test
	public void testAttachTwoFiles() throws Exception {
		final String filename1 = "testAttachTwoFiles1";
		final String filename2 = "testAttachTwoFiles2";

		final File f1 = folder.newFile(filename1);
		final File f2 = folder.newFile(filename2);

		final AttachCommand attach = new AttachCommand(new String[] {
				f1.getAbsolutePath(), f2.getAbsolutePath() });
		attach.execute(env);

		assertEquals(env.files.get().get(0), f1.getAbsolutePath());
		assertEquals(env.files.get().get(1), f2.getAbsolutePath());
	}

	@Test
	public void testAttachWithTilda() throws Exception {
		final String filename = "testAttachWithTilda";
		final String path = System.getProperty("user.home")
				+ System.getProperty("file.separator") + filename;
		final AttachCommand attach = new AttachCommand(new String[] { "~"
				+ System.getProperty("file.separator") + filename });
		boolean fileExists = false;
		final File testfile = new File(path);
		try {
			fileExists = testfile.exists();
			if (!fileExists) {
				FileUtils.touch(new File(path));
				assertTrue(testfile.exists());
			}
			attach.execute(env);
			assertEquals(env.files.get().get(0), path);

		} catch (final IOException e) {

		} finally {
			if (!fileExists) {
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
		final String rand = "" + (int) (Math.random() * 10000);
		final String partname = File.pathSeparator + "testAttachFromHomeDir_"
				+ rand;
		final String filename = System.getProperty("user.home") + partname;
		final String shortname = "~" + partname;
		File f = null;
		try {
			f = new File(filename);
			f.createNewFile();
			assertTrue(f.exists());
			final AttachCommand attach = new AttachCommand(
					new String[] { shortname });
			attach.execute(env);
			assertEquals(env.files.get().get(0), f.getAbsolutePath());
		} catch (final GricliException e) {
			System.err.println(e.getMessage());
			fail();
		} catch (final IOException e) {
			// e.printStackTrace();
		} finally {
			if ((f != null) && f.exists()) {
				try {
					f.delete();
				} catch (final SecurityException e) {
					System.err.println("Unable to delete temp file: "
							+ f.getAbsolutePath());
				}
			}
		}
	}

	@Test
	public void testCdToHomeDir() throws Exception {
		final ChdirCommand cd = new ChdirCommand("~");
		cd.execute(env);

		final File home = new File(System.getProperty("user.home"));
		final File current = new File(System.getProperty("user.dir"));

		assertEquals(home.getCanonicalPath(), current.getCanonicalPath());
	}

	@Test
	public void testChdir() throws Exception {
		final String dir = folder.getRoot().getCanonicalPath();

		final ChdirCommand cd = new ChdirCommand(dir);
		cd.execute(env);

		final File cFile = new File(System.getProperty("user.dir"));
		assertEquals(dir, cFile.getCanonicalPath());
	}

	@Test(expected = GricliRuntimeException.class)
	public void testEmptySubmit() throws Exception {
		final SubmitCommand submit = new SubmitCommand();
		submit.execute(env);
	}

	@Test
	public void testRunWithComments() throws Exception {
		final List<String> script = new LinkedList<String>();
		script.add("# this is comment");
		final File f = folder.newFile("testRun2.script");
		final String scriptName = f.getCanonicalPath();
		FileUtils.writeLines(f, script);

		final RunCommand c = new RunCommand(scriptName);
		c.execute(env);
	}

	@Test
	public void testRunWithEmptyLines() throws Exception {
		final List<String> script = new LinkedList<String>();
		script.add("set description ''");
		script.add("");
		script.add("set jobname hello");

		final File f = folder.newFile("testRun1.script");
		final String scriptName = f.getCanonicalPath();
		FileUtils.writeLines(f, script);

		final RunCommand c = new RunCommand(scriptName);
		c.execute(env);

		assertEquals(env.jobname.get(), "hello");
	}

	@Test
	public void testRunWithNoEndOfLine() throws Exception {
		final File f = folder.newFile("testRun3.script");
		final String scriptName = f.getCanonicalPath();
		FileUtils.writeByteArrayToFile(f, "set jobname hello".getBytes());

		final RunCommand c = new RunCommand(scriptName);
		c.execute(env);

		assertEquals(env.jobname.get(), "hello");
	}

	@Test
	public void testRunWithTilda() throws Exception {
		final String rand = "" + (int) (Math.random() * 10000);
		final String partname = File.pathSeparator + "testRunWithTilda_" + rand;
		final String filename = System.getProperty("user.home") + partname;
		final String shortname = "~" + partname;
		File f = null;
		try {
			f = new File(filename);
			f.createNewFile();
			assertTrue(f.exists());
			final RunCommand run = new RunCommand(shortname);
			run.execute(env);
		} catch (final IOException ex) {

		} finally {
			if ((f != null) && f.exists()) {
				try {
					f.delete();
				} catch (final SecurityException e) {
					System.err.println("Unable to delete temp file: "
							+ f.getAbsolutePath());
				}
			}
		}
	}

	@Test
	public void testSetDirAsHome() throws Exception {
		final SetCommand set = new SetCommand("dir", "~");
		set.execute(env);
		assertEquals(env.dir.toString(), "~");
	}

	// testing submit commands

	@Test(expected = GricliSetValueException.class)
	public void testSetJobNameWithSpaces() throws Exception {
		env.jobname.set("job name with spaces");
	}

	@Test
	public void testSimpleSubmitCmd() throws Exception {
		final SubmitCommand submit = new SubmitCommand("java", "-version");
		assertEquals("java -version", submit.getCommandline());
	}

	@Test
	public void testSubmitCmdWithTilda() throws Exception {
		final SubmitCommand submit = new SubmitCommand("java", "-version", "&");
		assertEquals("java -version", submit.getCommandline());
	}

	@Test
	public void testSubmitWithQuotes() throws Exception {
		final SubmitCommand submit = new SubmitCommand("crazyquotes", "\"a\"");
		assertEquals("crazyquotes \"\\\"a\\\"\"", submit.getCommandline());
	}

	@Test
	public void testSubmitWithSlashes() {
		final SubmitCommand submit = new SubmitCommand("/bin/bash", "-c", "pwd");
		assertEquals(submit.getCommandline(), "/bin/bash -c pwd");
	}

	@Test
	public void testSubmitWithSpaces() throws Exception {
		final SubmitCommand submit = new SubmitCommand("cat",
				"file with spaces");
		assertEquals("cat \"file with spaces\"", submit.getCommandline());
	}

	// test set and unset commands

	@Test(expected = GricliSetValueException.class)
	public void testUnsetCpus() throws Exception {
		final SetCommand unset = new SetCommand("cpus");
		unset.execute(env);
	}

	@Test(expected = GricliSetValueException.class)
	public void testUnsetDir() throws Exception {
		final SetCommand unset = new SetCommand("dir");
		unset.execute(env);
	}

	@Test
	public void testUnsetEnv() throws Exception {
		final SetCommand unset = new SetCommand("env");
		unset.execute(env);
		assertEquals(env.env.get().size(), 0);
	}

	@Test
	public void testUnsetFiles() throws Exception {
		final SetCommand unset = new SetCommand("files");
		unset.execute(env);
		assertEquals(env.files.get().size(), 0);
	}

	@Test
	public void testUnsetHostCount() throws Exception {
		final SetCommand unset = new SetCommand("hostcount");
		unset.execute(env);
		assertNull(env.queue.get());
	}

	@Test(expected = GricliSetValueException.class)
	public void testUnsetJobname() throws Exception {
		final SetCommand unset = new SetCommand("jobname");
		unset.execute(env);
	}

	@Test(expected = GricliSetValueException.class)
	public void testUnsetMemory() throws Exception {
		final SetCommand unset = new SetCommand("memory");
		unset.execute(env);
	}

	@Test
	public void testUnsetRightVar() throws Exception {
		final SetCommand unset = new SetCommand("queue");
		unset.execute(env);
		assertNull(env.queue.get());
	}

	@Test(expected = GricliSetValueException.class)
	public void testUnsetWalltime() throws Exception {
		final SetCommand unset = new SetCommand("walltime");
		unset.execute(env);
	}

	@Test(expected = GricliSetValueException.class)
	public void testUnsetWrongVar() throws Exception {
		final SetCommand unset = new SetCommand("cpus");
		unset.execute(env);
	}

}
