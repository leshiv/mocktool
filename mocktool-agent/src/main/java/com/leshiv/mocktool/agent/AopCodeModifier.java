package com.leshiv.mocktool.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.leshiv.mocktool.aop.api.AddCatchAdvice;
import com.leshiv.mocktool.aop.api.AfterAdvice;
import com.leshiv.mocktool.aop.api.AroundAdvice;
import com.leshiv.mocktool.aop.api.Aspect;
import com.leshiv.mocktool.aop.api.BeforeAdvice;
import com.leshiv.mocktool.aop.api.ExpressionAdvice;
import com.leshiv.mocktool.aop.api.ExpressionEdition;
import com.leshiv.mocktool.aop.api.ReturnValueReplaceAdvice;
import com.leshiv.mocktool.aop.api.SourceReplaceAdvice;
import com.leshiv.mocktool.aop.api.WN;
import com.leshiv.mocktool.aop.implement.AdviceRegistry;
import com.leshiv.mocktool.aop.implement.AspectRegistry;
import com.leshiv.mocktool.util.ExpressionEditorUtil;
import com.leshiv.mocktool.util.MockToolLogFactory;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.expr.Expr;
import javassist.expr.ExprEditor;

/**
 * AOP Core
 */
public class AopCodeModifier
{
	private static final String LINE_SEPERATOR = System.getProperty("line.separator");
	private static final Logger LOG = MockToolLogFactory.getDefaultLogger();
	private static Map<String, CtClass> ctClasses = new HashMap<String, CtClass>();
	private static ClassPool classPool = ClassPool.getDefault();

	public static void setClassPool(ClassPool classPool)
	{
		AopCodeModifier.classPool = classPool;
	}

	public static void applyAspect(CtMethod ctMethod, Aspect aspect, ClassLoader loader) throws Exception
	{
		classPool.appendClassPath(new LoaderClassPath(loader));
		Class<?> adviceClass = aspect.getAdvice(loader).getClass();
		String adviceClassName = adviceClass.getName();
		Map<String, String> aspectParams = AspectRegistry.getParams(aspect.getId());
		// =========================== Expression Advice =============================
		if (ExpressionAdvice.class.isAssignableFrom(adviceClass))
		{
			LOG.info("ExpressionAdvice : " + adviceClassName);
			expressionEdit(ctMethod, adviceClassName);
		}

		// =========================== Replace Advice ================================
		if (ReturnValueReplaceAdvice.class.isAssignableFrom(adviceClass))
		{
			LOG.info("ReplaceAdvice : " + adviceClassName);
			bodyReturnValueReplace(ctMethod, adviceClassName, aspect.getId());
		}
		else if (SourceReplaceAdvice.class.isAssignableFrom(adviceClass))
		{
			bodySourceReplace(ctMethod, adviceClassName, aspect.getId());
		}
		// ======================= Around Before After Advice =========================
		if (AroundAdvice.class.isAssignableFrom(adviceClass))
		{
			LOG.info("AroundAdvice : " + adviceClassName);
			AroundAdvice aroundAdvice = (AroundAdvice) adviceClass.newInstance();
			aroundAdvice.setParams(aspectParams);
			bodyProxyAround(ctMethod, adviceClassName, aspect.getId(), aroundAdvice.asFinally());
		}
		else
		{
			if (BeforeAdvice.class.isAssignableFrom(adviceClass))
			{
				addLocalVariable(ctMethod, WN.JOINPOINT_CLASS, WN.JOINPOINT_VARIABLE);
				addLocalVariable(ctMethod, Map.class.getName(), WN.ADVICE_PARAMS_VARIABLE);
				LOG.info("BeforeAdvice : " + adviceClassName);
				bodyBefore(ctMethod, adviceClassName, aspect.getId());
			}
			if (AfterAdvice.class.isAssignableFrom(adviceClass))
			{
				addLocalVariable(ctMethod, WN.JOINPOINT_CLASS, WN.JOINPOINT_VARIABLE);
				addLocalVariable(ctMethod, Map.class.getName(), WN.ADVICE_PARAMS_VARIABLE);
				LOG.info("AfterAdvice : " + adviceClassName);
				AfterAdvice afterAdvice = (AfterAdvice) adviceClass.newInstance();
				afterAdvice.setParams(aspectParams);
				bodyAfter(ctMethod, adviceClassName, aspect.getId(), afterAdvice.asFinally());
			}
		}

		// =========================== Add Catch Advice ===============================
		if (AddCatchAdvice.class.isAssignableFrom(adviceClass))
		{
			AddCatchAdvice addCatchAdviceObject = (AddCatchAdvice) adviceClass.newInstance();
			String exceptionType = addCatchAdviceObject.getExceptionType();
			//			String exceptionType = (String) adviceClass.getDeclaredMethod("getExceptionType", new Class<?>[0])
			//					.invoke(addCatchAdviceObject, new Object[0]);
			LOG.info("AddCatchAdvice : " + adviceClassName + ", catch : " + exceptionType);
			bodyAddCatch(ctMethod, adviceClassName, aspect.getId(), exceptionType);
		}

	}

