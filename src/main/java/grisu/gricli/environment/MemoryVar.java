package grisu.gricli.environment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import grisu.gricli.GricliSetValueException;

public class MemoryVar extends ScalarVar<Integer> {
	
	public MemoryVar(String name, Integer value){
		super(name,value);
	}

	@Override
	protected Integer fromString(String arg) throws GricliSetValueException {
		int imem = 0;
		try {
			imem = Integer.parseInt(arg);
		} catch (NumberFormatException ex){
			Pattern mp = Pattern.compile("([0-9]+[gG])?([0-9]+[mM])?([0-9]+[kK])?");
			Matcher m = mp.matcher(arg);
			if (!m.matches()){
				throw new GricliSetValueException(getName(),arg,"not a valid memory format");
			}

                        String gb = m.group(1);
                        String mb = m.group(2);
                        String kb = m.group(3);

                        gb = (gb == null)?"0":gb.toLowerCase().replace("g", "");
                        mb = (mb == null)?"0":mb.toLowerCase().replace("m", "");
                        kb = (kb == null)?"0":kb.toLowerCase().replace("k", "");
			
			try {
				imem = (Integer.parseInt(gb) * 1024) + (Integer.parseInt(mb) + (Integer.parseInt(kb) / 1024));
			} catch (NumberFormatException ex2){
				throw new GricliSetValueException(getName(),arg,"not valid memory format");
			}
		}
		
		if (imem < 0 ){
			throw new GricliSetValueException(getName(),arg, "must be positive");
		}
		
		return imem;
	}
	
	public String toString() {
		return String.format("%.2f GB", get() / 1024.0);
	}

}
