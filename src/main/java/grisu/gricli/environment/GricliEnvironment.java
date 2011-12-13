package grisu.gricli.environment;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.StatusException;
import grisu.frontend.model.job.JobObject;
import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.GricliSetValueException;
import grisu.gricli.LoginRequiredException;
import grisu.jcommons.constants.Constants;
import grisu.model.GrisuRegistry;
import grisu.model.GrisuRegistryManager;
import grisu.model.dto.GridFile;
import grisu.model.status.StatusObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GricliEnvironment {

	static final Logger myLogger = LoggerFactory
			.getLogger(GricliEnvironment.class);

	public final GricliVar<String> email, prompt, group, gdir, queue,
	outputfile, version, application, jobname, jobtype, description;
	public final GricliVar<Boolean> email_on_start, email_on_finish, debug;
	public final GricliVar<Integer> memory, walltime, cpus, hostCount;
	public final GricliVar<File> dir;
	public final GricliVar<Hashtable<String, String>> env;
	public final FileListVar files;

	private ServiceInterface si;

	private GrisuRegistry reg;

	private String siUrl;

	private boolean quiet;

	private final List<String> notificationQueue = Collections
			.synchronizedList(new ArrayList<String>());


	public static final int STATUS_RECHECK_INTERVALL = 8;

	public GricliEnvironment() {
		this.email = new StringVar("email", "");
		this.email_on_start = new BoolVar("email_on_start", false);
		this.email_on_finish = new BoolVar("email_on_finish", false);

		this.prompt = new StringVar("prompt", "jobs> ");

		this.gdir = new StringVar("gdir", "");
		this.dir = new DirVar("dir", new File(System.getProperty("user.dir")));
		this.dir.addListener(new GricliVarListener<File>() {
			public void valueChanged(File value) {
				try {
					System.setProperty("user.dir", value.getCanonicalPath());
				} catch (final IOException e) {
					myLogger.error(e.getLocalizedMessage(), e);
				}
			}
		});

		this.description = new StringVar("description", "gricli job");
		this.group = new StringVar("group", "/nz/nesi") {

			private boolean validate = false;

			@Override
			public void set(String group) throws GricliSetValueException {
				if (validate) {
					// this is a bit hairy since you shouldn't actually rely on
					// completion cache
					// to be populated. I changed implementation for now, so it
					// blocks if list of fqans is not loaded yet...
					if (Gricli.completionCache.getAllFqans().length == 0) {
						super.set(group);
						return;
					}
					for (final String pg : Gricli.completionCache.getAllFqans()) {
						if (pg.equals(group)) {
							super.set(group);
							return;
						}
					}
					throw new GricliSetValueException(getName(), group,
							"group does not exist. use 'print groups' to see available options");
				}
				;
				this.validate = true;
				super.set(group);
			};

		};
		this.group.addListener(new GricliVarListener<String>() {

			public void valueChanged(String value) {
				if (StringUtils.isNotBlank(value)) {

					final String app = application.get();
					if (StringUtils.isNotBlank(app)
							&& !Constants.GENERIC_APPLICATION_NAME.equals(app)) {
						final String fqan = value;
						new Thread() {
							@Override
							public void run() {

								try {
									final GrisuRegistry reg = getGrisuRegistry();
									myLogger.debug("Pre-loading cache for "
											+ fqan + " / " + app);
									reg.getApplicationInformation(app)
									.getAllAvailableVersionsForFqan(
											fqan);
									// reg.getApplicationInformation(app)
									// .getExecutablesForVo(fqan);
									myLogger.debug("Pre-loading finished.");
								} catch (final Throwable th) {
									myLogger.error(th.getLocalizedMessage(), th);
								}
							}
						}.start();
					}
				}
			}
		});

		this.queue = new StringVar("queue", null, true);

		this.jobname = new StringVar("jobname", "gricli") {
			@Override
			public void set(String jobname) throws GricliSetValueException {
				if (jobname == null) {
					throw new GricliSetValueException(getName(), jobname,
							"jobname cannot be unset");
				}
				if (jobname.contains(" ")) {
					throw new GricliSetValueException(getName(), jobname,
							"cannot contain spaces");
				}
				super.set(jobname);
			}
		};
		this.jobtype = new StringVar("jobtype", "single") {
			@Override
			public String fromString(String arg) throws GricliSetValueException {
				if (!("single".equals(arg) || "mpi".equals(arg) || "smp"
						.equals(arg))) {
					throw new GricliSetValueException(getName(), arg,
							"jobtype has to be one of: smp, mpi or single");
				}
				return arg;
			}
		};

		this.memory = new MemoryVar("memory", 2048);
		this.walltime = new WalltimeVar("walltime", 10);
		this.cpus = new PositiveIntVar("cpus", 1);
		this.hostCount = new PositiveIntVar("hostCount", null, true);

		this.version = new StringVar("version", null, true);
		this.application = new StringVar("package", null, true);
		this.application.addListener(new GricliVarListener<String>() {

			public void valueChanged(String value) {
				if (!StringUtils.isBlank(value)
						&& !Constants.GENERIC_APPLICATION_NAME.equals(value)) {
					final String fqan = group.get();
					if (StringUtils.isNotBlank(fqan)) {
						final String a = value;
						new Thread() {
							@Override
							public void run() {

								try {
									final GrisuRegistry reg = getGrisuRegistry();
									myLogger.debug("Pre-loading cache for " + a
											+ " / " + fqan);
									reg.getApplicationInformation(a)
									.getAllAvailableVersionsForFqan(
											fqan);
									myLogger.debug("Pre-loading finished.");
								} catch (final Throwable th) {
									myLogger.error(th.getLocalizedMessage(), th);
								}
							}
						}.start();
					}
				}
			}

		});

		this.debug = new BoolVar("debug", false);
		this.outputfile = new StringVar("outputfile", null, true);

		this.files = new FileListVar("files");
		this.files.setPersistent(false);
		this.env = new EnvironmentVar("env");
		this.env.setPersistent(false);
	}

	public void addNotification(String msg) {
		getNotifications().add(msg);
	}

	public synchronized void addTaskToMonitor(final String taskDesc,
			final StatusObject status) {

		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					status.waitForActionToFinish(STATUS_RECHECK_INTERVALL,
							false);

					boolean failed = status.getStatus().isFailed();
					if (!failed) {
						addNotification(taskDesc + " finished successfully.");
					} else {
						String cause = status.getStatus().getErrorCause();
						if (StringUtils.isBlank(cause)) {
							cause = "Unknown error.";
						}
						addNotification(taskDesc + " failed: " + cause);
					}

				} catch (StatusException e) {
					addNotification("Can't find task handle for " + taskDesc);
				}
			}
		};

		t.setDaemon(true);
		t.setName("client task monitor: " + status.getHandle());
		t.start();

	}

	public void addTaskToMonitor(String taskDesk, String taskHandle)
			throws GricliRuntimeException {
		StatusObject so;
		try {
			so = new StatusObject(getServiceInterface(), taskHandle);
		} catch (LoginRequiredException e) {
			throw new GricliRuntimeException(e);
		}
		addTaskToMonitor(taskDesk, so);
	}

	public String getCurrentAbsoluteDirectory() {
		try {
			final File dir = (File) getVariable("dir").get();
			return dir.getAbsolutePath();
		} catch (final GricliRuntimeException e) {
			myLogger.error(e.getLocalizedMessage(), e);
			return null;
		}
	}

	public GrisuRegistry getGrisuRegistry() {
		return this.reg;
	}

	public JobObject getJob() throws LoginRequiredException {

		final ServiceInterface si = getServiceInterface();
		final JobObject job = new JobObject(si);
		job.setJobname(jobname.get());
		final String app = application.get();
		if (app == null) {
			job.setApplication(Constants.GENERIC_APPLICATION_NAME);
		} else {
			job.setApplication(app);
		}

		final String v = version.get();
		if (v == null) {
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
		job.setMemory(((long) memory.get()) * 1024 * 1024);

		if (queue.get() != null) {
			job.setSubmissionLocation(queue.get());
		} else {
			job.setSubmissionLocation(Constants.NO_SUBMISSION_LOCATION_INDICATOR_STRING);
		}

		if ("mpi".equals(jobtype.get())) {
			job.setForce_mpi(true);
		}

		if (hostCount.get() != null) {
			job.setHostCount(hostCount.get());
		}

		if ("smp".equals(jobtype.get())) {
			job.setForce_single(true);
			job.setHostCount(1);
		}

		// add environment variables
		for (final String var : env.get().keySet()) {
			job.addEnvironmentVariable(var, env.get().get(var));
		}

		// attach input files
		final List<String> fs = files.get();
		for (final String file : fs) {
			// System.out.println("adding input file "
			// + new GridFile(file).getUrl());
			job.addInputFileUrl(new GridFile(file).getUrl());
		}

		return job;
	}

	public synchronized List<String> getNotifications() {
		return notificationQueue;
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

			for (final Field f : this.getClass().getFields()) {
				if (f.get(this) instanceof GricliVar<?>) {
					final GricliVar<?> result = (GricliVar<?>) f.get(this);
					final String name = result.getName();
					if (var.equals(name)) {
						return result;
					}
				}
			}
			throw new GricliRuntimeException("global " + var
					+ " does not exist");

		} catch (final Exception ex) {
			throw new GricliRuntimeException("global " + var
					+ " does not exist");
		}
	}

	public List<String> getVariableNames() {
		final List<String> result = new LinkedList<String>();
		for (final GricliVar<?> v : getVariables()) {
			result.add(v.getName());
		}
		return result;
	}

	public List<GricliVar<?>> getVariables() {
		final List<GricliVar<?>> result = new LinkedList<GricliVar<?>>();
		Object o;

		for (final Field f : this.getClass().getFields()) {
			try {
				o = f.get(this);
			} catch (final Exception ex) {
				continue;
			}
			if (o instanceof GricliVar<?>) {
				result.add((GricliVar<?>) o);
			}
		}
		Collections.sort(result);
		return result;
	}

	public boolean isTerminalSession() {

		return System.console() != null;
	}

	public void printError(String message) {
		myLogger.info("gricli-audit-error username="
				+ System.getProperty("user.name") + "command=" + message);
		System.err.println(message);
	}

	public void printMessage(String message) {
		if (!quiet) {
			System.out.println(message);
		}

		PrintStream out = null;

		try {
			final String output = outputfile.get();
			if (output != null) {
				final File outputfile = new File(output);
				out = new PrintStream(new BufferedOutputStream(
						new FileOutputStream(outputfile, true)));
				out.println(message);
			}
		} catch (final IOException ex) {
			printError(ex.getMessage());
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public void quiet(boolean q) {
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
