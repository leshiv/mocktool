package com.leshiv.mocktool.extension.common.advice;

import com.leshiv.mocktool.aop.api.BeforeAdvice;
import com.leshiv.mocktool.aop.api.JoinPoint;

public abstract class GenericBeforeAdvice extends GenericAdvice implements BeforeAdvice
{
	public abstract void before(JoinPoint joinpoint);
}
