package grisu.gricli.environment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import grisu.gricli.GricliSetValueException;
import grisu.utils.WalltimeUtils;

public class WalltimeVar extends ScalarVar<Integer> {
	

	public WalltimeVar(String name, Integer value) {
		super(name, value);
	}

	@Override
	protected Integer fromString(String arg) throws GricliSetValueException {
		if (arg == null){
			throw new GricliSetValueException(getName(),"null","cannot be unset");
		}
		int ivalue = 0;
		try {
			ivalue = Integer.parseInt(arg);
		} catch (NumberFormatException ex){
			Pattern date = Pattern.compile("([0-9]+[dD])?([0-9]+[hH])?([0-9]+[mM])?");
			Matcher m = date.matcher(arg);
			if (!m.matches()){
				throw new GricliSetValueException(getName(),arg,"not a valid date format");
			}
			String days = m.group(1);
			String hours = m.group(2);
			String minutes = m.group(3);

			days = (days == null)?"0":days.toLowerCase().replace("d","");
			hours = (hours == null)?"0":hours.toLowerCase().replace("h","");
			minutes = (minutes == null)?"0":minutes.toLowerCase().replace("m","");

			try {
				ivalue = (Integer.parseInt(days) * 1440) +
						(Integer.parseInt(hours) * 60) +
						Integer.parseInt(minutes);

			} catch (NumberFormatException ex2){
				throw new GricliSetValueException(getName(),arg,"not valid date format");
			}

		}

		if (ivalue < 0){
			throw new GricliSetValueException(getName(),arg, "must be positive");
		}	
		return ivalue;
	}
	
	public String toString(){
		return StringUtils.join(WalltimeUtils.convertSecondsInHumanReadableString(get() * 60)," ");
	}
	
	public String marshall(){
		return "" + get();
	}

}
