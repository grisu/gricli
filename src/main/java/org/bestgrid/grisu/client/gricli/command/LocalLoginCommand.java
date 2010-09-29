package org.bestgrid.grisu.client.gricli.command;

import org.bestgrid.grisu.client.gricli.GricliEnvironment;
import org.bestgrid.grisu.client.gricli.GricliRuntimeException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;

public class LocalLoginCommand implements GricliCommand{
    private String siUrl;

    public LocalLoginCommand(String siUrl){
        this.siUrl = siUrl;
    }

    public GricliEnvironment execute(GricliEnvironment env) throws GricliRuntimeException{
        try {
            if (siUrl == null){
                siUrl = env.getServiceInterfaceUrl();
            }
            ServiceInterface serviceInterface = LoginManager.login(siUrl);
            env.setServiceInterface(serviceInterface);
            return env;
        } catch (LoginException ex) {
            throw new GricliRuntimeException(ex);
        }
    }

}
