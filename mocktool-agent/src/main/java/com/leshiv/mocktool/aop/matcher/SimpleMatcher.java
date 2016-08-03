package com.leshiv.mocktool.aop.matcher;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.leshiv.mocktool.aop.api.Matcher;
import com.leshiv.mocktool.aop.implement.MethodInformation;
import com.leshiv.mocktool.util.MockToolLogFactory;
import com.leshiv.mocktool.util.StringUtil;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.SignatureAttribute.MethodSignature;

/**
 * PointCut expression matcher
 *
 */
public class SimpleMatcher implements Matcher
{
	public static Logger LOG = MockToolLogFactory.getDefaultLogger();
	private static ClassPool classPool = ClassPool.getDefault();

	String expression;
	MethodInformation methodInfo;
	MethodSignature ms;

	public SimpleMatcher(String expression)
	{
		parseMethodLine(expression);
	}

	@Override
	public boolean matchClass(ClassLoader loader, String className)
	{
		return matchClass(loader, className, methodInfo.getClassName());
	}

	private boolean matchClass(ClassLoader loader, String targetClassName, String matchClassExpression)
	{
		if (classPool == null)
			classPool = ClassPool.getDefault();
		classPool.appendClassPath(new LoaderClassPath(loader));
		String matchClassName = matchClassExpression;

		if (matchClassName.endsWith("+"))
		{
			matchClassName = matchClassName.substring(0, matchClassName.length() - 1);
		}
		else
		{
			//wildcard match
			return targetClassName.matches(StringUtil.wildcardToRegex(matchClassName));
		}

		CtClass targetClass = null;
		CtClass matchClass = null;
		try
		{
			targetClass = classPool.get(targetClassName);
		}
		catch (NotFoundException e)
		{
			// no stack trace output
			//			LOG.log(Level.WARNING, "Target class for matching not found : " + targetClassName,e);
			return false;
		}

		try
		{
			matchClass = classPool.get(matchClassName);
		}
		catch (NotFoundException e)
		{
			//			LOG.log(Level.WARNING, "Matching class not found, check your pointcut expression : " +targetClassName+" - "+ matchClassName, e);
			return false;
		}

		try
		{
			return targetClass.subtypeOf(matchClass) || targetClass.equals(matchClass);
		}
		catch (NotFoundException e)
		{
			LOG.log(Level.WARNING, "Subtype not found : " + targetClassName + " - " + matchClassName, e);
			return false;
		}
		finally
		{
			targetClass.detach();
		}

	}

	@Override
	public boolean matchMethod(ClassLoader loader, String methodName)
	{
		return methodName.matches(StringUtil.wildcardToRegex(methodInfo.getMethodName()));
	}

	@Override
	public boolean matchCtMethod(ClassLoader loader, CtMethod method)
	{
		classPool = ClassPool.getDefault();
		classPool.appendClassPath(new LoaderClassPath(loader));

		// check class name and method name
		// match method name first for performance improvement
		if (method == null || method.getName() == null)
		{
			System.out.println("NULL METHOD ");
			return false;
		}
		if (!(matchMethod(loader, method.getName()) && matchClass(loader, method.getDeclaringClass().getName())))
			return false;

		// check return type
		try
		{
			if (!matchClass(loader, method.getReturnType().getName(), methodInfo.getReturnType()))
				return false;
		}
		catch (NotFoundException e1)
		{
			LOG.log(Level.SEVERE, "Return type not found for : " + method.getName(), e1);
			return false;
		}

		// check params
		if (methodInfo.getParams() != null && methodInfo.getParams().length > 0)
		{
			try
			{
				CtClass[] targetParamTypes = method.getParameterTypes();
				if (methodInfo.getParams().length != targetParamTypes.length)
				{
					return false;
				}
				for (int i = 0; i < targetParamTypes.length; i++)
				{
					String methodInfoParamExpression = methodInfo.getParams()[i];

					if (!matchClass(loader, targetParamTypes[i].getName(), methodInfoParamExpression))
					{
						return false;
					}
				}
			}
			catch (Exception e)
			{
				LOG.log(Level.SEVERE,
						"CtMethod getParameter Exception : " + method.getLongName() + " " + e.getMessage(), e);
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public void parseMethodLine(String line)
	{
		if (line == null || line.startsWith("#") || line.isEmpty())
			return;
		line = line.trim();
		String[] parts = line.split(" ");
		if (parts == null || parts.length != 2)
		{
			LOG.log(Level.WARNING, "Invalid line:" + line);
		}
		else
		{
			try
			{
				LOG.info("Parse: " + line);
				String classMethodName = parts[1].split("\\(")[0];
				String className = classMethodName.substring(0, classMethodName.lastIndexOf("."));
				String methodName = classMethodName.substring(classMethodName.lastIndexOf(".") + 1);
				String[] params = null;

				if (parts[1].contains("(") && ((parts[1].indexOf(")") - parts[1].indexOf("(") > 1)))
				{
					params = parts[1].substring(parts[1].indexOf("(") + 1, parts[1].indexOf(")")).split(",");
				}
				methodInfo = new MethodInformation(methodName);

				methodInfo.setClassName(className);
				methodInfo.setParams(params);
				methodInfo.setReturnType(parts[0]);
			}
			catch (Exception e)
			{
				LOG.log(Level.WARNING, "Failed to parse line : " + line, e);
			}
		}
	}

}
