package com.leshiv.mocktool.extension.example.advice;

import com.leshiv.mocktool.aop.api.JoinPoint;
import com.leshiv.mocktool.extension.common.advice.GenericAfterAdvice;

public class ExampleAfterAdvice extends GenericAfterAdvice
{
	public void after(JoinPoint joinpoint)
	{
		System.out.println(" ### After : " + joinpoint.getMethodSignature().getLongName());
		Object[] args = joinpoint.getArgs();
		for (int i = 0; i < args.length; i++)
		{
			System.out.println(" #### arg" + (i + 1) + " : " + args[i]);
		}
		System.out.println("#### RETURN　: " + joinpoint.getReturnValue());
	}

}
