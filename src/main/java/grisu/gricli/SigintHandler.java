package grisu.gricli;

import grisu.gricli.environment.GricliEnvironment;
import sun.misc.Signal;
import sun.misc.SignalHandler;

@SuppressWarnings("restriction")
public class SigintHandler implements SignalHandler {

	public static SigintHandler install(GricliEnvironment gricli) {

		final SigintHandler s = new SigintHandler(gricli);
		final Signal signal = new Signal("INT");
		s.oldHandler = Signal.handle(signal, s);
		return s;
	}

	private final GricliEnvironment gricli;

	private SignalHandler oldHandler;

	public SigintHandler(GricliEnvironment gricli) {
		this.gricli = gricli;
	}

	public void handle(Signal s) {
		// Chain back to previous handler, if one exists
		Gricli.shutdown(gricli);
		if (oldHandler != SIG_DFL && oldHandler != SIG_IGN) {
			oldHandler.handle(s);
		}
	}

}
