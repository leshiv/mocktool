package com.leshiv.mocktool.extension.example.advice;

import com.leshiv.mocktool.aop.api.JoinPoint;
import com.leshiv.mocktool.extension.common.advice.GenericBeforeAdvice;

public class RuntimeExceptionBeforeAdvice extends GenericBeforeAdvice
{

	@Override
	public void before(JoinPoint joinpoint)
	{
		String msg = "mocktool runtime exception";
		if (getParam("message") != null && !getParam("message").isEmpty())
		{
			msg = getParam("message");
		}
		System.out.println(msg);
		throw new RuntimeException(msg);
	}

}
