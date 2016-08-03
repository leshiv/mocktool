package com.leshiv.mocktool.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.leshiv.mocktool.agent.Agent;

public class JarHelper
{
	private static Logger LOG = MockToolLogFactory.getDefaultLogger();
	private static final Pattern AGENT_CLASS_NAME_PATTERN = Pattern
			.compile(Agent.class.getName().replace('.', '/') + ".class");

	public static URL getJarUrl()
	{
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		if ((classLoader instanceof URLClassLoader))
		{
			URL[] urls = ((URLClassLoader) classLoader).getURLs();
			for (URL localURL : urls)
			{
				if (localURL.getFile().contains("mocktool-agent") && localURL.getFile().endsWith(".jar"))
				{
					if (!findJarFileNames(localURL, AGENT_CLASS_NAME_PATTERN).isEmpty())
					{
						return localURL;
					}
				}
			}
		}
		return JarHelper.class.getProtectionDomain().getCodeSource().getLocation();
	}

	private static Collection<String> findJarFileNames(URL jarUrl, Pattern pattern)
	{
		JarFile jarFile = null;
		try
		{
			jarFile = getJarFile(jarUrl);
			Collection<String> names = new ArrayList<String>();
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements())
			{
				JarEntry jarEntry = (JarEntry) entries.nextElement();
				if (pattern.matcher(jarEntry.getName()).matches())
				{
					names.add(jarEntry.getName());
				}
			}
			return names;
		}
		catch (Exception e)
		{
			LOG.severe("Failed to search for " + pattern.pattern());
			return Collections.emptyList();
		}
		finally
		{
			if (jarFile != null)
			{
				try
				{
					jarFile.close();
				}
				catch (IOException e)
				{
				}
			}
		}
	}

	public static File getJarDirectory()
	{
		URL agentJarUrl = getJarUrl();
		if (agentJarUrl != null)
		{
			File file = new File(getJarFileName(agentJarUrl));
			if (file.exists())
			{
				return file.getParentFile();
			}
		}
		return null;
	}

	public static JarFile getJarFile(URL agentJarUrl)
	{
		if (agentJarUrl != null)
		{
			try
			{
				return new JarFile(getJarFileName(agentJarUrl));
			}
			catch (IOException e)
			{
			}
		}
		return null;
	}

	private static String getJarFileName(URL agentJarUrl)
	{
		if (agentJarUrl == null)
		{
			return null;
		}
		try
		{
			return URLDecoder.decode(agentJarUrl.getFile().replace("+", "%2B"), "UTF-8");
		}
		catch (IOException e)
		{
		}
		return null;
	}
}
