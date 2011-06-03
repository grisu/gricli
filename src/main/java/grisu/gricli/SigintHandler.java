package grisu.gricli;

import sun.misc.Signal;
import sun.misc.SignalHandler;

@SuppressWarnings("restriction")
public class SigintHandler implements SignalHandler {

	private Thread t;
	
	public SigintHandler(Thread t){
		this.t = t;
	}

	public static SigintHandler install(String signalName, Thread t){
		
		SigintHandler s = new SigintHandler(t);
		Signal signal = new Signal(signalName);
		Signal.handle(signal, s);
		return s;
	}
	
	public void handle(Signal s) {
		t.interrupt();

	}

}
