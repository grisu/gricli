package org.vpac.grisu.client.gricli;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.vpac.grisu.settings.Environment;

/**
 * some sensible defaults.
 **/
class DefaultConfiguration extends GrisuClientFileConfiguration {
	private final HashMap<String, String> commonProperties = new HashMap<String, String>();
	private final HashMap<String, String> jobProperties = new HashMap<String, String>();

	public DefaultConfiguration() {
		commonProperties.put("serviceInterfaceUrl",
				"https://grisu.vpac.org/grisu-ws/services/grisu");
	}

	@Override
	public String getCommonOption(String key) {
		return commonProperties.get(key);
	}

	@Override
	public String getJobOption(String key) {
		return jobProperties.get(key);
	}
}

/**
 * The default options for grisu command line client can be specified in INI
 * configuration file
 */
public class GrisuClientFileConfiguration {

	public final static String CONFIG_FILE_PATH = Environment
			.getGrisuDirectory() + File.separator + "gricli.config";
	private static HashMap<String, GrisuClientFileConfiguration> instances = new HashMap<String, GrisuClientFileConfiguration>();

	public static GrisuClientFileConfiguration getConfiguration(String path)
			throws ConfigurationException {
		path = (path == null) ? CONFIG_FILE_PATH : path;

		final File f = new File(path);
		if (!f.exists()) {
			return new DefaultConfiguration();
		}
		GrisuClientFileConfiguration instance = instances.get(path);
		if (instance == null) {
			instance = new GrisuClientFileConfiguration(path);
			instances.put(path, instance);
			return instance;
		} else {
			return instance;
		}
	}

	private HierarchicalINIConfiguration configuration = null;

	protected GrisuClientFileConfiguration() {
	}

	private GrisuClientFileConfiguration(String path)
			throws ConfigurationException {
		this.configuration = new HierarchicalINIConfiguration(path);
	}

	public String getCommonOption(String key) throws ConfigurationException {
		try {
			return (String) configuration.getProperty("common." + key);
		} catch (final NullPointerException ex) {
			return null;
		}
	}

	public String getJobOption(String key) throws ConfigurationException {
		try {
			return (String) configuration.getProperty("job." + key);
		} catch (final NullPointerException e) {
			return null;
		}
	}
}