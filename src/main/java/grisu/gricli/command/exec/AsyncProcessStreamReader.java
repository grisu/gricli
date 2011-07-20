package grisu.gricli.command.exec;

import grisu.gricli.GricliEnvironment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AsyncProcessStreamReader {

	private final GricliEnvironment env;
	private final Process process;

	private Thread outThread = null;
	private Thread errThread = null;

	public AsyncProcessStreamReader(GricliEnvironment env, Process process) {
		this.env = env;
		this.process = process;
		start();
	}

	private void start() {

		outThread = new Thread() {
			@Override
			public void run() {
				try {
					InputStreamReader isr = new InputStreamReader(
							process.getInputStream());
					BufferedReader br = new BufferedReader(isr);
					String line = null;
					while ((line = br.readLine()) != null) {
						env.printMessage(line);
					}
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		};
		errThread = new Thread() {
			@Override
			public void run() {
				try {
					InputStreamReader isr = new InputStreamReader(
							process.getErrorStream());
					BufferedReader br = new BufferedReader(isr);
					String line = null;
					while ((line = br.readLine()) != null) {
						env.printError(line);
					}
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		};
		outThread.start();
		errThread.start();

	}

	public int waitForProcessToFinish() throws InterruptedException {
		try {
			process.waitFor();
		} catch (Exception e) {
			// e.printStackTrace();
		}
		outThread.join();
		errThread.join();

		return process.exitValue();
	}

}
