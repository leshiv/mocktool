package com.leshiv.mocktool.aop.implement;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.leshiv.mocktool.aop.api.Advice;
import com.leshiv.mocktool.aop.api.AroundAdvice;
import com.leshiv.mocktool.aop.api.Aspect;
import com.leshiv.mocktool.aop.api.ExpressionEdition;
import com.leshiv.mocktool.aop.api.PointCut;
import com.leshiv.mocktool.util.MockToolLogFactory;

public class AspectImpl implements Aspect
{
	private static final Logger LOG = MockToolLogFactory.getDefaultLogger();

	protected PointCut pointCut;
	protected String adviceClassName;
	protected Map<String, String> params;
	protected String id;
	protected List<ExpressionEdition> expressionEditions;

	public AspectImpl(String pointcut, String adviceClassName, Map<String, String> params,
			List<ExpressionEdition> expressionEditions)
	{
		this.pointCut = new PointCutImpl(pointcut);
		this.adviceClassName = adviceClassName;
		this.params = params;
		this.id = pointcut + "#" + adviceClassName;
		this.expressionEditions = expressionEditions;
	}

	@Override
	public PointCut getPointCut()
	{
		return pointCut;
	}

	@Override
	public Advice getAdvice(ClassLoader loader)
	{
		return AdviceRegistry.getAdvice(loader, adviceClassName);
	}

	//TODO:
	@Override
	public boolean validate(ClassLoader loader)
	{
		// AroundAdvice should not return a sington
		Advice advice;
		try
		{
			advice = getAdvice(loader);
			if (advice instanceof AroundAdvice)
			{
				return advice != getAdvice(loader);
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, "Get advice failed", e);
		}
		return false;
	}

	public Map<String, String> getParams()
	{
		return params;
	}

	public String getAdviceClassName()
	{
		return adviceClassName;
	}

	public void setAdviceClassName(String adviceClassName)
	{
		this.adviceClassName = adviceClassName;
	}

	public void setPointCut(PointCut pointCut)
	{
		this.pointCut = pointCut;
	}

	public void setParams(Map<String, String> params)
	{
		this.params = params;
	}

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public List<ExpressionEdition> getExpressionEditions()
	{
		return expressionEditions;
	}

}
