package com.leshiv.mocktool.aop.api;

public interface PointCut
{
	void setExpression(String expression);

	public String getExpression();

	Matcher getMatcher();
}
