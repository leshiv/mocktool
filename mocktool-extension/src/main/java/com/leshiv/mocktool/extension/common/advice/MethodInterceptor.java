package com.leshiv.mocktool.extension.common.advice;

import com.leshiv.mocktool.aop.api.AddCatchAdvice;
import com.leshiv.mocktool.aop.api.AroundAdvice;
import com.leshiv.mocktool.aop.api.JoinPoint;
import com.leshiv.mocktool.util.TypeHelper;

public abstract class MethodInterceptor extends GenericAdvice implements AroundAdvice, AddCatchAdvice
{

	public abstract void addCatch(JoinPoint joinpoint) throws Exception;

	public abstract void before(JoinPoint joinpoint);

	public abstract void after(JoinPoint joinpoint);

	public boolean asFinally()
	{
		return TypeHelper.booleanValueOf(getParam("asFinally"), false);
	}

	public String getExceptionType()
	{
		return getParam("exceptionType");
	}

	public boolean suppress()
	{
		return TypeHelper.booleanValueOf(getParam("suppress"), false);
	}

	public boolean isProceed(JoinPoint joinpoint)
	{
		return TypeHelper.booleanValueOf(getParam("isProceed"), true);
	}

}