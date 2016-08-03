package com.leshiv.mocktool.extension.common.advice;

import com.leshiv.mocktool.aop.api.JoinPoint;
import com.leshiv.mocktool.aop.api.ReturnValueReplaceAdvice;
import com.leshiv.mocktool.util.TypeHelper;

public class FixedReturnValueAdvice extends GenericAdvice implements ReturnValueReplaceAdvice
{
	public Object returnValue(JoinPoint joinpoint)
	{
		String typeStr = getParam("type");
		String value = getParam("value");
		if (typeStr != null && !typeStr.isEmpty())
		{
			if (typeStr.equalsIgnoreCase("string"))
			{
				return value;
			}
			else if (typeStr.equalsIgnoreCase("bool") || typeStr.equalsIgnoreCase("boolean"))
			{
				return TypeHelper.booleanValueOf(value, false);
			}
			else if (typeStr.equalsIgnoreCase("float"))
			{
				return TypeHelper.floatValueOf(value, 0.0f);
			}
			else if (typeStr.equalsIgnoreCase("int") || typeStr.equalsIgnoreCase("integer"))
			{
				return TypeHelper.intValueOf(value, 0);
			}
		}
		return TypeHelper.stringToObject(value);
	}

	public boolean isProceed(JoinPoint joinpoint)
	{
		return TypeHelper.booleanValueOf(getParam("isProceed"), false);
	}

}
