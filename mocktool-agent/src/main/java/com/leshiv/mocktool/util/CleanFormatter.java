package com.leshiv.mocktool.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CleanFormatter extends Formatter
{

	Date dat = new Date();
	private final static String format = "{0,date} {0,time}";
	private MessageFormat formatter;
	private Object args[] = new Object[1];
	private String lineSeparator = System.getProperty("line.separator");

	public synchronized String format(LogRecord record)
	{

		StringBuilder sb = new StringBuilder();

		// Level
		sb.append("[");
		//		sb.append(record.getLevel().getLocalizedName());
		sb.append(record.getLevel().getName().toUpperCase());
		sb.append("] ");

		// Minimize memory allocations here.
		dat.setTime(record.getMillis());
		args[0] = dat;

		// Date and time 
		StringBuffer text = new StringBuffer();
		if (formatter == null)
		{
			formatter = new MessageFormat(format);
		}
		formatter.format(args, text, null);
		sb.append(text);
		sb.append(" ");

		String message = formatMessage(record);

		sb.append(message);
		sb.append(lineSeparator);
		if (record.getThrown() != null)
		{
			try
			{
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
			}
			catch (Exception ex)
			{
			}
		}
		return sb.toString();
	}
}