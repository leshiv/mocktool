package com.leshiv.mocktool.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.logging.Logger;

import com.leshiv.mocktool.agent.Agent;
import com.leshiv.mocktool.util.JarHelper;
import com.leshiv.mocktool.util.MockToolLogFactory;
import com.leshiv.mocktool.util.Streams;
import com.leshiv.mocktool.util.StylishLogging;

public class ExtensionManager
{
	private static Logger LOG = MockToolLogFactory.getDefaultLogger();
	private static final String[] JAR_FILE_NAMES =
	{ "mocktool" };

	private static Map<String, Class<?>> clazzLoaderMap = new HashMap<String, Class<?>>();
	private static Map<String, byte[]> clazzBytesMap = new HashMap<String, byte[]>();
	private static List<JarFile> jarFiles = new ArrayList<>();

	public static boolean isExtensionClass(String name)
	{
		return name.startsWith("com.leshiv.mocktool.extension.");
	}

	public static boolean isAgentClass(String name)
	{
		return name.startsWith("com.leshiv.mocktool.agent");
	}

	public static Class<?> getClazz(String className)
	{
		return clazzLoaderMap.get(className);
	}

	public static void setClazzLoaderMap(String className, Class<?> clazz)
	{
		clazzLoaderMap.put(className, clazz);
	}

	public static byte[] getClazzBytes(String className)
	{
		return clazzBytesMap.get(className);
	}

	public static void addToClassPath(Instrumentation inst)
	{
		for (JarFile jf : jarFiles)
		{
			LOG.info("Add to class path: " + jf.getName());
			inst.appendToSystemClassLoaderSearch(jf);
		}
	}

	public static void init(Instrumentation inst)
	{
		boolean success = true;
		File agentJarDirectory = JarHelper.getJarDirectory();
		Agent.LOG.info("Agent jar directory : " + agentJarDirectory.toString());
		String[] jars = agentJarDirectory.list();

		for (String jarNameWithoutExtension : JAR_FILE_NAMES)
		{
			for (String jarName : jars)
			{
				if (jarName.startsWith(jarNameWithoutExtension) && jarName.endsWith(".jar"))
				{
					File file = new File(agentJarDirectory, jarName);
					InputStream inputStream = null;
					try
					{
						jarFiles.add(new JarFile(file));
						inputStream = new FileInputStream(file);
						JarInputStream jarStream = new JarInputStream(inputStream);
						JarEntry entry = null;
						while ((entry = jarStream.getNextJarEntry()) != null)
						{
							if (entry.getName().endsWith(".class"))
							{
								byte[] bytes = Streams.read(jarStream, (int) entry.getSize(), false);
								String name = entry.getName().substring(0, entry.getName().length() - 6)
										.replace('/', '.');
								clazzBytesMap.put(name, bytes);
							}
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						success = false;
					}
					finally
					{
						try
						{
							if (inputStream != null)
							{
								inputStream.close();
							}
						}
						catch (IOException e)
						{
						}
					}
				}
			}
		}

		addToClassPath(inst);

		String successStr = success ? "SUCCESS" : "FAILED";
		LOG.info(StylishLogging.bannerString("load extension jar(s) : ".toUpperCase() + successStr));
	}
}
