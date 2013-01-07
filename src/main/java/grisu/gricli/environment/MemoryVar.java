package grisu.gricli.environment;

import grisu.gricli.GricliSetValueException;
import grisu.jcommons.utils.MemoryUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.kenai.jaffl.provider.jffi.MemoryUtil;

public class MemoryVar extends ScalarVar<Integer> {


	public MemoryVar(String name, Integer value) {
		super(name, value, false);
	}

	@Override
	protected Integer fromString(String arg) throws GricliSetValueException {
		if (StringUtils.isBlank(arg)) {
			throw new GricliSetValueException(getName(), "null",
					"memory cannot be unset");
		}
		
		try {
			Long megabytes = MemoryUtils.fromStringToMegaBytes(arg);
			return megabytes.intValue();
		} catch (IllegalArgumentException iae) {
			throw new GricliSetValueException(getName(), arg, iae.getLocalizedMessage());
		} 
		
	}

	@Override
	public String marshall() {
		return "" + get();
	}

	@Override
	public String toString() {
		return String.format("%.2f GB", get() / 1024.0);
	}

}
