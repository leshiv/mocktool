package com.leshiv.mocktool.aop.api;

import java.util.Map;

public interface Advice
{
	public void setParams(Map<String, String> params);

	public Map<String, String> getParams();
}
