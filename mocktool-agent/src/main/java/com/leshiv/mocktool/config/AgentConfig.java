package com.leshiv.mocktool.config;

import java.util.Map;
import java.util.logging.Logger;

import com.leshiv.mocktool.util.ConfigFileHelper;
import com.leshiv.mocktool.util.MockToolLogFactory;
import com.leshiv.mocktool.util.StylishLogging;

public class AgentConfig
{
	private static Logger LOG = MockToolLogFactory.getDefaultLogger();
	private static boolean agentEnabled;
	private static String version;
	private static String aopConfigFileName;
	//deserted
	private static String methodConfigFileName;
	private static boolean exportTransformedClasses;

	public static void init()
	{
		boolean success = true;
		try
		{
			Map<String, Object> props = ConfigFileHelper.getConfigSettings();
			agentEnabled = getBooleanProperty(props, "agentEnabled", true);
			version = getStringProperty(props, "version", "mta-0.1-20160602");
			aopConfigFileName = getStringProperty(props, "aopConfigFileName", "aop.xml");
			//deserted
			methodConfigFileName = getStringProperty(props, "methodConfigFileName", "methods");
			exportTransformedClasses = getBooleanProperty(props, "exportTransformedClasses", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		String successStr = success ? "SUCCESS" : "FAILED";
		LOG.info(StylishLogging.bannerString("initialize agent ".toUpperCase() + version + " : " + successStr));
	}

	public static boolean isAgentEnabled()
	{
		return agentEnabled;
	}

	public static String getVersion()
	{
		return version;
	}

	//deserted
	public static String getMethodConfigFileName()
	{
		return methodConfigFileName;
	}

	public static boolean isExportTransformedClasses()
	{
		return exportTransformedClasses;
	}

	public static String getAopConfigFileName()
	{
		return aopConfigFileName;
	}

	public static Object getProperty(Map<String, Object> props, String key, Object defaultVal)
	{
		Object propVal = props.get(key);
		if (propVal == null)
		{
			return defaultVal;
		}

		if (propVal instanceof String)
		{
			return ((String) propVal).trim();
		}
		return propVal;
	}

	public static String getStringProperty(Map<String, Object> props, String key, String defaultVal)
	{
		String propVal = (String) props.get(key);
		if (propVal == null)
		{
			return defaultVal;
		}

		return propVal.trim();
	}

	public static int getIntProperty(Map<String, Object> props, String key, int defaultVal)
	{
		Number val = (Number) props.get(key);
		if (val == null)
		{
			return defaultVal;
		}

		return val.intValue();
	}

	public static boolean getBooleanProperty(Map<String, Object> props, String key, Boolean defaultVal)
	{
		Boolean val = (Boolean) props.get(key);
		if (val == null)
		{
			return defaultVal;
		}

		return val.booleanValue();
	}
}
