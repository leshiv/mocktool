package com.leshiv.mocktool.util;

import java.util.logging.Logger;

import com.leshiv.mocktool.aop.api.ExpressionEdition;

import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class ExpressionEditorUtil
{
	private static Logger LOG = MockToolLogFactory.getDefaultLogger();

	public static ExprEditor buildExprEditor(final ExpressionEdition exprEdition)
	{
		ExprEditor editor = null;
		LOG.info("Build ExprEditor,type: " + exprEdition.getExpressionType().getValue() + ", className: "
				+ exprEdition.getClassName() + ", methodName: " + exprEdition.getMethodName() + ", replaceStatement: "
				+ exprEdition.getReplaceStatement());

		switch (exprEdition.getExpressionType())
		{
			case CAST:
				break;
			case CONSTRUCTOR_CALL:
				break;
			case HANDLER:
				break;
			case INSTANCE_OF:
				break;
			case METHOD_CALL:
				editor = new ExprEditor() {
					public void edit(MethodCall mc) throws CannotCompileException
					{
						if (mc.getClassName().equals(exprEdition.getClassName())
								&& mc.getMethodName().equals(exprEdition.getMethodName()))
						{
							mc.replace(exprEdition.getReplaceStatement());
						}
					}
				};
				break;
			case NEW_ARRAY:
				break;
			case NEW_EXPR:
				break;
			default:
				LOG.warning("Invalid ExpressionType : " + exprEdition.getExpressionType().getValue());
				break;
		}

		return editor;
	}
}
