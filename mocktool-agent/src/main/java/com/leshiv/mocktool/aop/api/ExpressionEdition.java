package com.leshiv.mocktool.aop.api;

public interface ExpressionEdition
{
	public ExpressionType getExpressionType();

	public String getClassName();

	public String getMethodName();

	public String getReplaceStatement();
}
