package grisu.gricli;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.log4j.Logger;

public class DefaultExceptionHandler implements UncaughtExceptionHandler {
	
	static final Logger myLogger = Logger.getLogger(DefaultExceptionHandler.class.getName());

	public void uncaughtException(Thread t, Throwable e) {
		myLogger.error(e);
		System.err.println("unexpected error occurred : " + e.getLocalizedMessage() );
	}

}
