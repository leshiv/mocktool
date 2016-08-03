package com.leshiv.mocktool.aop.api;

public interface AfterThrowingAdvice extends Advice
{
	public void afterThrowing(final JoinPoint joinPoint);
}