	public static void addLocalVariable(CtMethod ctMethod, String className, String name) throws Exception
	{
		try
		{
			ctMethod.addLocalVariable(name, getCtClass(className));
		}
		catch (CannotCompileException e1)
		{
			LOG.log(Level.SEVERE, "Cannot add local variable type: " + className + ", name: " + name, e1);
		}
	}

	private static String joinpointInitStr(CtMethod ctMethod) throws Exception
	{
		String jpStr = WN.JOINPOINT_VARIABLE + " = new com.leshiv.mocktool.aop.implement.JoinPointImpl();";
		if (!Modifier.isStatic(ctMethod.getModifiers()))
		{
			jpStr += WN.JOINPOINT_VARIABLE + ".setThat($0);";
		}
		jpStr += WN.JOINPOINT_VARIABLE + ".setArgs($args);";

		//================= method signature constructor string ==================
		String longName = ctMethod.getLongName();
		int modifiers = ctMethod.getModifiers();
		String declaringClass = ctMethod.getDeclaringClass().getName();
		String returnType = ctMethod.getReturnType().getName();
		String methodName = ctMethod.getName();
		StringBuilder ptsb = new StringBuilder();

		CtClass[] paramTypeCtClasses = ctMethod.getParameterTypes();
		if (paramTypeCtClasses != null && paramTypeCtClasses.length > 0)
		{
			for (int i = 0; i < paramTypeCtClasses.length; i++)
			{
				ptsb.append(paramTypeCtClasses[i].getName());
				if (i < paramTypeCtClasses.length - 1)
					ptsb.append(",");
			}
		}

		String paramTypes = ptsb.toString();

		StringBuilder etsb = new StringBuilder();
		CtClass[] exceptionTypeCtClasses = ctMethod.getExceptionTypes();

		if (exceptionTypeCtClasses != null && exceptionTypeCtClasses.length > 0)
		{
			for (int i = 0; i < exceptionTypeCtClasses.length; i++)
			{
				etsb.append(exceptionTypeCtClasses[i].getName());
				if (i < exceptionTypeCtClasses.length - 1)
					etsb.append(",");
			}
		}

		String exceptionTypes = etsb.toString();
		String constructorStr = String.format("\"%s\",%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"", longName, modifiers,
				methodName, returnType, declaringClass, paramTypes, exceptionTypes);

		jpStr += WN.JOINPOINT_VARIABLE + ".setMethodSignature(new com.leshiv.mocktool.aop.api.MethodSignature("
				+ constructorStr + "));";

		return jpStr;
	}

	private static String paramsInitString(CtMethod ctMethod, String aspectId)
	{
		String paramsStr = WN.ADVICE_PARAMS_VARIABLE + " = " + WN.ASPECT_REGISTRY_CLASS + ".getParams(\"" + aspectId
				+ "\");";
		return paramsStr;
	}

	private static CtClass getCtClass(String className) throws NotFoundException
	{
		if (classPool == null)
			classPool = ClassPool.getDefault();
		if (!ctClasses.containsKey(className))
		{
			ctClasses.put(className, classPool.get(className));
		}
		return ctClasses.get(className);
	}

