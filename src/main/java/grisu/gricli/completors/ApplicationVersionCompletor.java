package grisu.gricli.completors;

import grisu.gricli.Gricli;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.constants.Constants;
import grisu.model.info.ApplicationInformation;
import grisu.model.info.dto.Version;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jline.Completor;
import jline.SimpleCompletor;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Functions;
import com.google.common.collect.Collections2;

public class ApplicationVersionCompletor implements Completor {

	private final SimpleCompletor anyCompletor = new SimpleCompletor(
			new String(Constants.NO_VERSION_INDICATOR_STRING));

	public ApplicationVersionCompletor() {
	}

	public int complete(String arg0, int arg1, List arg2) {

		final GricliEnvironment env = Gricli.completionCache.getEnvironment();
		final String app = env.application.get();
		// String queue = env.get("queue");
		final String fqan = env.group.get();

		if (StringUtils.isBlank(fqan)) {
			// return anyCompletor.complete(arg0, arg1, arg2);
			return -1;
		}
		if (StringUtils.isBlank(app)
				|| Constants.GENERIC_APPLICATION_NAME.equals(app)) {
			// no versions here
			// return anyCompletor.complete(arg0, arg1, arg2);
			return -1;
		} else {
			final ApplicationInformation ai = env.getGrisuRegistry()
					.getApplicationInformation(app);

			final Set<Version> versions = ai
					.getAllAvailableVersionsForFqan(fqan);
			final List<String> v = new LinkedList<String>(
					Collections2.transform(versions,
							Functions.toStringFunction()));
			Collections.sort(v);
			v.add(0, Constants.NO_VERSION_INDICATOR_STRING);
			return new SimpleCompletor(v.toArray(new String[] {})).complete(
					arg0, arg1, arg2);

		}

	}

}
