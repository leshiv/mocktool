package com.leshiv.mocktool.aop.api;

import javassist.CtMethod;

public interface Matcher
{
	public boolean matchClass(ClassLoader loader, String className);

	public boolean matchMethod(ClassLoader loader, String methodName);

	public boolean matchCtMethod(ClassLoader loader, CtMethod method);
}
