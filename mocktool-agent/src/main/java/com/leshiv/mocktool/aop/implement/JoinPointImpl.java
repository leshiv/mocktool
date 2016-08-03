package com.leshiv.mocktool.aop.implement;

import java.util.HashMap;
import java.util.Map;

import com.leshiv.mocktool.aop.api.JoinPoint;
import com.leshiv.mocktool.aop.api.MethodSignature;
import com.leshiv.mocktool.aop.api.SourceLocation;

public class JoinPointImpl implements JoinPoint
{
	Object that;
	Object returnValue;
	Object target;
	Object[] args;
	SourceLocation sourceLocation;
	Map<String, Object> localVarMap = new HashMap<String, Object>();
	Throwable exception;
	MethodSignature methodSignature;

	public JoinPointImpl()
	{
	}

	public void clean()
	{
		that = null;
		target = null;
		args = null;
		sourceLocation = null;
		localVarMap.clear();
		exception = null;
	}

	public void addLocalVar(int index, int jpIndex, Object obj)
	{
		//        if (index > jpIndex) {
		//            String varName;
		//            if (interceptor.localVarNameList.size() <= index
		//                || (varName = interceptor.localVarNameList.get(index)) == null) {
		//                varName = obj == null ? "null" : obj.getClass().getSimpleName() + index;
		//            }
		//            localVarMap.put(varName, obj);
		//        }
	}

	public Object getThat()
	{
		return that;
	}

	public void setThat(Object that)
	{
		this.that = that;
	}

	public Object getTarget()
	{
		return target;
	}

	public void setTarget(Object target)
	{
		this.target = target;
	}

	public Object[] getArgs()
	{
		return args;
	}

	public void setArgs(Object[] args)
	{
		this.args = args;
	}

	public SourceLocation getSourceLocation()
	{
		return sourceLocation;
	}

	public void setSourceLocation(SourceLocation sourceLocation)
	{
		this.sourceLocation = sourceLocation;
	}

	public Map<String, Object> getLocalVarMap()
	{
		return localVarMap;
	}

	public void setLocalVarMap(Map<String, Object> localVarMap)
	{
		this.localVarMap = localVarMap;
	}

	public Throwable getException()
	{
		return exception;
	}

	public void setException(Throwable exception)
	{
		this.exception = exception;
	}

	public MethodSignature getMethodSignature()
	{
		return methodSignature;
	}

	public void setMethodSignature(MethodSignature methodSignature)
	{
		this.methodSignature = methodSignature;
	}

	public void setReturnValue(Object returnValue)
	{
		this.returnValue = returnValue;
	}

	public Object getReturnValue()
	{
		return returnValue;
	}

}
