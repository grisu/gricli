package grisu.gricli;


import grisu.gricli.environment.GricliEnvironment;
import sun.misc.Signal;
import sun.misc.SignalHandler;

@SuppressWarnings("restriction")
public class SigintHandler implements SignalHandler {
	
	private GricliEnvironment gricli;
	private SignalHandler oldHandler;

	public SigintHandler(GricliEnvironment gricli){
		this.gricli = gricli;
	}

	public static SigintHandler install(GricliEnvironment gricli){
		
		SigintHandler s = new SigintHandler(gricli);
		Signal signal = new Signal("INT");
		s.oldHandler = Signal.handle(signal, s);
		return s;
	}

	public void handle(Signal s) {
		// Chain back to previous handler, if one exists
		Gricli.shutdown(gricli);
		if (oldHandler != SIG_DFL && oldHandler != SIG_IGN) {
			oldHandler.handle(s);
		}
	}
	

}
