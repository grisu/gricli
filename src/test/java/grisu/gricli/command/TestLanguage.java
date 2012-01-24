package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

import org.junit.Test;

public class TestLanguage {

	public static class Args1Command extends ErrorCommand {
		@SyntaxDescription(command = { "arg1" }, arguments = { "one" })
		public Args1Command(String one) {
		}
	}

	public static class Args2Command extends ErrorCommand {
		@SyntaxDescription(command = { "c1", "c2" }, arguments = { "one" })
		public Args2Command(String one) {
		}
	}

	public static class ErrorCommand implements GricliCommand {
		public void execute(GricliEnvironment env)
				throws GricliRuntimeException {
			throw new GricliRuntimeException("error");
		}
	}

	public static class InconsistentCommand extends ErrorCommand {
		@SyntaxDescription(command = { "c1", "c2" })
		public InconsistentCommand() {
		}

		@SyntaxDescription(command = { "c1" }, arguments = { "one" })
		public InconsistentCommand(String one) {
		}
	}

	public static class MultiCommand extends ErrorCommand {

		public String one, two;

		@SyntaxDescription(command = { "multi" })
		public MultiCommand() {
		}

		@SyntaxDescription(command = { "multi" }, arguments = { "one" })
		public MultiCommand(String one) {
			this.one = one;
		}

		@SyntaxDescription(command = { "multi" }, arguments = { "one", "two" })
		public MultiCommand(String one, String two) {
			this.one = one;
			this.two = two;
		}

	}

	public static class SimpleCommand extends ErrorCommand {
		@SyntaxDescription(command = { "a" }, arguments = {})
		public SimpleCommand() {
		}
	}

	public static class VarArgsCommand extends ErrorCommand {

		@SyntaxDescription(command = { "var" }, arguments = { "varargs" })
		public VarArgsCommand(String... strings) {
		}
	}

	@Test
	public void success() {

	}

}
