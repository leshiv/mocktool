package com.leshiv.mocktool.aop.api;

public enum ExpressionType
{
	CAST("Cast"), CONSTRUCTOR_CALL("ConstructorCall"), FIELD_ACCESS("FieldAccess"), HANDLER("Handler"), INSTANCE_OF(
			"InstanceOf"), METHOD_CALL("MethodCall"), NEW_ARRAY("NewArray"), NEW_EXPR("NewExpr");

	private String value;

	ExpressionType(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

}