	/**
	 * @param ctMethod
	 * @param adviceClassName
	 * @param aspectId
	 */
	public static void bodyBefore(CtMethod ctMethod, String adviceClassName, String aspectId) throws Exception
	{

		//FIXME:
		//		addLocalVariable(ctMethod, WN.BEFORE_ADVICE_CLASS, WN.BEFORE_ADVICE_VARIABLE);

		//		String joinpointStr = joinpointInitStr(ctMethod) + LINE_SEPERATOR;
		String joinpointStr = WN.JOINPOINT_CLASS + " " + joinpointInitStr(ctMethod) + LINE_SEPERATOR;
		//		String paramsInitStr = paramsInitString(ctMethod, aspectId) + LINE_SEPERATOR;
		String paramsInitStr = Map.class.getName() + " " + paramsInitString(ctMethod, aspectId) + LINE_SEPERATOR;
		String beforeSrc = WN.BEFORE_ADVICE_VARIABLE + ".before(" + WN.JOINPOINT_VARIABLE + ");";
		//		String adviceStr = WN.BEFORE_ADVICE_VARIABLE + " = (" + WN.BEFORE_ADVICE_CLASS + ")" + WN.ADVICE_REGISTRY_CLASS
		//				+ ".getAdvice(null,\"" + adviceClassName + "\");";

		String adviceStr = WN.BEFORE_ADVICE_CLASS + " " + WN.BEFORE_ADVICE_VARIABLE + " = (" + WN.BEFORE_ADVICE_CLASS
				+ ")" + WN.ADVICE_REGISTRY_CLASS + ".getAdvice(null,\"" + adviceClassName + "\");";
		//FIXME:cannot insert if-else statement?
		//		adviceStr += "try{if(" + WN.ADVICE_PARAMS_VARIABLE + "!=null){" + WN.BEFORE_ADVICE_VARIABLE + ".setParams("
		//				+ WN.ADVICE_PARAMS_VARIABLE + ");}}catch(Throwable t){}";
		adviceStr += WN.BEFORE_ADVICE_VARIABLE + ".setParams(" + WN.ADVICE_PARAMS_VARIABLE + ");" + LINE_SEPERATOR;

		try
		{
			//			ctMethod.insertBefore(beforeSrc);
			//			ctMethod.insertBefore(adviceStr);
			//			ctMethod.insertBefore(joinpointStr);
			//			ctMethod.insertBefore(paramsInitStr);
			ctMethod.insertBefore(paramsInitStr + joinpointStr + adviceStr + beforeSrc);
		}
		catch (CannotCompileException e)
		{
			LOG.log(Level.SEVERE, "Cannot compile before : " + adviceStr + beforeSrc, e);
			e.printStackTrace();
		}
	}

	/**
	 * @param ctMethod
	 * @param adviceClassName
	 * @param aspectId
	 */
	public static void bodyAfter(CtMethod ctMethod, String adviceClassName, String aspectId) throws Exception
	{
		bodyAfter(ctMethod, adviceClassName, aspectId, false);
	}

