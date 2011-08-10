package grisu.gricli.completors;

import grisu.gricli.Gricli;
import grisu.gricli.GricliRuntimeException;
import grisu.gricli.environment.GricliEnvironment;
import grisu.jcommons.constants.Constants;
import grisu.model.info.ApplicationInformation;

import java.util.List;
import java.util.Set;

import jline.Completor;
import jline.SimpleCompletor;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class ExecutablesCompletor implements Completor {

	private static Logger myLogger = Logger
			.getLogger(ExecutablesCompletor.class.getName());

	public int complete(String s, int i, List list) {

		GricliEnvironment env = Gricli.completionCache.getEnvironment();
		String version;
		try {
			version = (String) env.getVariable("version").get();
		} catch (GricliRuntimeException e1) {
			e1.printStackTrace();
			return -1;
		}
		String fqan;
		try {
			fqan = (String) env.getVariable("group").get();
		} catch (GricliRuntimeException e1) {
			e1.printStackTrace();
			return -1;
		}

		if (StringUtils.isBlank(version)) {
			version = Constants.NO_VERSION_INDICATOR_STRING;
		}

		try {
			ApplicationInformation ai = env.getGrisuRegistry()
					.getApplicationInformation(
							(String) env.getVariable("application").get());



			Set<String> exes = ai.getExecutablesForVo(fqan, version);


			return new SimpleCompletor(exes.toArray(new String[] {})).complete(
					s, i, list);
		} catch (Exception e) {
			myLogger.error(e);
			return -1;
		}

	}

}
