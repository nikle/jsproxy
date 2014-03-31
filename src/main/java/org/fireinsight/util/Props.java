package org.fireinsight.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Common utility class for loading properties values.
 * 
 * @author Steven Gao
 */
public class Props {
	
	/** Default environment properties file to use */
	private static String defaultEnvProp = "/props/env/dev/scriptinsight.properties";
	
	/** Points to properties file once it is successfully loaded */
	private static Properties envProps;

	/** Load the properties file */
	private static void loadProperties() {
		try {
			InputStream rt = Props.class.getResourceAsStream(defaultEnvProp);
			envProps = new Properties();
			envProps.load(rt);
			rt.close();
		} catch (IOException e) {
			System.err.println("Error: Props: Cannot load regencegame.properties file :::"
							+ e);
		}

	}

	/** Getter method to retrieve a specific entry from properties file */
	public static String getProperty(String prop) {
		String propValues = null;
		if(envProps == null) {
			loadProperties();
		}
		if (envProps != null) {
			propValues = envProps.getProperty(prop);
		}
		return propValues;
	}

	/** Getter method to retrieve path for default environment properties file */
	public static String getDefaultPath() {
		return defaultEnvProp;
	}
}
