package org.bestgrid.grisu.client.gricli.command;

import au.org.arcs.jcommons.constants.Constants;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.GricliException;
import org.bestgrid.grisu.client.gricli.util.CommandlineTokenizer;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.JobPropertiesException;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.utils.SeveralStringHelpers;

public class SubmitCmdCommand implements GricliCommand{

    public static final String JOBNAME_PLACEHOLDER = "XXX_JOBNAME_XXX";
    public static final String APPLICATION_NAME_PLACEHOLDER = "XXX_APPLICATION_NAME_XXX";
    public static final String EXECUTABLE_NAME_PLACEHOLDER = "XXX_EXECUTABLE_XXX";
    public static final String ARGUMENTS_PLACEHOLDER = "XXX_ARGUMENT_ELEMENTS_XXX";
    public static final String WORKINGDIRECTORY_PLACEHOLDER = "XXX_WORKINGDIRECTORY_XXX";
    public static final String STDOUT_PLACEHOLDER = "XXX_STDOUT_XXX";
    public static final String STDERR_PLACEHOLDER = "XXX_STDERR_XXX";
    public static final String MODULE_PLACEHOLDER = "XXX_MODULE_XXX";
    public static final String EMAIL_PLACEHOLDER = "XXX_EMAIL_ADDRESS_XXX";
    public static final String TOTALCPUTIME_PLACEHOLDER = "XXX_TOTALCPUTIME_XXX";
    public static final String TOTALCPUCOUNT_PLACEHOLDER = "XXX_TOTALCPUCOUNT_XXX";
    public static final String SUBMISSIONLOCATION_PLACEHOLDER = "XXX_SUBMISSIONLOCATION_XXX";
    public static final String USEREXECUTIONHOSTFS_PLACEHOLDER = "XXX_USEREXECUTIONHOSTFS";
    public static final String MEMORY_PLACEHOLDER = "XXX_MEMORY_XXX";
    private final String cmd;

    public SubmitCmdCommand(String cmd){
        this.cmd = cmd;
    }

    public GricliEnvironment execute(GricliEnvironment env) throws GricliException {

        if (env.get("queue") == null ){
            throw new GricliException("submission queue not specified");
        }

        String[] arguments = CommandlineTokenizer.tokenize(cmd);
        String app = arguments[0];

        final InputStream in = SubmitCmdCommand.class.getResourceAsStream("/templates/generic_memory.xml");
        final String jsdlTemplateString = SeveralStringHelpers.fromInputStream(in);

        String jsdl = jsdlTemplateString.replaceAll(JOBNAME_PLACEHOLDER, env.get("jobname"));
        jsdl = jsdl.replaceAll(APPLICATION_NAME_PLACEHOLDER, Constants.GENERIC_APPLICATION_NAME);
        jsdl = jsdl.replaceAll(EXECUTABLE_NAME_PLACEHOLDER, app);

        final StringBuffer argElements = new StringBuffer();
        for (int i = 1; i < arguments.length; i++) {
            argElements.append("<Argument>" + arguments[i] + "</Argument>\n");
        }
        jsdl = jsdl.replaceAll(ARGUMENTS_PLACEHOLDER, argElements.toString());
        // this will be calculated on the backend now.
        jsdl = jsdl.replaceAll(WORKINGDIRECTORY_PLACEHOLDER, "");
        jsdl = jsdl.replaceAll(STDOUT_PLACEHOLDER, "stdout.txt");
        jsdl = jsdl.replaceAll(STDERR_PLACEHOLDER, "stderr.txt");
        jsdl = jsdl.replaceAll(MODULE_PLACEHOLDER, "");
        jsdl = jsdl.replaceAll(EMAIL_PLACEHOLDER, env.get("email"));
        final int noCpus = Integer.parseInt(env.get("cpus"));
        final int cpuTime = Integer.parseInt(env.get("walltime")) * 60 * noCpus;
        final int memory = Integer.parseInt(env.get("memory"));
        jsdl = jsdl.replaceAll(TOTALCPUTIME_PLACEHOLDER,
                new Integer(cpuTime).toString());
        jsdl = jsdl.replaceAll(TOTALCPUCOUNT_PLACEHOLDER,
                new Integer(noCpus).toString());
        jsdl = jsdl.replaceAll(MEMORY_PLACEHOLDER,
                new Integer(memory).toString());
        jsdl = jsdl.replaceAll(SUBMISSIONLOCATION_PLACEHOLDER, env.get("queue"));
        // this will be calculated on the backend now
        jsdl = jsdl.replaceAll(USEREXECUTIONHOSTFS_PLACEHOLDER, "");


        System.out.println(jsdl);

        ServiceInterface si = env.getServiceInterface();
        String jobname = null;
        try {
           jobname = si.createJob(jsdl, env.get("fqan"), Constants.TIMESTAMP_METHOD);
        } catch (JobPropertiesException ex) {
            Logger.getLogger(SubmitCmdCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            si.submitJob(jobname);
            System.out.println(" job name is " + jobname);
        } catch (JobSubmissionException ex) {
            Logger.getLogger(SubmitCmdCommand.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchJobException ex) {
            Logger.getLogger(SubmitCmdCommand.class.getName()).log(Level.SEVERE, null, ex);
        }

        return env;
    }


}
