package com.leshiv.mocktool.extension.example.advice;

import com.leshiv.mocktool.extension.common.advice.BodyReplaceAdvice;

import javassist.CtMethod;

public class HelloWorldBodyReplaceAdvice extends BodyReplaceAdvice
{
	public String source(CtMethod ctMethod)
	{
		String bodyStr = "{System.out.println(\"Hello " + ctMethod.getName()
				+ " ! - from HelloWorldBodyReplaceAdvice\");}";
		return bodyStr;
	}
}
