package com.leshiv.mocktool.aop.api;

/**
 * deserted.
 *
 */
public interface ReplaceAdvice extends Advice
{
	/**
	 * @param joinpoint
	 * @return proceed with original method body/call or not 
	 */
	public boolean isProceed(final JoinPoint joinpoint);
}
