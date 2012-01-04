package grisu.gricli.command;

import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

public class SubmitSweepCommand implements GricliCommand {
	private final String template;

	public SubmitSweepCommand(String template) {
		this.template = template;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {

		String tempTemplate = this.template;

		// f pattern for file substituted into command line
		final Pattern singleInputFile = Pattern.compile("\\$\\{f:[^}]*}");
		final Matcher singleInputFileM = singleInputFile.matcher(template);
		while (singleInputFileM.find()) {

			final String found = singleInputFileM.group();
			final String filename = found.substring(4, found.length() - 1);
			env.files.get().add(filename);
			tempTemplate = tempTemplate.replace(found,
					FilenameUtils.getName(filename));
		}

		// n pattern for file attached but not substituted
		final Pattern invisibleInputFile = Pattern.compile("\\$\\{n:[^}]*}");
		final Matcher invisibleInputFileM = invisibleInputFile
				.matcher(tempTemplate);
		while (invisibleInputFileM.find()) {
			final String found = invisibleInputFileM.group();
			final String filename = found.substring(4, found.length() - 1);
			env = new AttachCommand(null, new String[] { filename })
					.execute(env);
			tempTemplate = tempTemplate.replace(found, "");
		}
		env.printMessage("submitting " + tempTemplate);

		return new SubmitCommand(tempTemplate).execute(env);
	}

}
