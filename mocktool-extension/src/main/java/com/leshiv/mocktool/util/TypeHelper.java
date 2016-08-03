package com.leshiv.mocktool.util;

import java.util.regex.Pattern;

public class TypeHelper
{
	private static final Pattern BOOLEAN_PATTERN = Pattern.compile("^(?:true|True|TRUE|false|False|FALSE)$");
	private static final Pattern FLOAT_PATTERN = Pattern
			.compile("^(?:[-+]?(?:[0-9][0-9_]*)\\.[0-9_]*(?:[eE][-+][0-9]+)?|[-+]?(?:[0-9][0-9_]*)?\\.[0-9_]+(?:[eE][-+][0-9]+)?|[-+]?[0-9][0-9_]*(?::[0-5]?[0-9])+\\.[0-9_]*|[-+]?\\.(?:inf|Inf|INF)|\\.(?:nan|NaN|NAN))$");
	private static final Pattern INTEGER_PATTERN = Pattern
			.compile("^(?:[-+]?0b[0-1_]+|[-+]?0[0-7_]+|[-+]?(?:0|[1-9][0-9_]*)|[-+]?0x[0-9a-fA-F_]+|[-+]?[1-9][0-9_]*(?::[0-5]?[0-9])+)$");

	public static boolean booleanValueOf(String str, boolean defaultValue)
	{
		if (str != null && BOOLEAN_PATTERN.matcher(str).matches())
		{
			return Boolean.valueOf(str);
		}
		return defaultValue;
	}

	public static float floatValueOf(String str, float defaultValue)
	{
		if (str != null && FLOAT_PATTERN.matcher(str).matches())
		{
			return Float.valueOf(str);
		}
		return defaultValue;
	}

	public static int intValueOf(String str, int defaultValue)
	{
		if (str != null && INTEGER_PATTERN.matcher(str).matches())
		{
			return Integer.valueOf(str);
		}
		return defaultValue;
	}

	public static Object stringToObject(String str)
	{
		if (str == null || str.isEmpty())
			return null;
		str = str.trim();
		if (BOOLEAN_PATTERN.matcher(str).matches())
		{
			return Boolean.valueOf(str);
		}
		if (FLOAT_PATTERN.matcher(str).matches())
		{
			return Float.valueOf(str);
		}
		if (INTEGER_PATTERN.matcher(str).matches())
		{
			return Integer.valueOf(Integer.parseInt(str));
		}
		return str;
	}

}
