package com.leshiv.mocktool.extension.example.advice;

import com.leshiv.mocktool.aop.api.BeforeAdvice;
import com.leshiv.mocktool.aop.api.JoinPoint;
import com.leshiv.mocktool.extension.common.advice.GenericAdvice;

public class ExampleBeforeAdvice extends GenericAdvice implements BeforeAdvice
{

	public void before(JoinPoint joinpoint)
	{
		System.out.println(" ### Before : " + joinpoint.getMethodSignature().getLongName());
		Object[] args = joinpoint.getArgs();
		for (int i = 0; i < args.length; i++)
		{
			System.out.println(" #### arg" + (i + 1) + " : " + args[i]);
		}
		System.out.println(" ### returnvalue : " + joinpoint.getReturnValue());
	}

}
