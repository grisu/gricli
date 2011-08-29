package grisu.gricli.environment;

import grisu.gricli.GricliSetValueException;

import java.util.ArrayList;
import java.util.List;

public abstract class GricliVar<T> implements Comparable<GricliVar> {

	private final String name;
	private T value;
	private final List<GricliVarListener<T>> listeners;
	private boolean persistent = true;


	public GricliVar(String name){
		this.name = name;
		this.listeners = new ArrayList<GricliVarListener<T>>();
	}

	public void addListener(GricliVarListener<T> l) {
		listeners.add(l);
	}

	public int compareTo(GricliVar o) {
		return getName().compareTo(o.getName());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof GricliVar) {
			GricliVar other = (GricliVar)o;
			return getName().equals(other.getName());
		} else {
			return false;
		}
	}

	protected abstract T fromStrings(String[] args) throws GricliSetValueException;

	public T get(){
		return this.value;
	}

	public String getName(){
		return this.name;
	}

	@Override
	public int hashCode(){
		return getName().hashCode();
	}

	public void set(String[] args) throws GricliSetValueException{
		set(fromStrings(args));
	}

	public void set(T value) throws GricliSetValueException {
		this.value = value;
		for (GricliVarListener<T> listener: listeners){
			listener.valueChanged(this.value);
		}
	}

	@Override
	public String toString(){
		return value.toString();
	}
	
	protected void setPersistent(boolean persistent){
		this.persistent = persistent;
	}
	
	public String marshall(){
		return toString();
	}
	
	public boolean isPersistent(){
		return this.persistent;
	}


}
