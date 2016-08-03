package com.leshiv.mocktool.aop.api;

import javassist.Modifier;

public class MethodSignature
{
	protected String longName;
	protected int modifiers;
	protected String methodName;
	protected String returnType;
	protected String declaringClass;
	protected String paramTypes;
	protected String exceptionTypes;

	//TODO: annotations,isConstructor

	public String getLongName()
	{
		return longName;
	}

	public MethodSignature(String longName, int modifiers, String methodName, String returnType, String declaringClass,
			String paramTypes, String exceptionTypes)
	{
		super();
		this.longName = longName;
		this.modifiers = modifiers;
		this.methodName = methodName;
		this.returnType = returnType;
		this.declaringClass = declaringClass;
		this.paramTypes = paramTypes;
		this.exceptionTypes = exceptionTypes;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getModifierString(modifiers));
		sb.append(returnType);
		sb.append(" ");
		sb.append(longName);

		if (exceptionTypes != null && !exceptionTypes.isEmpty())
		{
			sb.append(" throws " + exceptionTypes);
		}
		return sb.toString();
	}

	public static String getModifierString(int mod)
	{
		StringBuilder msb = new StringBuilder();
		if (Modifier.isPublic(mod))
			msb.append("public ");
		if (Modifier.isPrivate(mod))
			msb.append("private ");
		if (Modifier.isProtected(mod))
			msb.append("protected ");
		if (Modifier.isSynchronized(mod))
			msb.append("synchronized");
		if (Modifier.isAbstract(mod))
			msb.append("abstract ");
		if (Modifier.isStatic(mod))
			msb.append("static ");
		if (Modifier.isNative(mod))
			msb.append("native ");
		if (Modifier.isStrict(mod))
			msb.append("strictfp ");
		if (Modifier.isFinal(mod))
			msb.append("final");

		return msb.toString();
	}

	public void setLongName(String longName)
	{
		this.longName = longName;
	}

	public int getModifiers()
	{
		return modifiers;
	}

	public void setModifiers(int modifiers)
	{
		this.modifiers = modifiers;
	}

	public String getReturnType()
	{
		return returnType;
	}

	public void setReturnType(String returnType)
	{
		this.returnType = returnType;
	}

	public String getDeclaringClass()
	{
		return declaringClass;
	}

	public void setDeclaringClass(String declaringClass)
	{
		this.declaringClass = declaringClass;
	}

	public String getMethodName()
	{
		return methodName;
	}

	public void setMethodName(String methodName)
	{
		this.methodName = methodName;
	}

	public String getParamTypes()
	{
		return paramTypes;
	}

	public void setParamTypes(String paramTypes)
	{
		this.paramTypes = paramTypes;
	}

	public String getExceptionTypes()
	{
		return exceptionTypes;
	}

	public void setExceptionTypes(String exceptionTypes)
	{
		this.exceptionTypes = exceptionTypes;
	}

}
