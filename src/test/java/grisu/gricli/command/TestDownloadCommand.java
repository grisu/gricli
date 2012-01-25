package grisu.gricli.command;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import grisu.gricli.GricliRuntimeException;

import org.junit.Test;

public class TestDownloadCommand {

	@Test
	public void testTildeExpansion() throws GricliRuntimeException {

		String dir = DownloadJobCommand.calculateTargetDir("~/testdir", "~");

		assertThat(dir, equalTo(System.getProperty("user.home") + "/testdir"));

	}

}