	/**
	 * @param ctMethod
	 * @param adviceClassName
	 * @param aspectId
	 * @param asFinally
	 */
	public static void bodyAfter(CtMethod ctMethod, String adviceClassName, String aspectId, boolean asFinally)
			throws Exception
	{
		addLocalVariable(ctMethod, WN.AFTER_ADVICE_CLASS, WN.AFTER_ADVICE_VARIABLE);
		String paramsInitStr = paramsInitString(ctMethod, aspectId) + LINE_SEPERATOR;
		String joinpointStr = joinpointInitStr(ctMethod) + LINE_SEPERATOR;
		String adviceStr = WN.AFTER_ADVICE_VARIABLE + " = (" + WN.AFTER_ADVICE_CLASS + ")" + WN.ADVICE_REGISTRY_CLASS
				+ ".getAdvice(null,\"" + adviceClassName + "\");";
		adviceStr += WN.AFTER_ADVICE_VARIABLE + ".setParams(" + WN.ADVICE_PARAMS_VARIABLE + ");" + LINE_SEPERATOR;
		// return value is accessible here
		String joinpointReturnValueString = WN.JOINPOINT_VARIABLE + ".setReturnValue(($w)$_);" + LINE_SEPERATOR;
		String afterSrc = joinpointReturnValueString + "{" + WN.AFTER_ADVICE_VARIABLE + ".after("
				+ WN.JOINPOINT_VARIABLE + ");}" + LINE_SEPERATOR;
		try
		{
			ctMethod.insertBefore(adviceStr);
			ctMethod.insertBefore(joinpointStr);
			ctMethod.insertBefore(paramsInitStr);
			if (asFinally)
			{
				LOG.info("After as finally : " + adviceClassName);
				String finallyParamsInitStr = Map.class.getName() + " " + paramsInitStr + LINE_SEPERATOR;
				String finallyJoinpointInitStr = WN.JOINPOINT_CLASS + " " + joinpointInitStr(ctMethod) + LINE_SEPERATOR;
				String finallyAdviceInitStr = WN.AFTER_ADVICE_CLASS + " " + adviceStr;
				finallyAdviceInitStr += WN.AFTER_ADVICE_VARIABLE + ".setParams(" + WN.ADVICE_PARAMS_VARIABLE + ");"
						+ LINE_SEPERATOR;
				afterSrc = finallyParamsInitStr + finallyJoinpointInitStr + finallyAdviceInitStr + afterSrc;
			}
			ctMethod.insertAfter(afterSrc, asFinally);
		}
		catch (CannotCompileException e)
		{
			LOG.log(Level.SEVERE, "Cannot compile after : " + adviceStr + afterSrc, e);
			e.printStackTrace();
		}
	}

	/**
	 * deserted
	 * 
	 * @param ctMethod
	 * @param adviceClassName
	 * @param aspectId
	 */
	public static void bodyAround(CtMethod ctMethod, String adviceClassName, String aspectId) throws Exception
	{
		bodyAround(ctMethod, adviceClassName, aspectId, false);
	}

	/**
	 * deserted
	 * 
	 * @param ctMethod
	 * @param adviceClassName
	 * @param aspectId
	 * @param asFinally
	 */
	public static void bodyAround(CtMethod ctMethod, String adviceClassName, String aspectId, boolean asFinally)
			throws Exception
	{
		addLocalVariable(ctMethod, WN.AROUND_ADVICE_CLASS, "aroundAdvice");
		String joinpointStr = joinpointInitStr(ctMethod);
		String paramsInitStr = paramsInitString(ctMethod, aspectId);
		String beforeSrc = "{" + WN.AROUND_ADVICE_VARIABLE + ".before(" + WN.JOINPOINT_VARIABLE + ");}";
		String adviceStr = WN.AROUND_ADVICE_VARIABLE + " = (" + WN.AROUND_ADVICE_CLASS + ")" + WN.ADVICE_REGISTRY_CLASS
				+ ".getAdvice(null,\"" + adviceClassName + "\");";
		adviceStr += WN.AROUND_ADVICE_VARIABLE + ".setParams(" + WN.ADVICE_PARAMS_VARIABLE + ");";

		try
		{
			//reverse order
			ctMethod.insertBefore(beforeSrc);
			ctMethod.insertBefore(adviceStr);
			ctMethod.insertBefore(joinpointStr);
			ctMethod.insertBefore(paramsInitStr);
		}
		catch (CannotCompileException e)
		{
			LOG.log(Level.SEVERE, "Cannot compile before(s) : " + beforeSrc + adviceStr, e);
			e.printStackTrace();
		}

		String joinpointReturnValueString = WN.JOINPOINT_VARIABLE + ".setReturnValue(($w)$_);";
		//		String afterSrc = joinpointReturnValueString + "{" + WN.AROUND_ADVICE_VARIABLE + ".after("
		//				+ WN.JOINPOINT_VARIABLE + ");}";
		String afterSrc = joinpointReturnValueString + WN.AROUND_ADVICE_VARIABLE + ".after(" + WN.JOINPOINT_VARIABLE
				+ ");";
		try
		{
			if (asFinally)
			{
				// re-declare variables
				// would raise byte code java.lang.VerifyError otherwise
				String finallySetParamStr = WN.AROUND_ADVICE_VARIABLE + ".setParams(" + WN.ADVICE_PARAMS_VARIABLE
						+ ");";
				String finallyJoinpointInitStr = WN.JOINPOINT_CLASS + " " + joinpointInitStr(ctMethod);
				String finallyAdviceInitStr = WN.AROUND_ADVICE_CLASS + " " + adviceStr;

				afterSrc = paramsInitStr + finallyJoinpointInitStr + finallyAdviceInitStr + afterSrc
						+ finallySetParamStr;
			}
			ctMethod.insertAfter(afterSrc, asFinally);
		}
		catch (CannotCompileException e)
		{
			LOG.log(Level.SEVERE, "Cannot compile after : " + afterSrc, e);
			e.printStackTrace();
		}
	}

