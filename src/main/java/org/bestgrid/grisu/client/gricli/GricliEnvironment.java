
package org.bestgrid.grisu.client.gricli;

import java.util.HashMap;
import java.util.Set;
import org.vpac.grisu.control.ServiceInterface;


public class GricliEnvironment {

    private ServiceInterface si;
    private String siUrl;
    private HashMap<String,String> globals = new HashMap<String,String>();

    public GricliEnvironment(String configFile){
        globals.put("queue", null);
        globals.put("walltime","10");
        globals.put("jobname","gricli");
        globals.put("cpus","1");
        globals.put("memory","2048");
        globals.put("fqan", "/ARCS");
        globals.put("host", null);
        globals.put("gdir", "/");
        globals.put("email","y.halytskyy@auckland.ac.nz");
    }

    public String get(String global){
        return globals.get(global);
    }

    public Set<String> getGlobalNames(){
        return globals.keySet();
    }

    public void put(String global, String value) throws  GricliException{
        if (globals.containsKey(global)){
            globals.put(global, value);
        } else {
            throw new GricliException(global + " global variable does not exist");
        }

    }

    public ServiceInterface getServiceInterface() {
        return si;
    }

    public void setServiceInterface(ServiceInterface si) {
        this.si = si;
    }

    public String getServiceInterfaceUrl() {
        return siUrl;
    }

    public void setServiceInterfaceUrl(String siUrl) {
        this.siUrl = siUrl;
    }


}
