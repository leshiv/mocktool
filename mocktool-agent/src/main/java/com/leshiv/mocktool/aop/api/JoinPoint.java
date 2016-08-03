package com.leshiv.mocktool.aop.api;

import java.util.Map;

public interface JoinPoint
{
	void setThat(Object that);

	Object getThat();

	void setReturnValue(Object returnValue);

	Object getReturnValue();

	//TODO:
	Object getTarget();

	///TODO:
	void setTarget(Object target);

	Object[] getArgs();

	void setArgs(Object[] args);

	//TODO:
	SourceLocation getSourceLocation();

	//TODO:
	void setSourceLocation(SourceLocation sourceLocation);

	//TODO:
	Map<String, Object> getLocalVarMap();

	//TODO:
	void setLocalVarMap(Map<String, Object> localVarMap);

	Throwable getException();

	void setException(Throwable exception);

	MethodSignature getMethodSignature();

	void setMethodSignature(MethodSignature methodSignature);

}