	public static void bodyProxyAround(CtMethod ctMethod, String adviceClassName, String aspectId, boolean asFinally)
			throws Exception
	{
		String methodName = ctMethod.getName();
		String backupName = methodName + "$original";
		ctMethod.setName(backupName);
		StringBuilder proxyAroundSrcBuilder = new StringBuilder();

		CtMethod proxyMethod = null;
		try
		{
			proxyMethod = CtNewMethod.copy(ctMethod, methodName, ctMethod.getDeclaringClass(), null);
		}
		catch (CannotCompileException e2)
		{
			LOG.log(Level.SEVERE, "Cannot compile while copy method : " + ctMethod.getLongName(), e2);
			ctMethod.setName(methodName);
			return;
		}

		String joinpointStr = WN.JOINPOINT_CLASS + " " + joinpointInitStr(proxyMethod);
		String paramsInitStr = Map.class.getName() + " " + paramsInitString(proxyMethod, aspectId);
		String beforeSrc = WN.AROUND_ADVICE_VARIABLE + ".before(" + WN.JOINPOINT_VARIABLE + ");";
		String adviceStr = WN.AROUND_ADVICE_CLASS + " " + WN.AROUND_ADVICE_VARIABLE + " = (" + WN.AROUND_ADVICE_CLASS
				+ ")" + WN.ADVICE_REGISTRY_CLASS + ".getAdvice(null,\"" + adviceClassName + "\");";
		adviceStr += WN.AROUND_ADVICE_VARIABLE + ".setParams(" + WN.ADVICE_PARAMS_VARIABLE + ");";

		String proceedStr = "if(" + WN.AROUND_ADVICE_VARIABLE + ".isProceed(" + WN.JOINPOINT_VARIABLE + "))";

		if ("void".equalsIgnoreCase(ctMethod.getReturnType().getName()))
		{
			proceedStr += "{" + backupName + "($$);}";
		}
		else
		{
			proceedStr += "{" + WN.JOINPOINT_VARIABLE + ".setReturnValue(($w)" + backupName + "($$));}";
		}

		String afterSrc = WN.AROUND_ADVICE_VARIABLE + ".after(" + WN.JOINPOINT_VARIABLE + ");";
		String returnStr = "return ($r)" + WN.JOINPOINT_VARIABLE + ".getReturnValue();";

		proxyAroundSrcBuilder.append("{");
		proxyAroundSrcBuilder.append(paramsInitStr).append(joinpointStr).append(adviceStr).append(beforeSrc)
				.append(proceedStr);
		if (!asFinally)
			proxyAroundSrcBuilder.append(afterSrc);
		if (!"void".equalsIgnoreCase(ctMethod.getReturnType().getName()))
		{
			proxyAroundSrcBuilder.append(returnStr);
		}
		proxyAroundSrcBuilder.append("}");

		try
		{
			proxyMethod.setBody(proxyAroundSrcBuilder.toString());
			LOG.info(proxyAroundSrcBuilder.toString());
		}
		catch (CannotCompileException e1)
		{
			LOG.log(Level.SEVERE, "Cannot compile : " + proxyAroundSrcBuilder.toString(), e1);
			ctMethod.setName(methodName);
			return;
		}

		try
		{
			if (asFinally)
			{
				LOG.info("Around's after as finally : " + adviceClassName);
				// re-declare variables
				afterSrc = paramsInitStr + joinpointStr + adviceStr + afterSrc;
				proxyMethod.insertAfter(afterSrc, asFinally);
			}
			ctMethod.getDeclaringClass().addMethod(proxyMethod);

		}
		catch (CannotCompileException e)
		{
			LOG.log(Level.SEVERE, "Cannot compile after : " + afterSrc, e);
			ctMethod.setName(methodName);
			e.printStackTrace();
		}

	}

