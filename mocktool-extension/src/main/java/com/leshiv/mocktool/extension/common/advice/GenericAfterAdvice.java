package com.leshiv.mocktool.extension.common.advice;

import com.leshiv.mocktool.aop.api.AfterAdvice;
import com.leshiv.mocktool.aop.api.JoinPoint;
import com.leshiv.mocktool.util.TypeHelper;

public abstract class GenericAfterAdvice extends GenericAdvice implements AfterAdvice
{

	public abstract void after(JoinPoint joinpoint);

	public boolean asFinally()
	{
		return TypeHelper.booleanValueOf(getParam("asFinally"), false);
	}

}
