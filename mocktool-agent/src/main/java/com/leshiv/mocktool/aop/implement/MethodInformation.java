package com.leshiv.mocktool.aop.implement;

public class MethodInformation
{
	private String className;
	private String methodName;
	private String[] params;
	private String returnType;
	private String newReturnValue;

	public String getClassName()
	{
		return className;
	}

	public void setClassName(String className)
	{
		this.className = className;
	}

	public String getReturnType()
	{
		return returnType;
	}

	public void setReturnType(String returnType)
	{
		this.returnType = returnType;
	}

	public String getNewReturnValue()
	{
		return newReturnValue;
	}

	public void setNewReturnValue(String newReturnValue)
	{
		this.newReturnValue = newReturnValue;
	}

	public MethodInformation(String methodName)
	{
		this.methodName = methodName;
		this.params = null;
	}

	public String getMethodName()
	{
		return methodName;
	}

	public void setMethodName(String methodName)
	{
		this.methodName = methodName;
	}

	public String[] getParams()
	{
		return params;
	}

	public void setParams(String[] params)
	{
		this.params = params;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(returnType + " " + methodName + "(");
		if (params != null)
		{
			for (int i = 0; i < params.length; i++)
			{
				sb.append(params[i]);
				if (i != params.length - 1)
					sb.append(",");
			}
		}
		sb.append(") -" + newReturnValue);
		return sb.toString();
	}

}
