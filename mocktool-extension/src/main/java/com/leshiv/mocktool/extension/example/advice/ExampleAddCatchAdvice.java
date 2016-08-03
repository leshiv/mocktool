package com.leshiv.mocktool.extension.example.advice;

import com.leshiv.mocktool.aop.api.JoinPoint;
import com.leshiv.mocktool.extension.common.advice.GenericAddCatchAdvice;

public class ExampleAddCatchAdvice extends GenericAddCatchAdvice
{

	public String getExceptionType()
	{
		return Throwable.class.getName();
	}

	public boolean suppress()
	{
		System.out.println(" ### Try to suppress exception, would sucess if target method returns void");
		return true;
	}

	public void addCatch(JoinPoint joinpoint) throws Exception
	{
		System.out.println(" ### Caught exception : " + joinpoint.getException().getMessage());
	}


}
