package com.leshiv.mocktool.extension.common.advice;

import com.leshiv.mocktool.aop.api.AddCatchAdvice;
import com.leshiv.mocktool.aop.api.JoinPoint;
import com.leshiv.mocktool.util.TypeHelper;

public abstract class GenericAddCatchAdvice extends GenericAdvice implements AddCatchAdvice
{

	public String getExceptionType()
	{
		String exceptionType = getParam("exceptionType");
		if (exceptionType == null || exceptionType.isEmpty())
		{
			return Throwable.class.getName();
		}
		return exceptionType;
	}

	public boolean suppress()
	{
		return TypeHelper.booleanValueOf(getParam("suppress"), false);
	}

	public abstract void addCatch(JoinPoint joinpoint) throws Exception;

}
