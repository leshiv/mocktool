package com.leshiv.mocktool.aop.api;

import com.leshiv.mocktool.aop.implement.AdviceRegistry;
import com.leshiv.mocktool.aop.implement.AspectRegistry;

/**
 * Weaving Names
 */
public class WN
{
	//class names
	public static final String JOINPOINT_CLASS = JoinPoint.class.getName();
	public static final String BEFORE_ADVICE_CLASS = BeforeAdvice.class.getName();
	public static final String AFTER_ADVICE_CLASS = AfterAdvice.class.getName();
	public static final String AROUND_ADVICE_CLASS = AroundAdvice.class.getName();
	public static final String ADD_CATCH_ADVICE_CLASS = AddCatchAdvice.class.getName();
	//	public static final String REPLACE_ADVICE_CLASS = ReplaceAdvice.class.getName();
	public static final String RETURN_VALUE_REPLACE_ADVICE_CLASS = ReturnValueReplaceAdvice.class.getName();
	public static final String SOURCE_REPLACE_ADVICE_CLASS = SourceReplaceAdvice.class.getName();

	public static final String ADVICE_REGISTRY_CLASS = AdviceRegistry.class.getName();
	public static final String ASPECT_REGISTRY_CLASS = AspectRegistry.class.getName();

	//variable names
	public static final String JOINPOINT_VARIABLE = "joinpoint";
	public static final String ADVICE_PARAMS_VARIABLE = "adviceParams";
	public static final String BEFORE_ADVICE_VARIABLE = "beforeAdvice";
	public static final String AFTER_ADVICE_VARIABLE = "afterAdvice";
	public static final String AROUND_ADVICE_VARIABLE = "aroundAdvice";
	//	public static final String REPLACE_ADVICE_VARIABLE = "replaceAdvice";
	public static final String RETURN_VALUE_REPLACE_ADVICE_VARIABLE = "returnValueReplaceAdvice";
	public static final String SOURCE_REPLACE_ADVICE_VARIABLE = "sourceReplaceAdvice";
	public static final String ADD_CATCH_ADVICE_VARIABLE = "addCatchAdvice";
}
