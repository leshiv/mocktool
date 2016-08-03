package com.leshiv.mocktool.extension.example.advice;

import com.leshiv.mocktool.aop.api.JoinPoint;
import com.leshiv.mocktool.extension.common.advice.GenericAfterAdvice;

public class RuntimeExceptionAfterAdvice extends GenericAfterAdvice
{

	@Override
	public void after(JoinPoint joinpoint)
	{
		throw new RuntimeException("mockagent runtime exception");
	}

}
