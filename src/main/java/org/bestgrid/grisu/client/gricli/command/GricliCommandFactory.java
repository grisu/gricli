package org.bestgrid.grisu.client.gricli.command;

import java.util.ArrayList;
import java.util.HashMap;
import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.InvalidCommandException;
import org.bestgrid.grisu.client.gricli.SyntaxException;
import org.bestgrid.grisu.client.gricli.UnknownCommandException;

import jline.ArgumentCompletor;
import jline.SimpleCompletor;
import jline.Completor;
import jline.NullCompletor;
import org.apache.commons.lang.StringUtils;
import org.bestgrid.grisu.client.gricli.GricliRuntimeException;

public class GricliCommandFactory {

	private HashMap<String, CommandCreator> creatorMap = new HashMap<String, CommandCreator>();

	/*
	 * simple auto-completion based on keywords
	 */
	public Completor createCompletor() {
		HashMap<String, CommandCreator> tempMap = creatorMap;
		ArrayList<ChainedCreator> creators = new ArrayList<ChainedCreator>();
		ArrayList<ChainedCreator> creators2 = new ArrayList<ChainedCreator>();
		ArrayList<Completor> completors = new ArrayList<Completor>();
		ArrayList<String> keywords = new ArrayList<String>();

		for (String keyword : tempMap.keySet()) {
			keywords.add(keyword);
			CommandCreator cr = tempMap.get(keyword);
			if (cr.getClass() == ChainedCreator.class) {
				creators.add((ChainedCreator) cr);
			}

		}

		completors.add(new SimpleCompletor(keywords.toArray(new String[] {})));

		creators2 = (ArrayList<ChainedCreator>) creators.clone();
		while (creators2.size() > 0) {
			keywords = new ArrayList<String>();
			creators = new ArrayList<ChainedCreator>();
			for (ChainedCreator cch : creators2) {
				tempMap = cch.getCreatorMap();
				for (String ckey : tempMap.keySet()) {
					keywords.add(ckey);
					CommandCreator cr = tempMap.get(ckey);
					if (cr.getClass() == ChainedCreator.class) {
						creators.add((ChainedCreator) cr);
					}
				}
			}
			completors.add(new SimpleCompletor(keywords
					.toArray(new String[] {})));
			creators2 = (ArrayList<ChainedCreator>) creators.clone();
		}

		completors.add(new NullCompletor());
		return new ArgumentCompletor(completors.toArray(new Completor[] {}));
	}

	public GricliCommandFactory() {
		creatorMap.put("login",
				new FixedArgsCreator(1, LocalLoginCommand.class));
		creatorMap.put("ilogin",
				fhelp(1, InteractiveLoginCommand.class, "Intractive login"));

		ChainedCreator printCreator = new ChainedCreator("print");
		printCreator.add("jobs", new AliasCreator(this, new String[] { "print",
				"job", "*", "status" }));
		printCreator.add("job", new HelpCreator(new PrintJobCreator(),
				"Usage: print job <jobname>\n Prints <jobname> details"));
		printCreator.add("queues", new FixedArgsCreator(1,
				PrintQueuesCommand.class));
		printCreator.add("hosts", new FixedArgsCreator(0,
				PrintHostsCommand.class));
		printCreator.add("globals", new FixedArgsCreator(0,
				PrintGlobalsCommand.class));
		printCreator.add(
				"apps",
				fhelp(0, PrintAppsCommand.class,
						"prints all available applications on the grid"));
		creatorMap.put("print", printCreator);

		ChainedCreator setCreator = new ChainedCreator("set");
		setCreator.add("global",
				new FixedArgsCreator(2, SetGlobalCommand.class));
		creatorMap.put("set", setCreator);

		ChainedCreator submitCreator = new ChainedCreator("submit");
		submitCreator.add("cmd",
				new FixedArgsCreator(1, SubmitCmdCommand.class));
		submitCreator.add("sweep", new FixedArgsCreator(1,
				SubmitSweepCommand.class));
		creatorMap.put("submit", submitCreator);

		ChainedCreator killCreator = new ChainedCreator("kill");
		killCreator.add("job", new KillJobCreator(false));
		creatorMap.put("kill", killCreator);

		ChainedCreator destroyCreator = new ChainedCreator("destroy");
		destroyCreator.add("job", new KillJobCreator(true));
		creatorMap.put("destroy", destroyCreator);

		ChainedCreator addCreator = new ChainedCreator("add");
		addCreator.add("global",
				new FixedArgsCreator(2, AddGlobalCommand.class));
		creatorMap.put("add", addCreator);

		ChainedCreator clearCreator = new ChainedCreator("clear");
		clearCreator.add("global", new FixedArgsCreator(1,
				ClearListCommand.class));
		creatorMap.put("clear", clearCreator);

		ChainedCreator downloadCreator = new ChainedCreator("download");
		downloadCreator.add("job", new FixedArgsCreator(1,
				DownloadJobCommand.class));
		creatorMap.put("download", downloadCreator);

		creatorMap.put(
				"gls",
				fhelp(0, GridLsCommand.class,
						"gls\n list files in current grid directory"));
		creatorMap
				.put("attach",
						fhelp(1,
								AttachCommand.class,
								"attach\n usage: attach <filelist>\n"
										+ "Attaches files to current job. <filelist> supports unix-style regular expressions"));
		creatorMap
				.put("get",
						fhelp(1, GetCommand.class,
								"get\n usage: get <filename>\n downloads file file from grid space"));
		creatorMap.put("help", new HelpCommandCreator());
		creatorMap.put("cd", new AliasCreator(this, new String[] { "set",
				"global", "dir" }));

	}

	private CommandCreator fhelp(int n, Class<? extends GricliCommand> command,
			String helpMessage) {
		return new HelpCreator(new FixedArgsCreator(n, command), helpMessage);
	}

