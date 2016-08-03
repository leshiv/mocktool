package com.leshiv.mocktool.extension.example.advice;

import com.leshiv.mocktool.aop.api.SourceReplaceAdvice;
import com.leshiv.mocktool.extension.common.advice.GenericAdvice;

import javassist.CtMethod;

public class ExampleSourceReplaceAdvice extends GenericAdvice implements SourceReplaceAdvice
{

	public String source(CtMethod ctMethod) throws Exception
	{
		return ";";
	}

}
