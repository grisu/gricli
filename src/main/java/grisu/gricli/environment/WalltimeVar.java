package grisu.gricli.environment;

import grisu.gricli.GricliSetValueException;
import grisu.utils.WalltimeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class WalltimeVar extends ScalarVar<Integer> {

	public WalltimeVar(String name, Integer value) {
		super(name, value);
	}

	@Override
	protected Integer fromString(String arg) throws GricliSetValueException {
		if (StringUtils.isBlank(arg)) {
			throw new GricliSetValueException(getName(), "null",
					"cannot be unset");
		}
		int ivalue = 0;
		try {
			ivalue = Integer.parseInt(arg);
		} catch (final NumberFormatException ex) {
			final Pattern date = Pattern
					.compile("([0-9]+[dD])?([0-9]+[hH])?([0-9]+[mM])?");
			final Matcher m = date.matcher(arg);
			if (!m.matches()) {
				throw new GricliSetValueException(getName(), arg,
						"not a valid date format");
			}
			String days = m.group(1);
			String hours = m.group(2);
			String minutes = m.group(3);

			days = (days == null) ? "0" : days.toLowerCase().replace("d", "");
			hours = (hours == null) ? "0" : hours.toLowerCase()
					.replace("h", "");
			minutes = (minutes == null) ? "0" : minutes.toLowerCase().replace(
					"m", "");

			try {
				ivalue = (Integer.parseInt(days) * 1440)
						+ (Integer.parseInt(hours) * 60)
						+ Integer.parseInt(minutes);

			} catch (final NumberFormatException ex2) {
				throw new GricliSetValueException(getName(), arg,
						"not valid date format");
			}

		}

		if (ivalue < 0) {
			throw new GricliSetValueException(getName(), arg,
					"must be positive");
		}
		return ivalue;
	}

	@Override
	public String marshall() {
		return "" + get();
	}

	@Override
	public String toString() {
		return StringUtils.join(
				WalltimeUtils.convertSecondsInHumanReadableString(get() * 60),
				" ");
	}

}
