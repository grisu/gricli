package grisu.gricli.environment;

import grisu.gricli.GricliSetValueException;

import java.util.ArrayList;
import java.util.List;

public abstract class GricliVar<T> {
	
	private String name;
	private T value;
	private List<GricliVarListener<T>> listeners;
	

	public GricliVar(String name){
		this.name = name;
		this.listeners = new ArrayList<GricliVarListener<T>>();
	}
	
	protected abstract T fromStrings(String[] args) throws GricliSetValueException;
	
	public String getName(){
		return this.name;
	}
	
	public T get(){
		return this.value;
	}
	
	public void addListener(GricliVarListener<T> l){
		listeners.add(l);
	}
	
	public void set(T value) throws GricliSetValueException {
		this.value = value;
		for (GricliVarListener<T> listener: listeners){
			listener.valueChanged(this.value);
		}
	}
	
	public void set(String[] args) throws GricliSetValueException{
		set(fromStrings(args));
	}
	
	public String toString(){
		return value.toString();
	}
	
	
}
