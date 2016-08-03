package com.leshiv.mocktool.aop.api;

import java.util.List;


public interface ExpressionAdvice extends Advice
{
	public void setExpressionEditions(List<ExpressionEdition> expressionEditions);

	public List<ExpressionEdition> getExpressionEditions();
}
