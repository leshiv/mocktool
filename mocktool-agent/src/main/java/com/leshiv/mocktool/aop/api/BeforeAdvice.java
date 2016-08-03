package com.leshiv.mocktool.aop.api;

public interface BeforeAdvice extends Advice
{
	public void before(final JoinPoint joinpoint);
}
