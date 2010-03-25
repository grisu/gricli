package org.vpac.grisu.client.gricli;

import java.io.File;
import java.net.URI;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class GrisuClientCommandlineProperties implements GrisuClientProperties {

	static final Logger myLogger = Logger
			.getLogger(Gricli.class.getName());


	// common options
	public final static String CONFIG_FILE_PATH_OPTION = "config";
	public final static String SERVICE_INTERFACE_URL_OPTION = "serviceInterfaceUrl";
	public final static String MODE_OPTION = "mode";
	public final static String FORCE_ALL_MODE_PARAMETER = "force-all";
	public final static String ALL_MODE_PARAMETER = "all";
	public final static String SUBMIT_MODE_PARAMETER = "submit";
	public final static String STATUS_MODE_PARAMETER = "check";
	public final static String LOGIN_MODE_PARAMETER = "login";
	public final static String JOIN_MODE_PARAMETER = "join";
		
	public final static String VERBOSE_OPTION = "verbose";
	public final static String DEBUG_OPTION = "debug";

	public final static String MYPROXY_USERNAME_OPTION = "myproxy_username";
	public final static String USE_LOCAL_PROXY_OPTION = "useLocalProxy";

	public final static String KILL_POSSIBLY_EXISTING_JOB = "killExistingJob";
	
	public final static String TIME_TO_WAIT_BEFORE_RECHECK_STATUS = "statusRecheckInterval";
	public final static String STAGEOUT_OPTION = "stageout";
	public final static String CLEAN_OPTION = "clean";
	public final static String FORCE_CLEAN_MODE_PARAMETER = "force-clean";


	// submit options

	// stageout options
//	public final static String OUTPUTFILENAMES_OPTION = "outputFileNames";
	public final static String STAGEOUTDIRECTORY_OPTION = "stageoutDirectory";

	private String serviceInterfaceUrl = "https://grisu.vpac.org/grisu-ws/services/grisu";

	private Set<String> failedParameters = null;

	private CommandLine line = null;

	private HelpFormatter formatter = new HelpFormatter();
	private Options options = null;

	private String mode = null;
	private boolean stageout = false;
	private boolean clean = false;

	private int recheckInterval = -1;

	private String[] outputFileNames = null;
	private URI stageOutDirectory = null;

	private GrisuClientFileConfiguration configuration = null;

	public GrisuClientCommandlineProperties(String[] args) {
		this.formatter.setLongOptPrefix("--");
		this.formatter.setOptPrefix("-");
		this.options = getOptions();
		parseCLIargs(args);
	}


	
	public String getStageoutDirectory() {
		return line.getOptionValue(STAGEOUTDIRECTORY_OPTION);
	}
	
	public String getServiceInterfaceUrl() {
            	serviceInterfaceUrl = line.getOptionValue(SERVICE_INTERFACE_URL_OPTION);
                if (serviceInterfaceUrl == null){
                    serviceInterfaceUrl = getConfigOption(SERVICE_INTERFACE_URL_OPTION);
                }
                return serviceInterfaceUrl;
	}

	public String getMode() {
		return mode;
	}

	public boolean stageOutResults() {
		boolean stageout = line.hasOption(STAGEOUT_OPTION);
		return  (stageout || "true".equals(getConfigOption(STAGEOUT_OPTION)));
	}
	

	public boolean cleanAfterStageOut() {
		return clean;
	}
	
	public boolean verbose() {
		boolean verbose = line.hasOption(VERBOSE_OPTION);
		return  (verbose || "true".equals(getConfigOption(VERBOSE_OPTION)));
	}
	
	public boolean debug() {
		boolean debug = line.hasOption(DEBUG_OPTION);
		return  (debug || "true".equals(getConfigOption(DEBUG_OPTION)));
	}

	public boolean useLocalProxy(){
		boolean useLocalProxy = line.hasOption(USE_LOCAL_PROXY_OPTION);
		return  (useLocalProxy || "true".equals(getConfigOption(USE_LOCAL_PROXY_OPTION)));
	}

	public boolean killPossiblyExistingJob() {
		boolean kill = line.hasOption(KILL_POSSIBLY_EXISTING_JOB);
		return  (kill || "true".equals(getConfigOption(KILL_POSSIBLY_EXISTING_JOB)));
	}
	
	public int getRecheckInterval() {
		return recheckInterval;
	}

	
	private void parseCLIargs(String[] args) {
		
		// create the parser
		CommandLineParser parser = new PosixParser();
		try {
			// parse the command line arguments
			line = parser.parse(this.options, args);
		} catch (ParseException exp) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		}
		
		try {
			this.configuration = 
				GrisuClientFileConfiguration.getConfiguration(line.getOptionValue(CONFIG_FILE_PATH_OPTION));
			myLogger.debug(configuration);
		} catch (ConfigurationException ex) {
			throw new RuntimeException(ex);
		}

		String[] arguments = line.getArgs();
		
		if ( arguments.length > 0 ) {
			if ( arguments.length == 1 ) {
				System.err.println("Unknown argument: "+arguments[0]);
			} else {
				StringBuffer buf = new StringBuffer();
				for ( String arg : arguments ) {
					buf.append(arg+" ");
				}	
				System.err.println("Unknown argument: "+buf.toString());
			}
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		}

		if (!line.hasOption(MODE_OPTION)) {
			mode = ALL_MODE_PARAMETER;
		} else {
			mode = line.getOptionValue(MODE_OPTION);
		}

		if (SUBMIT_MODE_PARAMETER.equals(mode)) {

			checkSubmitModeParameters();

		} else if (STATUS_MODE_PARAMETER.equals(mode)) {

			checkStatusModeParameters();

		} else if (JOIN_MODE_PARAMETER.equals(mode)) {

		       checkJoinModeParameters();
		} else if (LOGIN_MODE_PARAMETER.equals(mode)) {
		    checkLoginModeParameters();
		    return;

		} else if (ALL_MODE_PARAMETER.equals(mode) || FORCE_ALL_MODE_PARAMETER.equals(mode)) {

			checkAllModeParameters();

		} else if (FORCE_CLEAN_MODE_PARAMETER.equals(mode)) {
				// all we need is job name
		} else {

			System.err.println("Mode " + mode + " not supported.");
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		}

		// load configuration
		try {
			this.configuration = 
				GrisuClientFileConfiguration.getConfiguration(line.getOptionValue(CONFIG_FILE_PATH_OPTION));
			myLogger.debug(configuration);
		} catch (ConfigurationException ex) {
			throw new RuntimeException(ex);
		}

		// common options
		checkCommonOptions();
		
		// other options
		String recheckIntervalString = getConfigOption(TIME_TO_WAIT_BEFORE_RECHECK_STATUS);
		if ( line.hasOption(TIME_TO_WAIT_BEFORE_RECHECK_STATUS) ) {
			recheckIntervalString = line.getOptionValue(TIME_TO_WAIT_BEFORE_RECHECK_STATUS);
		}
		
		try {
			if (recheckIntervalString != null) {
				recheckInterval = Integer.parseInt(recheckIntervalString);
			}
							
		} catch (NumberFormatException e) {
			System.err.println("Please use an integer for the recheck interval time");
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		}

	}

	private String getConfigOption(String key){
		try {
			return configuration.getCommonOption(key);
		} catch (ConfigurationException e) {
			myLogger.debug("problem reading configuration option " + key + ": " + e);
			return null;
		}
	}

	private void checkCommonOptions() {

		serviceInterfaceUrl = getConfigOption(SERVICE_INTERFACE_URL_OPTION);			

		if (!line.hasOption(CommandlineProperties.JOBNAME)) {
			System.err.println("Please specify the jobname.");
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		}

		if (line.hasOption(SERVICE_INTERFACE_URL_OPTION)) {
			serviceInterfaceUrl = 
				line.getOptionValue(SERVICE_INTERFACE_URL_OPTION);
			
		}
		
		/* if ( ! line.hasOption(MYPROXY_USERNAME_OPTION) ) {
			System.err.println("Please specify your myproxy username.");
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		} */

	}

        private void checkLoginModeParameters(){
	    if (!line.hasOption(CommandlineProperties.SHIB_USERNAME_OPTION)) {
		System.err.println("please specify shibboleth username");
		System.exit(1);
	    }
	    if (!line.hasOption(CommandlineProperties.SHIB_IDP_OPTION)) {
		System.err.println("please specify identity provider");
		System.exit(1);
	    }
	} 

	private void checkSubmitModeParameters() {

		/*if (!line
				.hasOption(CommandlineProperties.SUBMISSION_LOCATION_OPTION)) {
			System.err
					.println("Please specify the submission location you want to use.");
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		}*/

		/*if (!line.hasOption(CommandlineProperties.VO_OPTION)) {
			System.err.println("Please specify the VO you want to use.");
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		}*/

		if (!line.hasOption(CommandlineProperties.COMMAND_OPTION)) {
			System.err.println("Please specify the comand you want to run.");
			formatter.printHelp("grisu-client", this.options);
			System.exit(1);
		}


		if (!line.hasOption(CommandlineProperties.INPUTFILEPATH_OPTION)) {
			myLogger.debug("No input files to stage for this job.");
		} else {
			String inputFileString = line.getOptionValue(CommandlineProperties.INPUTFILEPATH_OPTION);
			if ( !StringUtils.isEmpty(inputFileString) ) {
			String[] input = inputFileString.split(",");
			for ( String filepath : input ) {
				
				File file = new File(filepath);
				if ( ! file.exists() ) {
					System.err.println("Input file "+filepath+" doesn't exist.");
					System.exit(1);
				}
			}
			}
			
		}

	}


	private void checkStatusModeParameters() {

		checkStageoutModeParameters();
		
	}

	private void checkStageoutModeParameters() {

		if (line.hasOption(STAGEOUT_OPTION)) {

			String stageOutDirectoryStr = getConfigOption(STAGEOUTDIRECTORY_OPTION);

			if (!line.hasOption(STAGEOUTDIRECTORY_OPTION) && (stageOutDirectoryStr == null)) {
				myLogger
						.debug("No stageout directory specified. Using current directory.");
				stageOutDirectory = new File(".").toURI();

			} else {
				stageOutDirectory = new File(stageOutDirectoryStr).toURI();
			}
			
			// check whether the dir can be used
			File dir = new File(stageOutDirectory);
			if ( dir.exists() ) {
				if (! dir.canWrite() ) {
					myLogger.debug("Can't write to stageout directory.");
					System.err.println("Can't write to stageout directory.");
					System.exit(1);
				}
			} else {
				System.err.println("Stageout directory doesn't exist.");
				System.exit(1);
			}
			
			stageout = true;
		} else {
			stageout = false;
		}
		
		clean = "true".equals(getConfigOption(CLEAN_OPTION)) || line.hasOption(CLEAN_OPTION);


	}


	private void checkJoinModeParameters() {

		checkStatusModeParameters();
		checkStageoutModeParameters();

	}

	private void checkAllModeParameters() {

		checkSubmitModeParameters();
		checkStatusModeParameters();
		checkStageoutModeParameters();
	}

	public CommandLine getCommandLine() {
		return line;
	}

        public String getShibIdp(){
	    return line.getOptionValue(CommandlineProperties.SHIB_IDP_OPTION);
        } 
    
        public String getShibUsername(){
	    return line.getOptionValue(CommandlineProperties.SHIB_USERNAME_OPTION);
        } 

	public String getMyProxyUsername() {
		
		String username = getConfigOption(MYPROXY_USERNAME_OPTION);
		if (line.hasOption(MYPROXY_USERNAME_OPTION)) {
			username = line.getOptionValue(MYPROXY_USERNAME_OPTION);
		}
		if (username == null) {
			myLogger.debug("No myproxy username specified...");
		}
		return username;
	}

	// helper methods

	// option with long name, no arguments
	private static  Option createOption(String longName,String description){
		return OptionBuilder.withLongOpt(longName).withDescription(description).create();
	}

	// option with long name, has arguments
	private  static Option createOptionWithArg(String longName, String description){
		return OptionBuilder.withArgName(longName).hasArg().withLongOpt(longName).withDescription(description).create();
	}

	// option with long name, short name, no arguments
	private static Option createOption(String  longName, String shortName, String description){
		return OptionBuilder.withLongOpt(longName).withDescription(description).create(shortName);
	}

	// option with  long name,short name and argument
	private static Option  createOptionWithArg(String longName, String shortName, String description){
		return OptionBuilder.withArgName(longName).hasArg().withLongOpt(longName).withDescription(description).create(shortName);
	}

	private static Options getOptions() {

		Options options = null;

		// common options
		Option config = createOptionWithArg(CONFIG_FILE_PATH_OPTION,"F",				    
				"configuration file location (optional, default: " + GrisuClientFileConfiguration.CONFIG_FILE_PATH +")");
		Option serviceInterfaceUrl = createOptionWithArg(SERVICE_INTERFACE_URL_OPTION,"i",
					     "the serviceinterface to connect to (optional, default: https://grisu.vpac.org/grisu-ws/services/grisu)");
		Option mode = createOptionWithArg(MODE_OPTION,"m",
			      "the mode you want to use: all|submit|check|join|force-clean|login (optional, default: all)");
		Option baseName = createOptionWithArg(CommandlineProperties.JOBNAME,"n",
                                  "the name for the job (required)");
		Option myproxy_username = createOptionWithArg(MYPROXY_USERNAME_OPTION, "myproxy username (required unless local proxy is used)");
		Option useLocalProxy    = createOption(USE_LOCAL_PROXY_OPTION,"l");
		Option email            = createOptionWithArg(CommandlineProperties.EMAIL_OPTION,"the email address to send status report to");
		Option verbose          = createOption(VERBOSE_OPTION,"v","verbose output");
		Option debug = createOption(DEBUG_OPTION,"more debug output to $HOME/.grisu/grisu.debug (optional)");
		Option submissionLocation = createOptionWithArg(CommandlineProperties.SUBMISSION_LOCATION_OPTION, "s",
					  "the submission location (e.g. dque@brecca-m:ng2.vpac.monash.edu.au), (required for modes all|submit)");
		Option vo = createOptionWithArg(CommandlineProperties.VO_OPTION,"the vo to use (required for modes all|submit)");
		Option command  = createOptionWithArg(CommandlineProperties.COMMAND_OPTION,"c","the commandline to run remotely (required for modes all|submit)");
		Option baseInputFilePath = createOptionWithArg(CommandlineProperties.INPUTFILEPATH_OPTION,"the input files, separated with a coma");
		Option walltime = createOptionWithArg(CommandlineProperties.WALLTIME_OPTION, "the walltime in seconds (required for modes all|submit)");
		Option cpus = createOptionWithArg(CommandlineProperties.CPUS_OPTION,"the number of cpus to run the job with (required for modes all|submit)");
		Option memory = createOptionWithArg(CommandlineProperties.MEMORY_OPTION,"r","the number of cpus to run the job with (required for modes all|submit)");
		Option stdout = createOptionWithArg(CommandlineProperties.STDOUT_OPTION,"the name of the stdout file (optional)");
		Option stderr = createOptionWithArg(CommandlineProperties.STDERR_OPTION,"the name of the stderr file (optional)");
		Option module = createOptionWithArg(CommandlineProperties.MODULE_OPTION,"the module to load on the cluster (optional)");
		Option killExisting = createOption(KILL_POSSIBLY_EXISTING_JOB,"k",
				     "specify this if you want to kill & clean a possibly existing job with the same name before submitting the job (optional)");
		Option recheckIntervall = createOptionWithArg(TIME_TO_WAIT_BEFORE_RECHECK_STATUS,
                                         "time to wait inbetween status checks in seconds (default: 600, optional for all|join)");
		Option stageout = createOption(STAGEOUT_OPTION,
					       "if you want to stageout the results if the job is finished (optional for modes all|check|join)");
		Option clean   = createOption(CLEAN_OPTION,
					      "specify this if you want to clean the job after a successful stageout (optional for modes all|check|join)");
		Option stageOutDirectory = createOptionWithArg(STAGEOUTDIRECTORY_OPTION,
					    "the local directory to stage out the files to (default: current directory, optional for modes all|status|join)");
		Option generateUniqueJobName = createOption(CommandlineProperties.GENERATE_UNIQUE_JOBNAME_OPTION,
						     "with this option, grisu client ensures that the name for the job will always be unique.\n"+
						     "the "+CommandlineProperties.JOBNAME+ " is still required and will be used as a prefix to this job");
		Option shibUsername = createOptionWithArg(CommandlineProperties.SHIB_USERNAME_OPTION, "u","shibboleth username");
		Option shibIdp  = createOptionWithArg(CommandlineProperties.SHIB_IDP_OPTION, "shibboleth identity provider");

		options = new Options();
		options.addOption(config);
		options.addOption(serviceInterfaceUrl);
		options.addOption(useLocalProxy);
		options.addOption(myproxy_username);
		options.addOption(verbose);
		options.addOption(debug);
		options.addOption(email);
		options.addOption(mode);
		options.addOption(submissionLocation);
		options.addOption(vo);
		options.addOption(baseName);
		options.addOption(command);
		options.addOption(baseInputFilePath);
		options.addOption(walltime);
		options.addOption(cpus);
		options.addOption(memory);
		options.addOption(stdout);
		options.addOption(stderr);
		options.addOption(module);
		options.addOption(killExisting);
		options.addOption(recheckIntervall);
		options.addOption(stageout);
		options.addOption(clean);
		options.addOption(stageOutDirectory);
		options.addOption(generateUniqueJobName);
		options.addOption(shibUsername);
		options.addOption(shibIdp);

		return options;

	}

}
