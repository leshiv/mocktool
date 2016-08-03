package com.leshiv.mocktool.util;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.leshiv.mocktool.agent.Agent;

public class InstrumentationUtil
{
	public static void forceRedefinition(Instrumentation instrumentation, Class<?>... classes)
			throws ClassNotFoundException, UnmodifiableClassException
	{
		List<ClassDefinition> toRedefine = new ArrayList<ClassDefinition>();
		for (Class<?> clazz : classes)
		{
			String classResourceName = clazz.getName().replace('.', '/') + ".class";
			URL resource = clazz.getResource(classResourceName);
			if (resource == null)
			{
				resource = BootstrapLoader.get().getBootstrapResource(classResourceName);
			}

			if (resource != null)
			{
				try
				{
					byte[] classfileBuffer = Streams.read(resource.openStream(), true);

					toRedefine.add(new ClassDefinition(clazz, classfileBuffer));
				}
				catch (Exception e)
				{
					Agent.LOG.severe("Unable to redefine " + clazz.getName() + " " + e);
				}
			}
			else
			{
				Agent.LOG.severe("Unable to find resource to redefine " + clazz.getName());
			}
		}

		if (!toRedefine.isEmpty())
		{
			instrumentation.redefineClasses((ClassDefinition[]) toRedefine.toArray(new ClassDefinition[0]));
		}
	}
}