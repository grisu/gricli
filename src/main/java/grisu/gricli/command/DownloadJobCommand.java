package grisu.gricli.command;

import grisu.control.ServiceInterface;
import grisu.control.exceptions.NoSuchJobException;
import grisu.control.exceptions.RemoteFileSystemException;
import grisu.gricli.GricliEnvironment;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.completors.JobnameCompletor;
import grisu.gricli.util.ServiceInterfaceUtils;
import grisu.model.dto.DtoJob;
import grisu.model.dto.GridFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.activation.DataHandler;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings("restriction")
public class DownloadJobCommand implements GricliCommand {
	private final String jobFilter;

	@SyntaxDescription(command="download job")
	@AutoComplete(completors={JobnameCompletor.class})
	public DownloadJobCommand(String jobFilter) {
		this.jobFilter = jobFilter;
	}

	private void download(ServiceInterface si, GridFile df, File dst)
			throws RemoteFileSystemException, IOException {
		Set<GridFile> files = df.getChildren();
		for (GridFile file : files) {
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

		Set<GridFile> folders = df.getChildren();
		for (GridFile folder : folders) {
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
		
		boolean hasError = false;

		ServiceInterface si = env.getServiceInterface();
		String normalDirName = StringUtils.replace(env.get("dir"), "~",
				System.getProperty("user.home"));
		for (String jobname : ServiceInterfaceUtils.filterJobNames(si,
				jobFilter)) {
			
			try {
			downloadJob(si, jobname,
					FilenameUtils.concat(normalDirName, jobname));
			} 
			catch (GricliRuntimeException ex){
				hasError = true;
				env.printError(ex.getMessage());
			}
		}
		if (hasError){
			throw new GricliRuntimeException("download command was unsuccessful");
		}
		return env;

	}

}
