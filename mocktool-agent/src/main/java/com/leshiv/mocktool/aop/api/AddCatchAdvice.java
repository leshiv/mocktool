package com.leshiv.mocktool.aop.api;

public interface AddCatchAdvice extends Advice
{
	public String getExceptionType();

	/**
	 * try to suppress any exception, only if target method returns void
	 */
	public boolean suppress();

	/**
	 * called inside the new wrapping catch clause 
	 * @param joinPoint
	 * @throws Exception
	 */
	public void addCatch(JoinPoint joinpoint) throws Exception;
}
