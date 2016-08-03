package com.leshiv.mocktool.aop.implement;

import com.leshiv.mocktool.aop.api.ExpressionEdition;
import com.leshiv.mocktool.aop.api.ExpressionType;

public class ExpressionEditionImpl implements ExpressionEdition
{

	ExpressionType expressionType;
	String className;
	String methodName;
	String replaceStatement;

	public ExpressionEditionImpl()
	{

	}

	public ExpressionEditionImpl(ExpressionType expressionType, String className, String methodName,
			String replaceStatement)
	{
		this.expressionType = expressionType;
		this.className = className;
		this.methodName = methodName;
		this.replaceStatement = replaceStatement;
	}

	public ExpressionEdition clone()
	{
		return new ExpressionEditionImpl(expressionType, className, methodName, replaceStatement);
	}

	public void setExpressionType(ExpressionType expressionType)
	{
		this.expressionType = expressionType;
	}

	public void setClassName(String className)
	{
		this.className = className;
	}

	public void setMethodName(String methodName)
	{
		this.methodName = methodName;
	}

	public void setReplaceStatement(String replaceStatement)
	{
		this.replaceStatement = replaceStatement;
	}

	@Override
	public ExpressionType getExpressionType()
	{
		return expressionType;
	}

	@Override
	public String getClassName()
	{
		return className;
	}

	@Override
	public String getMethodName()
	{
		return methodName;
	}

	@Override
	public String getReplaceStatement()
	{
		return replaceStatement;
	}

	public void clear()
	{
		this.expressionType = null;
		this.className = null;
		this.methodName = null;
		this.replaceStatement = null;
	}

}
