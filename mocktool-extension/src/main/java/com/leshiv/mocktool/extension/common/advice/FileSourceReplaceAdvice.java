package com.leshiv.mocktool.extension.common.advice;

import java.io.File;
import java.util.Scanner;

import com.leshiv.mocktool.aop.api.SourceReplaceAdvice;

import javassist.CtMethod;

public class FileSourceReplaceAdvice extends GenericAdvice implements SourceReplaceAdvice
{
	public String source(CtMethod ctMethod) throws Exception
	{
		@SuppressWarnings("resource")
		String content = new Scanner(new File(getParam("file"))).useDelimiter("\\Z").next();
		return content;
	}
}
