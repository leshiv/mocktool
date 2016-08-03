package com.leshiv.mocktool.aop.implement;

import com.leshiv.mocktool.aop.api.Matcher;
import com.leshiv.mocktool.aop.api.PointCut;
import com.leshiv.mocktool.aop.matcher.SimpleMatcher;

public class PointCutImpl implements PointCut
{
	String expression;
	Matcher matcher;

	public PointCutImpl(String expression)
	{
		this.expression = expression;
		this.matcher = new SimpleMatcher(expression);
	}

	public String getExpression()
	{
		return expression;
	}

	public void setExpression(String expression)
	{
		this.expression = expression;
	}

	public Matcher getMatcher()
	{
		return matcher;
	}

}