	public GricliCommand create(String[] args) throws SyntaxException {

		if (args.length == 0) {
			throw new InvalidCommandException("empty command");
		}
		String command = args[0];
		String[] arguments = new String[args.length - 1];
		System.arraycopy(args, 1, arguments, 0, arguments.length);

		CommandCreator creator = creatorMap.get(command);
		if (creator != null) {
			return creator.create(arguments);
		} else {
			throw new UnknownCommandException(command);
		}
	}

	private abstract class CommandCreator {
		public CommandCreator() {
		};

		public abstract GricliCommand create(String[] args)
				throws SyntaxException;

		public String help() {
			return "";
		}
	}

	/*
	 * allows to implement complex commands like "set global" and "set job"
	 */
	private class ChainedCreator extends CommandCreator {

		private HashMap<String, CommandCreator> creatorMap = new HashMap<String, CommandCreator>();
		private final String commandName;

		public ChainedCreator(String commandName) {
			this.commandName = commandName;
		}

		public void add(String command, CommandCreator creator) {
			creatorMap.put(command, creator);
		}

		public HashMap<String, CommandCreator> getCreatorMap() {
			return creatorMap;
		}

		@Override
		public String help() {
			String help = "";
			for (String keyword : creatorMap.keySet()) {
				help += this.commandName + " " + keyword + ":\n";
				String subhelp = creatorMap.get(keyword).help();
				String[] lines = subhelp.split("\n");
				for (String line : lines) {
					help += "    " + line + "\n";
				}
			}
			return help;
		}

		@Override
		public GricliCommand create(String[] args) throws SyntaxException {
			if (args.length < 1) {
				throw new InvalidCommandException(this.commandName
						+ " command incomplete ");
			}
			CommandCreator sub = creatorMap.get(args[0]);
			if (sub == null) {
				throw new UnknownCommandException(this.commandName + " "
						+ args[0]);
			}

			String[] subargs = new String[args.length - 1];
			System.arraycopy(args, 1, subargs, 0, subargs.length);
			return sub.create(subargs);

		}

	}

	/*
	 * kill job <jobname> or destroy job <jobname>
	 */
	private class KillJobCreator extends CommandCreator {
		private final boolean clean;

		public KillJobCreator(boolean clean) {
			this.clean = clean;
		}

		@Override
		public GricliCommand create(String[] args) throws SyntaxException {
			if (args.length != 1) {
				throw new InvalidCommandException("kill command needs job name");
			}
			return new KillJobCommand(args[0], this.clean);
		}
	}

	private class FixedArgsCreator extends CommandCreator {
		private final int number;
		private final Class<? extends GricliCommand> command;

		public FixedArgsCreator(int number,
				Class<? extends GricliCommand> command) {
			this.number = number;
			this.command = command;
		}

		@Override
		public GricliCommand create(String[] args) throws SyntaxException {
			if (args.length != number) {
				throw new InvalidCommandException("command needs " + number
						+ " arguments");
			}
			try {
				return (GricliCommand) command.getConstructors()[0]
						.newInstance(args);
			} catch (Exception ex) {
				throw new InvalidCommandException(command.getCanonicalName()
						+ " command cannot be created");
			}
		}

	}

	/**
	 * help decorator
	 */
	private class HelpCreator extends CommandCreator {
		private final CommandCreator c;
		private final String helpMessage;

		public HelpCreator(CommandCreator c, String helpMessage) {
			this.c = c;
			this.helpMessage = helpMessage;
		}

		@Override
		public GricliCommand create(String[] args) throws SyntaxException {
			return c.create(args);
		}

		@Override
		public String help() {
			return helpMessage;
		}

	}

	private class HelpCommandCreator extends CommandCreator {

		@Override
		public GricliCommand create(String[] args) throws SyntaxException {
			return new HelpCommand(args);
		}

	}

	private class HelpCommand implements GricliCommand {
		private final String[] args;

		public HelpCommand(String[] args) {
			this.args = args;
		}

		public GricliEnvironment execute(GricliEnvironment env)
				throws GricliRuntimeException {

			if (args.length == 0) {
				for (CommandCreator c : creatorMap.values()) {
					System.out.println(c.help());
				}
			} else {
				CommandCreator c2 = creatorMap.get(args[0]);
				for (int i = 0; i < args.length; i++) {
					if (c2 == null) {
						System.out.println("command "
								+ StringUtils.join(args, " ")
								+ " does not exist");
						return env;
					} else if (i == args.length - 1
							|| c2.getClass() != ChainedCreator.class) {
						System.out.println(c2.help());
					} else {
						ChainedCreator cc = (ChainedCreator) c2;
						c2 = cc.creatorMap.get(args[i + 1]);
					}

				}
			}

			return env;
		}

	}

	private class PrintJobCreator extends CommandCreator {

		@Override
		public GricliCommand create(String[] args) throws SyntaxException {
			if (args.length == 1) {
				return new PrintJobCommand(args[0], null);
			} else if (args.length == 2) {
				return new PrintJobCommand(args[0], args[1]);
			} else {
				throw new InvalidCommandException("invalid number of arguments");
			}

		}

	}

	private class AliasCreator extends CommandCreator {
		private final String[] alias;
		private final GricliCommandFactory f;

		public AliasCreator(GricliCommandFactory f, String[] alias) {
			this.alias = alias;
			this.f = f;
		}

		@Override
		public GricliCommand create(String[] args) throws SyntaxException {
			String[] resultArgs = new String[alias.length + args.length];
			System.arraycopy(alias, 0, resultArgs, 0, alias.length);
			System.arraycopy(args, 0, resultArgs, alias.length, args.length);

			return f.create(resultArgs);
		}

	}

}
