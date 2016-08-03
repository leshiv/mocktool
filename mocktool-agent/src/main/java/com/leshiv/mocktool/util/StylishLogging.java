package com.leshiv.mocktool.util;

public class StylishLogging
{
	public static final String LINE_SEPERATOR = System.getProperty("line.separator");
	public static final String SPLIT_STRING = "+----------------------------------------------------------------------+";

	public static String simpleBannerString(String message)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(LINE_SEPERATOR);
		sb.append(" | " + message).append(" |");
		return sb.toString();
	}

	public static String bannerString(String message)
	{
		if (message == null)
		{
			message = "";
		}
		StringBuilder sb = new StringBuilder();
		String splitString = SPLIT_STRING;
		if (message.length() + 4 > SPLIT_STRING.length())
		{
			StringBuilder splitbuilder = new StringBuilder();
			for (int i = 0; i < message.length() + 4; i++)
			{
				splitbuilder.append("-");
			}
			splitString = splitbuilder.toString();
		}

		sb.append(LINE_SEPERATOR);
		sb.append(" " + splitString + LINE_SEPERATOR);
		sb.append(" | " + message);
		for (int i = 0; i < (splitString.length() - message.length() - 3); i++)
		{
			sb.append(" ");
		}
		sb.append("|" + LINE_SEPERATOR);
		sb.append(" " + splitString);
		return sb.toString();
	}
}
