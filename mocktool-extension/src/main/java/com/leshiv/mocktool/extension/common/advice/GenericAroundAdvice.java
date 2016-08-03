package com.leshiv.mocktool.extension.common.advice;

import com.leshiv.mocktool.aop.api.AroundAdvice;
import com.leshiv.mocktool.aop.api.JoinPoint;
import com.leshiv.mocktool.util.TypeHelper;

public abstract class GenericAroundAdvice extends GenericAdvice implements AroundAdvice
{
	public abstract void before(JoinPoint joinpoint);

	public abstract void after(JoinPoint joinpoint);

	public boolean isProceed(JoinPoint joinpoint)
	{
		return TypeHelper.booleanValueOf(getParam("isProceed"), false);
	}

	public boolean asFinally()
	{
		return TypeHelper.booleanValueOf(getParam("asFinally"), false);
	}

}
