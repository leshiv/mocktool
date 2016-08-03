package com.leshiv.mocktool.aop.api;

public interface AfterAdvice extends Advice
{
	public void after(final JoinPoint joinpoint);

	public boolean asFinally();
}
