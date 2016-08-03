package com.leshiv.mocktool.extension.common.advice;

import java.util.HashMap;
import java.util.Map;

import com.leshiv.mocktool.aop.api.Advice;

public abstract class GenericAdvice implements Advice
{
	ThreadLocal<Map<String, String>> paramMapHolder = new ThreadLocal<Map<String, String>>();

	public void setParams(Map<String, String> params)
	{
		paramMapHolder.set(params);
	}

	public Map<String, String> getParams()
	{
		return paramMapHolder.get();
	}

	public String getParam(String key)
	{
		if (paramMapHolder.get() != null)
		{
			return paramMapHolder.get().get(key);
		}
		return null;
	}

	public void putParam(String key, String value)
	{
		if (paramMapHolder.get() == null)
		{
			paramMapHolder.set(new HashMap<String, String>());
		}

		paramMapHolder.get().put(key, value);
	}

}