	/**
	 * @param ctMethod
	 * @param adviceClass
	 * @param aspectId
	 * @param exceptionType
	 */
	public static void bodyAddCatch(CtMethod ctMethod, String adviceClass, String aspectId, String exceptionType)
			throws Exception
	{
		if (exceptionType == null || exceptionType.isEmpty())
		{
			// catch them all by default
			exceptionType = Throwable.class.getName();
		}

		String paramsInitStr = Map.class.getName() + " " + paramsInitString(ctMethod, aspectId) + LINE_SEPERATOR;
		String joinpointInitStr = WN.JOINPOINT_CLASS + " " + joinpointInitStr(ctMethod) + LINE_SEPERATOR;
		String throwStr = "throw $e;" + LINE_SEPERATOR;
		String adviceStr = WN.ADD_CATCH_ADVICE_CLASS + " " + WN.ADD_CATCH_ADVICE_VARIABLE + " = ("
				+ WN.ADD_CATCH_ADVICE_CLASS + ")" + WN.ADVICE_REGISTRY_CLASS + ".getAdvice(null,\"" + adviceClass
				+ "\");";
		adviceStr += WN.ADD_CATCH_ADVICE_VARIABLE + ".setParams(" + WN.ADVICE_PARAMS_VARIABLE + ");" + LINE_SEPERATOR;
		if ("void".equals(ctMethod.getReturnType().getName()))
		{
			throwStr = "if(" + WN.ADD_CATCH_ADVICE_VARIABLE + ".suppress()){return ;}" + throwStr;
		}

		// Note that the inserted code fragment must end with a throw or return statement.
		String addCatchSrc = "{" + paramsInitStr + joinpointInitStr + WN.JOINPOINT_VARIABLE + ".setException($e);"
				+ adviceStr + WN.ADD_CATCH_ADVICE_VARIABLE + ".addCatch(" + WN.JOINPOINT_VARIABLE + ");" + throwStr
				+ "}" + LINE_SEPERATOR;

		try
		{
			CtClass etype = ClassPool.getDefault().get(exceptionType);
			ctMethod.addCatch(addCatchSrc, etype);
		}
		catch (CannotCompileException e2)
		{
			LOG.log(Level.SEVERE, "Cannot compile addCatch : " + addCatchSrc, e2);
			e2.printStackTrace();
		}

	}

	/**
	 * @param ctMethod
	 * @param adviceClassName
	 * @param aspectId
	 */
	public static void bodySourceReplace(CtMethod ctMethod, String adviceClassName, String aspectId) throws Exception
	{
		String methodName = ctMethod.getName();
		String backupName = methodName + "$original";
		ctMethod.setName(backupName);
		String source = null;
		try
		{
			CtMethod proxyMethod = CtNewMethod.copy(ctMethod, methodName, ctMethod.getDeclaringClass(), null);
			SourceReplaceAdvice srAdvice = (SourceReplaceAdvice) AdviceRegistry.getAdvice(null, adviceClassName);
			Map<String, String> adviceParams = AspectRegistry.getParams(aspectId);
			srAdvice.setParams(adviceParams);
			source = srAdvice.source(ctMethod);
			//replace proceed string with actual method call statement.
			if (source == null || source.isEmpty())
			{
				LOG.log(Level.WARNING, "Empty source provided by : " + adviceClassName);
				return;
			}
			source = source.replace(SourceReplaceAdvice.PROCEED_STRING, backupName + "($$)");
			LOG.info("Replace with source : " + source);
			proxyMethod.setBody(source);
			ctMethod.getDeclaringClass().addMethod(proxyMethod);
		}
		catch (CannotCompileException e)
		{
			LOG.log(Level.SEVERE, "Failed to source replace " + methodName + ", adviceClassName: " + adviceClassName, e);
			e.printStackTrace();
			// restore
			ctMethod.setName(methodName);
		}

	}

