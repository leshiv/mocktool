package com.leshiv.mocktool.extension.example.advice;

import com.leshiv.mocktool.aop.api.AroundAdvice;
import com.leshiv.mocktool.aop.api.JoinPoint;
import com.leshiv.mocktool.extension.common.advice.GenericAdvice;

import javassist.CtMethod;

public class ExampleAroundAdvice extends GenericAdvice implements AroundAdvice
{

	public void before(JoinPoint joinpoint)
	{
		System.out.println(" ### Before : " + joinpoint.getMethodSignature().getLongName());
	}

	public void after(JoinPoint joinpoint)
	{
		System.out.println(" ### After : " + joinpoint.getMethodSignature().getLongName());
	}

	public boolean isProceed(JoinPoint joinpoint)
	{
		return true;
	}

	public void asFinally(CtMethod ctMethod)
	{
	}

	public boolean asFinally()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
