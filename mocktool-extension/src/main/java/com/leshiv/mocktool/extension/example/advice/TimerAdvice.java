package com.leshiv.mocktool.extension.example.advice;

import com.leshiv.mocktool.aop.api.JoinPoint;
import com.leshiv.mocktool.extension.common.advice.GenericAroundAdvice;

public class TimerAdvice extends GenericAroundAdvice
{
	long start;
	@Override
	public void before(JoinPoint joinPoint)
	{
		start = System.currentTimeMillis();
		System.out.println(" ### Start time : " + start);
	}
	@Override
	public void after(JoinPoint joinPoint)
	{
		System.out.println(" ### Execution time : " + (System.currentTimeMillis() - start) + "ms.");
	}
	@Override
	public boolean isProceed(JoinPoint joinpoint)
	{
		return true;
	}

}
