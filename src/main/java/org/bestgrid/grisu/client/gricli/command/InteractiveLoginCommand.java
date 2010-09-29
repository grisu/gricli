package org.bestgrid.grisu.client.gricli.command;

import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.GricliRuntimeException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;

public class InteractiveLoginCommand implements GricliCommand {
    private final String backend;

    public InteractiveLoginCommand(String backend){
        this.backend = backend;
    }

    public GricliEnvironment execute(GricliEnvironment env) throws GricliRuntimeException {
        try {
            ServiceInterface si = LoginManager.loginCommandline(backend);
            env.setServiceInterface(si);
            return env;
        } catch (LoginException ex) {
           throw new GricliRuntimeException(ex);
        }
    }

}
