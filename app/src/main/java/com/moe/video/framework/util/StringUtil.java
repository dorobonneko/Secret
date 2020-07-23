package com.moe.video.framework.util;
import java.io.InputStream;
import java.io.IOException;

public class StringUtil
{

	public static String toString(InputStream input)
	{
		StringBuilder sb=new StringBuilder();
		byte[] b=new byte[1024];
		int len=-1;
		try
		{
			while ((len = input.read(b)) != -1)
			{
				sb.append(new String(b, 0, len));
			}
		}
		catch (IOException e)
		{}
		finally{
			try
			{
				input.close();
			}
			catch (Exception e)
			{}
		}
		return sb.toString();
	}
	
}
