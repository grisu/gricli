package org.bestgrid.grisu.client.gricli.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;
import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.GricliRuntimeException;

public class SubmitSweepCommand implements GricliCommand {
    private final String template;

    public SubmitSweepCommand(String template){
        this.template = template;
    }

    public GricliEnvironment execute(GricliEnvironment env) throws GricliRuntimeException {

        String tempTemplate = this.template;

        // f pattern for file substituted into command line
        Pattern singleInputFile = Pattern.compile("\\$\\{f:[^}]*}");
        Matcher singleInputFileM = singleInputFile.matcher(template);
        while (singleInputFileM.find()){

          String found = singleInputFileM.group();
          String filename =  found.substring(4, found.length() - 1);
          env.add("files", filename);
          tempTemplate = tempTemplate.replace(found, FilenameUtils.getName(filename));
        }

        // n pattern for file attached but not substituted
        Pattern invisibleInputFile = Pattern.compile("\\$\\{n:[^}]*}");
        Matcher invisibleInputFileM = invisibleInputFile.matcher(tempTemplate);
        while (invisibleInputFileM.find()){
            String found = invisibleInputFileM.group();
            String filename =  found.substring(4, found.length() - 1);
            env = new AttachCommand(filename).execute(env);
            tempTemplate = tempTemplate.replace(found, "");
        }
        System.out.println("submitting " + tempTemplate);

        return new SubmitCmdCommand(tempTemplate).execute(env);
    }

}
