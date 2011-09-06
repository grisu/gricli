package grisu.gricli.completors;

import grisu.frontend.view.cli.CliHelpers;

import java.util.List;

import jline.Completor;
import jline.SimpleCompletor;

public class VarValueCompletor implements Completor {

	private final QueueCompletor qc = new QueueCompletor();
	private final FqanCompletor fc = new FqanCompletor();
	private final ApplicationCompletor ac = new ApplicationCompletor();
	private final SimpleCompletor tfc = new SimpleCompletor(new String[] {
			"true", "false" });
	private final SimpleCompletor jtc = new SimpleCompletor(new String[] {
			"smp","mpi", "single" });
	private final ApplicationVersionCompletor avc = new ApplicationVersionCompletor();

	public VarValueCompletor() {
	}

	public int complete(String s, int i, List l) {

		String previous = CliHelpers.getConsoleReader().getCursorBuffer()
				.getBuffer().toString();

		if (previous.contains("queue")) {
			return qc.complete(s,  i, l);
		} else if (previous.contains("group")) {
			return fc.complete(s,  i, l);
		} else if (previous.contains("package")) {
			return ac.complete(s, i, l);
		} else if (previous.contains("debug") || previous.contains("email_on")) {
			return tfc.complete(s, i, l);
		} else if (previous.contains("jobtype")) {
			return jtc.complete(s, i, l);
		} else if (previous.contains("version")) {
			return avc.complete(s, i, l);
		} else {
			return -1;
		}
	}

}
