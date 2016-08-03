package com.leshiv.mocktool.extension.example.advice;

import com.leshiv.mocktool.aop.api.JoinPoint;
import com.leshiv.mocktool.aop.api.ReturnValueReplaceAdvice;
import com.leshiv.mocktool.extension.common.advice.GenericAdvice;

public class ExampleReturnValueReplaceAdvice extends GenericAdvice implements ReturnValueReplaceAdvice
{

	public boolean isProceed(JoinPoint joinpoint)
	{
		return true;
	}

	public Object returnValue(JoinPoint joinpoint)
	{
		System.out.println(" ### original return value if proceed : " + joinpoint.getReturnValue());
		Object newResult = null;
		try
		{
			newResult = Class.forName(joinpoint.getMethodSignature().getReturnType()).newInstance();
		}
		catch (Exception e)
		{
			System.out.println(" ### failed to instantiate new return value of class : "
					+ joinpoint.getMethodSignature().getReturnType());
			e.printStackTrace();
		}

		return newResult;
	}
}
