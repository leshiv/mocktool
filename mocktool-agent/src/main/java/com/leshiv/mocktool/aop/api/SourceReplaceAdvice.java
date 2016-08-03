package com.leshiv.mocktool.aop.api;

import javassist.CtMethod;

public interface SourceReplaceAdvice extends Advice
{
	// original method proceed string place holder, will be replaced by actual method call in AopCodeModifier
	public static final String PROCEED_STRING = "__pr0ceed__";

	/**
	 * @return source code to replace method body/call
	 */
	public String source(CtMethod ctMethod) throws Exception;

}
