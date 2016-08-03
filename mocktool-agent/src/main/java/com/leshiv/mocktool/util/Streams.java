package com.leshiv.mocktool.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Streams
{
	public static byte[] read(InputStream input, boolean closeInputStream) throws IOException
	{
		return read(input, input.available(), closeInputStream);
	}

	public static byte[] read(InputStream input, int expectedSize, boolean closeInputStream) throws IOException
	{
		if (expectedSize <= 0)
		{
			expectedSize = 8192;
		}
		ByteArrayOutputStream outStream = new ByteArrayOutputStream(expectedSize);
		copy(input, outStream, expectedSize, closeInputStream);
		return outStream.toByteArray();
	}

	private static int copy(InputStream input, ByteArrayOutputStream output, int bufferSize, boolean closeStreams)
			throws IOException
	{
		try
		{
			byte[] buffer = new byte[bufferSize];
			int count = 0;
			int n = 0;
			while (-1 != (n = input.read(buffer)))
			{
				output.write(buffer, 0, n);
				count += n;
			}
			return count;
		}
		finally
		{
			if (closeStreams)
			{
				input.close();
				output.close();
			}
		}
	}
}