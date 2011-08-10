package grisu.gricli.environment;

import grisu.control.ServiceInterface;
import grisu.frontend.model.job.JobObject;
import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.GricliSetValueException;
import grisu.gricli.LoginRequiredException;
import grisu.jcommons.constants.Constants;
import grisu.model.GrisuRegistry;
import grisu.model.GrisuRegistryManager;
import grisu.model.dto.GridFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class GricliEnvironment {

	static final Logger myLogger = Logger.getLogger(GricliEnvironment.class.getName());

	public static List<String> getVariableNames(){
		List<String> result = new LinkedList<String>();
		for (Field f: GricliEnvironment.class.getFields()){
			if (f.getType().isAssignableFrom(GricliVar.class)){
				result.add(f.getName());
			}
		}
		return result;
	}
	public final GricliVar<String> email, prompt, host, group, gdir, queue, outputfile, version, application, jobname, jobtype, description;
	public final GricliVar<Boolean> email_on_start, email_on_finish, debug;
	public final GricliVar<Integer> memory, walltime, cpus;
	public final GricliVar<File> dir;

	public final FileListVar files;

	private ServiceInterface si;

	private GrisuRegistry reg;

	private String siUrl;


	private boolean quiet;
	public GricliEnvironment() {
		this.email = new StringVar("email", "");
		this.email_on_start = new BoolVar("email_on_start", false);
		this.email_on_finish = new BoolVar("email_on_finish", false);

		this.prompt = new StringVar("prompt","gricli> ");

		this.host = new StringVar("host","");
		this.gdir = new StringVar("gdir","");
		this.dir = new DirVar("dir", new File(System.getProperty("user.dir")));
		this.dir.addListener(new GricliVarListener<File>() {
			public void valueChanged(File value) {
				try {
					System.setProperty("user.dir", value.getCanonicalPath());
				} catch (IOException e) {
					myLogger.error(e);
				}
			}
		});

		this.description = new StringVar("description","gricli job");
		this.group = new StringVar("group","/nz") {

			private boolean validate = false;

			@Override
			public void set(String group) throws GricliSetValueException {
				if (validate){
					for (String pg: Gricli.completionCache.getAllFqans()){
						if (pg.equals(group)){
							super.set(group);
							return;
						}
					}
					throw new GricliSetValueException(getName(),group,"group does not exist. use 'print groups' to see available options");
				};
				this.validate = true;
				super.set(group);
			};

		};
		this.group.addListener(new GricliVarListener<String>() {

			public void valueChanged(String value) {
				if (StringUtils.isNotBlank(value)) {
					final String app = application.get();
					if (StringUtils.isNotBlank(app) && !Constants.GENERIC_APPLICATION_NAME.equals(app)) {
						final String fqan = value;
						new Thread() {
							@Override
							public void run() {

								GrisuRegistry reg = getGrisuRegistry();
								myLogger.debug("Pre-loading cache for "+fqan+" / "+app);
								reg.getApplicationInformation(app).getAllAvailableVersionsForFqan(fqan);
								myLogger.debug("Pre-loading finished.");
							}
						}.start();
					}
				}
			}
		});

		this.queue = new StringVar("queue","");

		this.jobname = new StringVar("jobname","gricli") {
			@Override
			public void set(String jobname) throws GricliSetValueException {
				if (jobname.contains(" ")){
					throw new GricliSetValueException(getName(), jobname, "cannot contain spaces");
				}
				super.set(jobname);
			}
		};
		this.jobtype = new StringVar("jobtype","single") {
			@Override
			public String fromString(String arg) throws GricliSetValueException {
				if (!("single".equals(arg) || "mpi".equals(arg) || "smp".equals(arg))){
					throw new GricliSetValueException(getName(), arg, "jobtype has to be one of: smp, mpi or single");
				}
				return arg;
			}
		};

		this.memory = new MemoryVar("memory", 2048);
		this.walltime = new WalltimeVar("walltime",10);
		this.cpus = new IntVar("cpus",1) {
			@Override
			public void set(Integer value) throws GricliSetValueException {
				super.set(value);
				if (value <= 0){
					throw new GricliSetValueException(getName(),""+value, "cannot be negative");
				}
			}
		};

		this.version = new StringVar("version", null, true);
		this.application = new StringVar("application", null, true);
		this.application.addListener(new GricliVarListener<String>() {

			public void valueChanged(String value) {
				if (!StringUtils.isBlank(value)
						&& ! Constants.GENERIC_APPLICATION_NAME.equals(value)) {
					final String fqan = group.get();
					if (StringUtils.isNotBlank(fqan)) {
						final String a = value;
						new Thread() {
							@Override
							public void run() {
								GrisuRegistry reg = getGrisuRegistry();
								myLogger.debug("Pre-loading cache for " + a + " / "
										+ fqan);
								reg.getApplicationInformation(a)
								.getAllAvailableVersionsForFqan(fqan);
								myLogger.debug("Pre-loading finished.");
							}
						}.start();
					}
				}
			}

		});

		this.debug = new BoolVar("debug", false);
		this.outputfile = new StringVar("outputfile", null, true);

		this.files = new FileListVar("files");
	}
	public GrisuRegistry getGrisuRegistry() {
		return this.reg;
	}

	public JobObject getJob() throws LoginRequiredException{

		ServiceInterface si = getServiceInterface();
		final JobObject job = new JobObject(si);
		job.setJobname(jobname.get());
		String app = application.get();
		if (app == null){
			job.setApplication(Constants.GENERIC_APPLICATION_NAME);
		}
		else {
			job.setApplication(app);
		}

		String v = version.get();
		if (v == null){
			job.setApplicationVersion(Constants.NO_VERSION_INDICATOR_STRING);
		} else {
			job.setApplicationVersion(v);
		}

		job.setCpus(cpus.get());
		job.setEmail_address(email.get());
		job.setEmail_on_job_finish(email_on_finish.get());
		job.setEmail_on_job_start(email_on_start.get());

		job.setDescription(description.get());

		job.setWalltimeInSeconds(walltime.get() * 60);
		job.setMemory(((long)memory.get()) * 1024 * 1024);
		job.setSubmissionLocation(queue.get());

		boolean isMpi = "mpi".equals(jobtype.get());
		job.setForce_mpi(isMpi);
		boolean isSmp = "smp".equals(jobtype.get());
		if (isSmp){
			job.setForce_single(true);
			job.setHostCount(1);
		}

		// attach input files
		List<String> fs = files.get();
		for (String file : fs) {
			System.out.println("adding input file "
					+ new GridFile(file).getUrl());
			job.addInputFileUrl(new GridFile(file).getUrl());
		}

		return job;
	}

	public ServiceInterface getServiceInterface() throws LoginRequiredException {
		if (si == null) {
			throw new LoginRequiredException();
		}
		return si;
	}

	public String getServiceInterfaceUrl() {
		return siUrl;
	}


	public GricliVar<?> getVariable(String var) throws GricliRuntimeException {
		try {
			Field f = this.getClass().getField(var);
			return (GricliVar<?>)(f.get(this));
		} catch (Exception ex){
			throw new GricliRuntimeException("global " + var + " does not exist");
		}
	}

	public List<GricliVar<?>> getVariables(){
		List<GricliVar<?>> result = new LinkedList<GricliVar<?>>();
		Object o;

		for (Field f: this.getClass().getFields()){
			try {
				o = f.get(this);
			} catch (Exception ex){
				continue;
			}
			if (o instanceof GricliVar<?>){
				result.add((GricliVar<?>)o);
			}
		}
		Collections.sort(result);
		return result;
	}

	public void printError(String message){
		myLogger.info("gricli-audit-error username=" + System.getProperty("user.name") + "command=" + message );
		System.err.println(message);
	}

	public void printMessage(String message){
		if (!quiet){
			System.out.println(message);
		}

		PrintStream out = null;

		try {
			String output = outputfile.get();
			if (output != null){
				File outputfile = new File(output);
				out = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputfile, true)));
				out.println(message);
			}
		}
		catch (IOException ex){
			printError(ex.getMessage());
		}
		finally {
			if (out != null){
				out.close();
			}
		}
	}

	public void quiet(boolean q){
		this.quiet = q;
	}
	public void setServiceInterface(ServiceInterface si) {
		this.si = si;
		this.reg = GrisuRegistryManager.getDefault(si);
	}

	public void setServiceInterfaceUrl(String siUrl) {
		this.siUrl = siUrl;
	}

}
