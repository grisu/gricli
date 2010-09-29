package org.bestgrid.grisu.client.gricli;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

public enum GricliVar {
    QUEUE ("queue"){
    	@Override public void setValue(String value) throws GricliSetValueException {
    		if ("null".equals(value)){
    			super.setValue(null);
    		} else {
    			super.setValue(value);
    		}
    		
    	}
    },
    WALLTIME("walltime"),
    JOBNAME("jobname"),
    CPUS("cpus"),
    JOBTYPE("jobtype"){
      @Override public void setValue(String value) throws GricliSetValueException {
          if (!"mpi".equals(value) && !"single".equals(value) && !"threaded".equals(value)){
              throw new GricliSetValueException("jobtype",value,"must be one of 'mpi', 'single' or 'threaded'");
          }
          super.setValue(value);
      }
    },
    MEMORY("memory"){
        @Override public void setValue(String value) throws GricliSetValueException{
            try {
                int ivalue = Integer.parseInt(value);
                if (ivalue < 0){
                    throw new GricliSetValueException("memory",value,"cannot be negative");
                }
                super.setValue(value);

            }
            catch (NumberFormatException ex){
                throw new GricliSetValueException("memory", value,"must be a number");
            }
        }

    },
    FQAN("fqan"),
    HOST("host"),
    GRID_DIR("gdir"),
    LOCAL_DIR("dir"){
        // check if directory exists and valid pathname 
        @Override public void setValue(String value) throws GricliSetValueException{
            try {
                File dir = new File(value);
                if (!dir.exists()){
                    throw new GricliSetValueException("dir", dir.getCanonicalPath(), "directory does not exist");
                }
                String resultValue = StringUtils.replace(dir.getCanonicalPath(), System.getProperty("user.home"), "~");
                System.setProperty("user.dir", value);
                super.setValue(resultValue);
            } catch (IOException ex){
                throw new GricliSetValueException("dir",value, ex.getMessage());
            }
        }
    },
    EMAIL("email"),
    PROMPT("prompt"),
    ATTACHED_FILES("files"){
        private LinkedList<String> list = new LinkedList<String>();
        @Override public void setValue(String value) throws GricliSetValueException{
            clear();
            add(value);
        }
        @Override public String getValue(){
            if (this.list == null){
                return null;
            }
            return "[" + StringUtils.join(list,",") + "]";
        }
        @Override public void clear() throws GricliSetValueException{
            list = new LinkedList<String>();
        }
        @Override public void add(String value) throws GricliSetValueException{
            list.add(value);
        }
        @Override public List<String> getList(){
            return new LinkedList<String>(list);
        }
        @Override public boolean isList(){return true;}
    };
    protected String var;
    protected String value;

    private static final Map<String, GricliVar> lookup =
            new HashMap<String,GricliVar>();

    static {
        for (GricliVar v: EnumSet.allOf(GricliVar.class)){
            lookup.put(v.var, v);
        }
    }

    GricliVar(String var){
        this.var = var;
    }

    public void setValue(String value) throws GricliSetValueException{
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }

    public void clear() throws GricliSetValueException{
        throw new GricliSetValueException(this.var,"[]","variable not a list");
    }

    public void add(String value) throws GricliSetValueException {
        throw new GricliSetValueException(this.var,"[]","cannot add value to variable that is not a list");
    }

    public List<String> getList(){
        return null;
    }

    public boolean isList(){
        return false;
    }

    public static GricliVar get(String var){
        return lookup.get(var);
    }

    public static Set<String> allValues(){
        return lookup.keySet();
    }
}
