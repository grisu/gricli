package grisu.gricli.command.exec;

import grisu.gricli.environment.GricliEnvironment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncProcessStreamReader {

	private static Logger myLogger = LoggerFactory
			.getLogger(AsyncProcessStreamReader.class.getName());

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
					final InputStreamReader isr = new InputStreamReader(
							process.getInputStream());
					final BufferedReader br = new BufferedReader(isr);
					String line = null;
					while ((line = br.readLine()) != null) {
						env.printMessage(line);
					}
				} catch (final IOException ioe) {
					myLogger.error(ioe.getLocalizedMessage(), ioe);
				}
			}
		};
		errThread = new Thread() {
			@Override
			public void run() {
				try {
					final InputStreamReader isr = new InputStreamReader(
							process.getErrorStream());
					final BufferedReader br = new BufferedReader(isr);
					String line = null;
					while ((line = br.readLine()) != null) {
						env.printError(line);
					}
				} catch (final IOException ioe) {
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
		} catch (final Exception e) {
			// e.printStackTrace();
		}
		outThread.join();
		errThread.join();

		return process.exitValue();
	}

}
