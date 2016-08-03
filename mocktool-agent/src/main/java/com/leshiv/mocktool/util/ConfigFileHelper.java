package com.leshiv.mocktool.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ConfigFileHelper
{
	private static Logger LOG = MockToolLogFactory.getDefaultLogger();

	public static final String AGENT_PROP_FILE = "agent.properties";

	private static final Pattern BOOLEAN_PATTERN = Pattern.compile("^(?:true|True|TRUE|false|False|FALSE)$");
	private static final Pattern FLOAT_PATTERN = Pattern.compile(
			"^(?:[-+]?(?:[0-9][0-9_]*)\\.[0-9_]*(?:[eE][-+][0-9]+)?|[-+]?(?:[0-9][0-9_]*)?\\.[0-9_]+(?:[eE][-+][0-9]+)?|[-+]?[0-9][0-9_]*(?::[0-5]?[0-9])+\\.[0-9_]*|[-+]?\\.(?:inf|Inf|INF)|\\.(?:nan|NaN|NAN))$");
	private static final Pattern INTEGER_PATTERN = Pattern.compile(
			"^(?:[-+]?0b[0-1_]+|[-+]?0[0-7_]+|[-+]?(?:0|[1-9][0-9_]*)|[-+]?0x[0-9a-fA-F_]+|[-+]?[1-9][0-9_]*(?::[0-5]?[0-9])+)$");

	private static final String[] SEARCH_DIRECTORIES =
	{ ".", "conf", "config", "etc" };

	public static InputStream getConfigFile(String fileName) throws IOException
	{
		InputStream is = null;

		File configFile = findConfigFile(fileName);
		if (configFile != null)
		{
			LOG.info("Read config file path : " + configFile.getPath());
			is = new FileInputStream(configFile);
		}
		else
		{
			LOG.info("Read config file path : " + "/" + fileName);
			is = ConfigFileHelper.class.getResourceAsStream("/" + fileName);
		}

		return is;
	}

	public static Map<String, Object> getConfigSettings() throws IOException
	{
		InputStream is = getConfigFile(AGENT_PROP_FILE);

		Properties pros = new Properties();
		pros.load(is);
		is.close();

		Map<String, Object> map = new HashMap<String, Object>();
		for (Entry<Object, Object> entry : pros.entrySet())
		{
			String key = (String) entry.getKey();
			Object value = stringToObject((String) pros.get(key));
			map.put(key, value);
		}

		return map;
	}

	private static Object stringToObject(String str)
	{
		str = str.trim();
		if (BOOLEAN_PATTERN.matcher(str).matches())
		{
			return Boolean.valueOf(str);
		}
		if (FLOAT_PATTERN.matcher(str).matches())
		{
			return Float.valueOf(str);
		}
		if (INTEGER_PATTERN.matcher(str).matches())
		{
			return Integer.valueOf(Integer.parseInt(str));
		}
		return str;
	}

	public static File findConfigFile(String fileName)
	{
		File parentDir = JarHelper.getJarDirectory();

		if (parentDir != null)
		{
			File configFile = findConfigFile(parentDir, fileName);
			if (configFile != null)
			{
				return configFile;
			}
		}

		return findConfigFileInWorkingDirectory(fileName);
	}

	private static File findConfigFile(File parentDirectory, String fileName)
	{
		for (String searchDir : SEARCH_DIRECTORIES)
		{
			File configDir = new File(parentDirectory, searchDir);
			if (configDir.exists())
			{
				File configFile = new File(configDir, fileName);
				if (configFile.exists())
				{
					return configFile;
				}
			}
		}
		return null;
	}

	private static File findConfigFileInWorkingDirectory(String fileName)
	{
		File configFile = new File(fileName);
		if (configFile.exists())
		{
			return configFile;
		}
		else
		{
			configFile = new File("/" + fileName);
			if (configFile.exists())
			{
				return configFile;
			}
			return null;
		}
	}

}