package com.leshiv.mocktool.aop.implement;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.leshiv.mocktool.aop.api.Advice;
import com.leshiv.mocktool.util.MockToolLogFactory;

public class AdviceRegistry
{
	private static Map<String, Advice> adviceInstances = new ConcurrentHashMap<String, Advice>();
	private static Logger LOG = MockToolLogFactory.getDefaultLogger();

	public static Advice getAdvice(ClassLoader loader, String adviceClassName)

	{
		if (loader == null)
		{
			loader = Thread.currentThread().getContextClassLoader();
		}
		Advice advice = adviceInstances.get(adviceClassName);
		if (advice == null)
		{
			try
			{
				Class<?> adviceClass = Class.forName(adviceClassName, true, loader);
				advice = (Advice) adviceClass.newInstance();
				adviceInstances.put(adviceClassName, advice);
			}
			catch (Throwable t)
			{
				LOG.log(Level.SEVERE, "Failed to get advice class : " + adviceClassName, t);
				t.printStackTrace();
			}
		}
		return advice;
	}

}
