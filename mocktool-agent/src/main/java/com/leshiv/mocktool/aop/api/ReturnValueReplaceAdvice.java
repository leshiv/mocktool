package com.leshiv.mocktool.aop.api;

public interface ReturnValueReplaceAdvice extends Advice
{

	/**
	 * @param joinpoint
	 * @return proceed with original method body/call or not 
	 */
	public boolean isProceed(final JoinPoint joinpoint);

	/**
	 * @param joinpoint
	 * @return replacing return value
	 */
	public Object returnValue(final JoinPoint joinpoint);
}
