package com.leshiv.mocktool.util;

import java.util.logging.Logger;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class CtClassTypeHelper
{
	static Logger LOG = MockToolLogFactory.getDefaultLogger();

	public static CtClass typeNameToCtClass(String name, ClassPool classPool)
	{
		name = name.trim();
		if (!name.contains("."))
		{
			name = name.toLowerCase();
			if (name.equals("boolean"))
			{
				return CtClass.booleanType;
			}
			else if (name.equals("byte"))
			{
				return CtClass.byteType;
			}
			else if (name.equals("char"))
			{
				return CtClass.charType;
			}
			else if (name.equals("double"))
			{
				return CtClass.doubleType;
			}
			else if (name.equals("float"))
			{
				return CtClass.floatType;
			}
			else if (name.equals("int"))
			{
				return CtClass.intType;
			}
			else if (name.equals("long"))
			{
				return CtClass.longType;
			}
			else if (name.equals("short"))
			{
				return CtClass.shortType;
			}
			else if (name.equals("void"))
			{
				return CtClass.voidType;
			}
			else
			{
				LOG.severe("invalid return type :" + name);
			}
		}
		try
		{
			CtClass returnTypeClass = classPool.get(name);
			return returnTypeClass;
		}
		catch (NotFoundException e)
		{
			LOG.severe("Return type not found in class pool :" + name);
			e.printStackTrace();
		}
		return null;
	}
}
