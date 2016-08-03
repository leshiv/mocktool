package com.leshiv.mocktool.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.leshiv.mocktool.aop.implement.MethodInformation;
import com.leshiv.mocktool.util.ConfigFileHelper;
import com.leshiv.mocktool.util.MockToolLogFactory;

/**
 * deserted
 *
 */
public class MethodConfig
{
	public static HashMap<String, List<MethodInformation>> targets = new HashMap<String, List<MethodInformation>>();
	static Logger LOG = MockToolLogFactory.getDefaultLogger();

	public static void init()
	{
		File methodsConfig = ConfigFileHelper.findConfigFile(AgentConfig.getMethodConfigFileName().trim());
		if (methodsConfig != null && methodsConfig.isFile())
		{
			readMethods(methodsConfig.getPath());
		}
		else
		{
			LOG.log(Level.WARNING, "Method config file not found : " + AgentConfig.getMethodConfigFileName());
		}
	}

	/**
	 * deserted.
	 */
	public static void readMethods(String path)
	{
		LOG.info("Read method configs from : " + path);
		if (path == null)
			path = "methods";
		File methods = new File(path);
		try (BufferedReader br = new BufferedReader(new FileReader(methods)))
		{
			String line;
			while ((line = br.readLine()) != null)
			{
				parseMethodLine(line);
			}
		}
		catch (FileNotFoundException e)
		{
			LOG.log(Level.WARNING, "Methods config file not found : " + path);
		}
		catch (IOException e)
		{
			LOG.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * boolean com.dummy.Dummy.verify(String,String) true
	 * @param line
	 */
	public static void parseMethodLine(String line)
	{
		if (line == null || line.isEmpty() || line.startsWith("#"))
			return;
		line = line.trim();
		String[] parts = line.split(" ");
		if (parts == null || parts.length < 2 || parts.length > 3)
		{
			LOG.log(Level.WARNING, "Invalid line:" + line);
		}
		else
		{
			try
			{
				LOG.info("add: " + line);
				MethodInformation mi;
				// regex
				String classMethodName = parts[1].split("\\(")[0];

				String className = classMethodName.substring(0, classMethodName.lastIndexOf("."));

				String methodName = classMethodName.substring(classMethodName.lastIndexOf(".") + 1);
				String[] params = null;
				// charSequence
				if (parts[1].contains("(") && ((parts[1].indexOf(")") - parts[1].indexOf("(") > 1)))
				{
					params = parts[1].substring(parts[1].indexOf("(") + 1, parts[1].indexOf(")")).split(",");
				}

				if (params != null)
					System.out.println(params.length);

				mi = new MethodInformation(methodName);
				mi.setParams(params);
				mi.setReturnType(parts[0]);
				if (parts.length == 3)
				{
					mi.setNewReturnValue(parts[2]);
				}
				else
				{
					// return ;
					mi.setNewReturnValue(" ");
				}

				if (targets.containsKey(className))
				{
					targets.get(className).add(mi);
				}
				else
				{
					List<MethodInformation> list = new ArrayList<MethodInformation>();
					list.add(mi);
					targets.put(className, list);
				}
			}
			catch (Exception e)
			{
				LOG.log(Level.WARNING, "Failed to parse line : " + line, e);
			}
		}
	}

}
