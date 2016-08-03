package com.leshiv.mocktool.extension.common.advice;

import com.leshiv.mocktool.aop.api.SourceReplaceAdvice;
import com.leshiv.mocktool.util.TypeHelper;

import javassist.CtMethod;

public abstract class BodyReplaceAdvice extends GenericAdvice implements SourceReplaceAdvice
{
	public boolean isProceed(CtMethod ctMethod)
	{
		return TypeHelper.booleanValueOf(getParam("isProceed"), false);
	}

	public abstract String source(CtMethod ctMethod) throws Exception;

}
