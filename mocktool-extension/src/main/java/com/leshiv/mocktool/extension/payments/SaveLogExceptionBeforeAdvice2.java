package com.leshiv.mocktool.extension.payments;

import java.lang.reflect.Method;

import com.leshiv.mocktool.aop.api.JoinPoint;
import com.leshiv.mocktool.extension.common.advice.GenericBeforeAdvice;

public class SaveLogExceptionBeforeAdvice2 extends GenericBeforeAdvice
{
	@Override
	public void before(JoinPoint joinpoint)
	{
		System.out.println("BEFORE");
		Object[] args = joinpoint.getArgs();
		if (args == null || args.length < 2)
		{
			if (args != null)
				System.out.println("Args length : " + args.length);
			return;
		}
		// private void saveLog(Order order, String msg)
		Object orderObj = args[0];
		String msgName = (String) args[1];
		System.out.println(msgName);
		// Object msgObj = args[2];
		String storeName = null;
		try
		{
			Class<?> orderClass = Class.forName("com.example.bank.Order");
			Method storeNameGetter = orderClass.getDeclaredMethod("getStoreName");
			storeName = (String) storeNameGetter.invoke(orderObj);
		}
		catch (Exception e)
		{
			System.out.println("========== Mock Failed =============");
			e.printStackTrace();
			return;
		}

		if (storeName == null || storeName.isEmpty())
		{
			System.out.println("========== StoreName is empty =============");
			return;
		}

		System.out.println("========== StoreName : " + storeName + " =============");

		if (storeName.contains("mock"))
		{
			throw new RuntimeException("Mockagent Runtime Exception for Unit Test, StoreName: " + storeName);
		}

	}
}
