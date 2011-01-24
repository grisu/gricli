package org.bestgrid.grisu.client.gricli.command;

import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.GricliRuntimeException;
import org.bestgrid.grisu.client.gricli.util.ServiceInterfaceUtils;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.BatchJobException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;

public class KillJobCommand implements GricliCommand {
	private final String jobFilter;
	private final boolean clean;

	public KillJobCommand(String jobFilter, boolean clean) {
		this.jobFilter = jobFilter;
		this.clean = clean;
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {
		ServiceInterface si = env.getServiceInterface();
		String jobname = null;
		try {
			for (String j : ServiceInterfaceUtils.filterJobNames(si, jobFilter)) {
				System.out.println("killing job " + j);
				jobname = j;
				si.kill(j, clean);
			}
		} catch (RemoteFileSystemException ex) {
			throw new GricliRuntimeException(ex);
		} catch (NoSuchJobException ex) {
			throw new GricliRuntimeException("job " + jobname
					+ " does not exist");
		} catch (BatchJobException ex) {
			throw new GricliRuntimeException(ex);
		}
		return env;
	}

}
