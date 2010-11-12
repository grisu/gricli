package org.bestgrid.grisu.client.gricli.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.activation.DataHandler;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.GricliRuntimeException;
import org.bestgrid.grisu.client.gricli.util.ServiceInterfaceUtils;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.dto.DtoFileObject;
import org.vpac.grisu.model.dto.DtoJob;

public class DownloadJobCommand implements GricliCommand {
	private final String jobFilter;

	public DownloadJobCommand(String jobFilter) {
		this.jobFilter = jobFilter;
	}

	private void download(ServiceInterface si, DtoFileObject df, File dst)
			throws RemoteFileSystemException, IOException {
		Set<DtoFileObject> files = df.getChildren();
		for (DtoFileObject file : files) {
			DataHandler dh = si.download(file.getUrl());
			InputStream in = dh.getInputStream();
			FileOutputStream fout = new FileOutputStream(FilenameUtils.concat(
					dst.getCanonicalPath(), file.getName()));
			byte buf[] = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				fout.write(buf, 0, len);
			}
			fout.close();
			in.close();
		}

		Set<DtoFileObject> folders = df.getChildren();
		for (DtoFileObject folder : folders) {
			File dst2 = new File(FilenameUtils.concat(dst.getCanonicalPath(),
					folder.getName()));
			dst2.mkdir();
			download(si, folder, dst2);
		}
	}

	private void downloadDir(String src, String dst, ServiceInterface si)
			throws GricliRuntimeException {
		try {
			File dir = new File(dst);
			dir.mkdir();

			download(si, si.ls(src, 1), dir);
		} catch (RemoteFileSystemException ex) {
			throw new GricliRuntimeException(
					" cannot access remote file system: " + ex.getMessage());
		} catch (IOException ex) {
			throw new GricliRuntimeException(
					"cannot access local file system: " + ex.getMessage());
		}

	}

	private void downloadJob(ServiceInterface si, String jobname, String dst)
			throws GricliRuntimeException {
		try {
			System.out.println("downloading job " + jobname);
			DtoJob job = si.getJob(jobname);
			downloadDir(job.jobProperty("jobDirectory"), dst, si);
		} catch (NoSuchJobException ex) {
			throw new GricliRuntimeException("job " + jobname
					+ " does not exist");
		}
	}

	public GricliEnvironment execute(GricliEnvironment env)
			throws GricliRuntimeException {

		ServiceInterface si = env.getServiceInterface();
		String normalDirName = StringUtils.replace(env.get("dir"), "~",
				System.getProperty("user.home"));
		for (String jobname : ServiceInterfaceUtils.filterJobNames(si,
				jobFilter)) {
			downloadJob(si, jobname,
					FilenameUtils.concat(normalDirName, jobname));
		}
		return env;

	}

}
