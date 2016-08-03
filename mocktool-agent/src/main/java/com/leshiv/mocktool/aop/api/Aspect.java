package com.leshiv.mocktool.aop.api;

import java.util.List;
import java.util.Map;

public interface Aspect
{
	public PointCut getPointCut();

	public Advice getAdvice(ClassLoader loader) throws Exception;

	public boolean validate(ClassLoader loader);

	public String getId();

	public Map<String, String> getParams();

	public List<ExpressionEdition> getExpressionEditions();
}