	/**
	 * @param ctMethod
	 * @param adviceClassName
	 * @param aspectId
	 */
	public static void bodyReturnValueReplace(CtMethod ctMethod, String adviceClassName, String aspectId)
			throws Exception
	{
		String methodName = ctMethod.getName();
		String backupName = methodName + "$original";
		ctMethod.setName(backupName);
		StringBuilder srcBuilder = new StringBuilder();

		String paramsInitStr = Map.class.getName() + " " + paramsInitString(ctMethod, aspectId);

		try
		{
			String returnTypeName = ctMethod.getReturnType().getName();
			CtMethod proxyMethod = CtNewMethod.copy(ctMethod, methodName, ctMethod.getDeclaringClass(), null);
			String joinpointStr = joinpointInitStr(ctMethod);
			// wrapping brace
			srcBuilder.append("{");

			srcBuilder.append(WN.JOINPOINT_CLASS + " " + joinpointStr);
			srcBuilder.append(paramsInitStr);
			srcBuilder.append(WN.RETURN_VALUE_REPLACE_ADVICE_CLASS + " " + WN.RETURN_VALUE_REPLACE_ADVICE_VARIABLE
					+ " = (" + WN.RETURN_VALUE_REPLACE_ADVICE_CLASS + ")" + WN.ADVICE_REGISTRY_CLASS
					+ ".getAdvice(null,\"" + adviceClassName + "\");");
			srcBuilder.append(WN.RETURN_VALUE_REPLACE_ADVICE_VARIABLE + ".setParams(" + WN.ADVICE_PARAMS_VARIABLE
					+ ");");
			//if proceed, call original method and pass its return value to advice.replace through join point
			srcBuilder.append("if(" + WN.RETURN_VALUE_REPLACE_ADVICE_VARIABLE + ".isProceed(" + WN.JOINPOINT_VARIABLE
					+ ")){");

			if (!"void".equalsIgnoreCase(returnTypeName))
			{
				srcBuilder.append(returnTypeName + " result = ");
			}
			srcBuilder.append(backupName + "($$);");
			if (!"void".equalsIgnoreCase(returnTypeName))
			{
				// end if 
				srcBuilder.append(WN.JOINPOINT_VARIABLE + ".setReturnValue(($w)result);}");
				srcBuilder.append("return ($r)" + WN.RETURN_VALUE_REPLACE_ADVICE_VARIABLE + ".returnValue("
						+ WN.JOINPOINT_VARIABLE + ");");
			}
			else
			{
				// end if proceed
				srcBuilder.append("}");
			}
			// closing wrapping brace
			srcBuilder.append("}");

			proxyMethod.setBody(srcBuilder.toString());
			ctMethod.getDeclaringClass().addMethod(proxyMethod);
		}
		catch (CannotCompileException e1)
		{
			LOG.severe("Failed to add replace advice : " + e1.getMessage());
			e1.printStackTrace();
			// restore
			ctMethod.setName(methodName);
		}

	}

	public static void expressionEdit(CtMethod ctMethod, String adviceClassName)
	{
		ExpressionAdvice exprAdvice = (ExpressionAdvice) AdviceRegistry.getAdvice(null, adviceClassName);
		for (ExpressionEdition edition : exprAdvice.getExpressionEditions())
		{
			try
			{
				ExprEditor editor = ExpressionEditorUtil.buildExprEditor(edition);
				if (editor != null)
				{
					ctMethod.instrument(editor);
				}
			}
			catch (CannotCompileException e)
			{
				LOG.severe("Failed to edit expression for " + ctMethod.getLongName() + ", " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	//TODO
	public static void callBefore(Expr expr)
	{
		if (Modifier.isStatic(expr.where().getModifiers()))
		{
			// inside static method, no 'this'
		}
	}

	//TODO
	public static void callAfter(Expr expr)
	{

	}

	//TODO
	public static void callReplace(Expr expr)
	{

	}

	//TODO
	public static void bodyReplaceConstructor(CtMethod ctMethod)
	{

	}

	//TODO
	public static void callReplaceConstructor(Expr expr)
	{

	}

}
