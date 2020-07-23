package com.moe.video.framework.util;
import java.io.File;

public class FileUtil
{
	public static void delete(File f){
		if(f.isFile())
			f.delete();
			else{
		for(File ff:f.listFiles())
			delete(ff);
			f.delete();
			}
	}
}
