package com.leshiv.mocktool.extension.example.advice;

import com.leshiv.mocktool.extension.common.advice.BodyReplaceAdvice;

import javassist.CtMethod;
import javassist.NotFoundException;

// source replace version
public class TimerAdvice2 extends BodyReplaceAdvice
{
	public String source(CtMethod ctMethod) throws NotFoundException
	{
		StringBuilder srcBuilder = new StringBuilder();

		srcBuilder.append("{ long start = System.currentTimeMillis();");
		boolean hasReturnValue = false;
		if (!"void".equalsIgnoreCase(ctMethod.getReturnType().getName()))
		{
			srcBuilder.append(ctMethod.getReturnType().getName() + " result = " + PROCEED_STRING + ";");
			hasReturnValue = true;
		}
		else
		{
			srcBuilder.append(PROCEED_STRING + ";");
		}
		srcBuilder
				.append("System.out.println(\" ### Execution time : \" + (System.currentTimeMillis() - start) + \"ms.\");");
		if (hasReturnValue)
		{
			srcBuilder.append("return result;");
		}
		srcBuilder.append("}");

		return srcBuilder.toString();
	}
}
