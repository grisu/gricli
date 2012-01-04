package grisu.gricli;

import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.ws.client.ClientTransportException;

public class DefaultExceptionHandler implements UncaughtExceptionHandler {

	static final Logger myLogger = LoggerFactory
			.getLogger(DefaultExceptionHandler.class.getName());

	public void uncaughtException(Thread t, Throwable e) {
		myLogger.error(e.getLocalizedMessage(), e);

		if (e instanceof ClientTransportException) {
			if (e.getLocalizedMessage().contains("401")) {
				System.err
				.println("Authorization problem. Most likely your credential expired. Use login command to create new one.");
			}
		} else {
			System.err.println("unexpected error occurred : "
					+ e.getLocalizedMessage());
		}
	}

}
