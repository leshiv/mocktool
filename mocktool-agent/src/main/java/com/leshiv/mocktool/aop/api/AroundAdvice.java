package com.leshiv.mocktool.aop.api;

public interface AroundAdvice extends BeforeAdvice, AfterAdvice
{
	public void before(final JoinPoint joinpoint);

	public void after(final JoinPoint joinpoint);
	
	public boolean isProceed(final JoinPoint joinpoint);
}
