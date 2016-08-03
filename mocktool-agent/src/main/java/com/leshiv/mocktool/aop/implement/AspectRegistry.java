package com.leshiv.mocktool.aop.implement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.leshiv.mocktool.aop.api.Aspect;
import com.leshiv.mocktool.util.MockToolLogFactory;
import com.leshiv.mocktool.util.StylishLogging;

import javassist.CtMethod;

public class AspectRegistry
{
	private static Logger LOG = MockToolLogFactory.getDefaultLogger();
	private static List<Aspect> aspects;

	static HashSet<String> ignoreClasses = new HashSet<String>();
	static HashSet<String> unignoreClasses = new HashSet<String>();
	static Map<String, Map<String, String>> paramsMap = new ConcurrentHashMap<>();

	static
	{
		ignoreClasses.add("com/leshiv/mocktool/");
		ignoreClasses.add("sun/");
		ignoreClasses.add("java/");
		ignoreClasses.add("com/sun/");
		ignoreClasses.add("javax/");
		ignoreClasses.add("net/sf/cglib/");
		ignoreClasses.add("org/javassist/javassist");
		ignoreClasses.add("org/objectweb/asm/");
		ignoreClasses.add("org/aspectj/");
		ignoreClasses.add("org/apache/");
		ignoreClasses.add("org/apache/log4j/");
		ignoreClasses.add("org/apache/jsp/");
		ignoreClasses.add("org/slf4j/");
	}

	static
	{
		aspects = new ArrayList<>();
	}

	public static void registerAspect(Aspect aspect)
	{
		synchronized (aspects)
		{
			LOG.info(StylishLogging.LINE_SEPERATOR + " " + StylishLogging.SPLIT_STRING);
			LOG.info(StylishLogging.LINE_SEPERATOR + "Register aspect : " + aspect.getId());
			StringBuilder sb = new StringBuilder();
			if (aspect.getParams() != null)
			{
				sb.append(StylishLogging.LINE_SEPERATOR + " params:");
				for (Entry<String, String> e : aspect.getParams().entrySet())
				{
					sb.append(StylishLogging.LINE_SEPERATOR + "  " + e.getKey() + " - " + e.getValue());
				}
				LOG.info(sb.toString());
			}
			aspects.add(aspect);
			//TODO:
			// if have parameters, generate an id for aspect for parameter lookup purpose inside instrumented methods later on;
			if (aspect.getParams() != null)
			{
				paramsMap.put(aspect.getId(), aspect.getParams());
			}

		}
	}

	public static void addIgnore(String name)
	{
		LOG.info("Ignore : " + name);
		ignoreClasses.add(name.replace(".", "/"));
	}

	public static void addUnIgnore(String name)
	{
		LOG.info("Unignore : " + name);
		unignoreClasses.add(name.replace(".", "/"));
	}

	/**
	 * Strict
	 * @param name
	 * @return
	 */
	public static boolean isUnignored(String name)
	{
		if (name.contains("$") || name.contains("#"))
			return false;

		for (String unignoreClass : unignoreClasses)
		{
			if (name.startsWith(unignoreClass))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean isIgnored(String name)
	{
		if (name == null || name.isEmpty() || name.contains("$") || name.contains("#"))
			return true;
		for (String unignoreClass : unignoreClasses)
		{
			if (name.startsWith(unignoreClass))
			{
				return false;
			}
		}
		for (String ignoreClass : ignoreClasses)
		{
			if (name.startsWith(ignoreClass))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean matchClass(ClassLoader loader, String className)
	{
		//TODO: match cache
		for (Aspect aspect : aspects)
		{
			if (aspect.getPointCut().getMatcher().matchClass(loader, className))
			{
				return true;
			}
		}
		return false;
	}

	//FIXME: better way to get a aspect
	public static Aspect matchCtMethod(ClassLoader loader, CtMethod ctMethod)
	{
		//TODO: match cache
		for (Aspect aspect : aspects)
		{
			if (aspect.getPointCut().getMatcher().matchCtMethod(loader, ctMethod))
			{
				return aspect;
			}
		}
		return null;
	}

	public static Map<String, String> getParams(String aspectId)
	{
		return paramsMap.get(aspectId);
	}

}
