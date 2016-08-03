package com.leshiv.mocktool.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import com.leshiv.mocktool.agent.Agent;

public abstract class BootstrapLoader
{
	private static final BootstrapLoader loader = create();

	public static BootstrapLoader get()
	{
		return loader;
	}

	private static BootstrapLoader create()
	{
		try
		{
			return new BootstrapLoaderImpl();
		}
		catch (Exception e)
		{
			Agent.LOG.info("IBM sysClassLoader property: " + System.getProperty("systemClassLoader"));
			try
			{
				return new IBMBootstrapLoader();
			}
			catch (Exception ex)
			{
				Agent.LOG.severe("IBM Bootstrap loader lookup failed :" + ex);
				Agent.LOG.severe("Error getting bootstrap classloader :" + e);
			}
		}
		return new BootstrapLoader() {
			public URL getBootstrapResource(String name)
			{
				return null;
			}

			public boolean isBootstrapClass(String internalName)
			{
				return internalName.startsWith("java/");
			}
		};
	}

	public boolean isBootstrapClass(String internalName)
	{
		return getBootstrapResource(internalName + ".class") != null;
	}

	public abstract URL getBootstrapResource(String paramString);

	private static class BootstrapLoaderImpl extends BootstrapLoader
	{
		private final Method getBootstrapResourceMethod;

		private BootstrapLoaderImpl() throws NoSuchMethodException, SecurityException, IllegalAccessException,
				IllegalArgumentException, InvocationTargetException
		{
			this.getBootstrapResourceMethod = ClassLoader.class.getDeclaredMethod("getBootstrapResource", new Class[]
			{ String.class });
			this.getBootstrapResourceMethod.setAccessible(true);
			this.getBootstrapResourceMethod.invoke(null, new Object[]
			{ "dummy" });
		}

		public URL getBootstrapResource(String name)
		{
			try
			{
				return (URL) this.getBootstrapResourceMethod.invoke(null, new Object[]
				{ name });
			}
			catch (Exception e)
			{
				Agent.LOG.severe(e.toString());
			}
			return null;
		}
	}

	private static class IBMBootstrapLoader extends BootstrapLoader
	{
		private static final List<String> BOOTSTRAP_CLASSLOADER_FIELDS = Arrays.asList("bootstrapClassLoader",
				"systemClassLoader");
		private final ClassLoader bootstrapLoader;

		public IBMBootstrapLoader() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
		{
			Field field = getBootstrapField();
			field.setAccessible(true);
			ClassLoader cl = (ClassLoader) field.get(null);
			Agent.LOG.info("Initializing IBM BootstrapLoader");
			this.bootstrapLoader = cl;
		}

		private Field getBootstrapField() throws NoSuchFieldException
		{
			for (String fieldName : BOOTSTRAP_CLASSLOADER_FIELDS)
			{
				try
				{
					Agent.LOG.info("Searching for java.lang.ClassLoader." + fieldName);
					return ClassLoader.class.getDeclaredField(fieldName);
				}
				catch (Exception e)
				{
				}
			}
			throw new NoSuchFieldException(MessageFormat.format("No bootstrap fields found: {0}", new Object[]
			{ BOOTSTRAP_CLASSLOADER_FIELDS }));
		}

		public URL getBootstrapResource(String name)
		{
			return this.bootstrapLoader.getResource(name);
		}
	}
}