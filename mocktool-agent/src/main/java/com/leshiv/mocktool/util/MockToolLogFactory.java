package com.leshiv.mocktool.util;

import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.File;
import java.util.logging.ConsoleHandler;

public class MockToolLogFactory
{
	static int MAX_FILE_SIZE = 10485760;
	static int MAX_BACKUP_INDEX = 1;
	static Logger LOG = Logger.getLogger("MockToolLogger");

	static
	{
		LOG.setUseParentHandlers(false);
		FileHandler fh = getFileHandler(null, null);
		ConsoleHandler ch = getConsoleHandler();
		Formatter fm = new SingleLineFormatter();
		fh.setFormatter(fm);
		ch.setFormatter(fm);
		LOG.addHandler(fh);
//		LOG.addHandler(ch);
	}

	public static Logger getLogger(Class<?> c)
	{
		Logger logger = Logger.getLogger(c.getName());
		logger.setUseParentHandlers(false);
		return logger;
	}

	public static Logger getDefaultLogger()
	{
		return LOG;
	}

	public static FileHandler getFileHandler()
	{
		return getFileHandler(null, null);
	}

	public static FileHandler getFileHandler(String logRoot, String filename)
	{
		String homePath = null;
		String filePath = null;

		if (logRoot != null)
		{
			File f = new File(logRoot.trim());
			if ((f.isDirectory()) && (f.canWrite()))
			{
				homePath = logRoot;
			}
		}
		if (homePath == null)
		{
			homePath = (String) System.getProperties().get("user.home") + File.separator + "mocktool-logs";
			//			homePath = AgentJarHelper.getAgentJarDirectory() + File.separator + "mocktoollogs";
		}

		File homeDir = new File(homePath);
		if (!homeDir.exists())
		{
			homeDir.mkdir();
		}

		if (filename == null)
		{
			filename = "mocktoollog";
		}

		filePath = homePath + File.separator + filename + ".log";

		try
		{
			FileHandler fileHandler = new FileHandler(filePath, MAX_FILE_SIZE, MAX_BACKUP_INDEX);
			fileHandler.setFormatter(new SimpleFormatter());
			return fileHandler;
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to init FileHandler: " + filePath);
		}
	}

	public static ConsoleHandler getConsoleHandler()
	{
		return new ConsoleHandler();
	}

	public static void main(String[] args)
	{
		Logger LOG = getDefaultLogger();
		LOG.info("msg");
	}
}
