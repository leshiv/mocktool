package com.leshiv.mocktool.extension.common.advice;

import java.util.List;

import com.leshiv.mocktool.aop.api.ExpressionAdvice;
import com.leshiv.mocktool.aop.api.ExpressionEdition;

public abstract class GenericExpressionAdvice extends GenericAdvice implements ExpressionAdvice
{
	List<ExpressionEdition> expressionEditions;

	public void setExpressionEditions(List<ExpressionEdition> expressionEditions)
	{
		this.expressionEditions = expressionEditions;
	}

	public List<ExpressionEdition> getExpressionEditions()
	{
		return expressionEditions;
	}
}
