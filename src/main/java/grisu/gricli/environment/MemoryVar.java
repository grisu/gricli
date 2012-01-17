package grisu.gricli.environment;

import grisu.gricli.GricliSetValueException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

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
		int imem = 0;
		try {
			imem = Integer.parseInt(arg);
		} catch (final NumberFormatException ex) {
			final Pattern mp = Pattern
					.compile("([0-9]+[gG])?([0-9]+[mM])?([0-9]+[kK])?");
			final Matcher m = mp.matcher(arg);
			if (!m.matches()) {
				throw new GricliSetValueException(getName(), arg,
						"not a valid memory format");
			}

			String gb = m.group(1);
			String mb = m.group(2);
			String kb = m.group(3);

			gb = (gb == null) ? "0" : gb.toLowerCase().replace("g", "");
			mb = (mb == null) ? "0" : mb.toLowerCase().replace("m", "");
			kb = (kb == null) ? "0" : kb.toLowerCase().replace("k", "");

			try {
				imem = (Integer.parseInt(gb) * 1024)
						+ (Integer.parseInt(mb) + (Integer.parseInt(kb) / 1024));
			} catch (final NumberFormatException ex2) {
				throw new GricliSetValueException(getName(), arg,
						"not valid memory format");
			}
		}

		if (imem < 0) {
			throw new GricliSetValueException(getName(), arg,
					"must be positive");
		}

		return imem;
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
