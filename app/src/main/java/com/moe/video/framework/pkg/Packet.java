package com.moe.video.framework.pkg;
import java.util.zip.ZipFile;
import java.io.IOException;
import java.io.InputStream;
import com.moe.video.framework.util.StringUtil;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.text.TextUtils;

public class Packet
{
	public String packageName;
	public String source;
	public String exe,title;
	String logo;
	public int version;
	private ZipFile file;
	Packet(String  path,boolean readHead) throws IOException{
		this.source=path;
		file=new ZipFile(path);
		BufferedReader br=new BufferedReader(new InputStreamReader(file.getInputStream(file.getEntry("info.ini"))));
		String line=null;
		while((line=br.readLine())!=null){
			if(line.startsWith("#")||TextUtils.isEmpty(line.trim()))continue;
			int index=line.indexOf("=");
			if(index!=-1){
				String value=line.substring(index+1).trim();
				switch(line.substring(0,index).trim().toLowerCase()){
					case "packagename":
						packageName=value;
						break;
					case "exe":
						exe=value;
						break;
					case "logo":
						logo=value;
						break;
					case "title":
						title=value;
						break;
					case "version":
						try{
						version=Integer.parseInt(value);
						}catch(Exception e){}
						break;
				}
			}
		}
		br.close();
		if(readHead)
			file.close();
	}
	public InputStream getFile(String path){
		InputStream input=null;
		try
		{
			input=file.getInputStream(file.getEntry(path));
			return input;
		}
		catch (IOException e)
		{}
		return null;
	}
	InputStream loadLogo(){
		try
		{
			return file.getInputStream(file.getEntry(logo));
		}
		catch (Exception e)
		{}
		return null;
	}
	public void close(){
		try
		{
			if (file != null)file.close();
		}
		catch (IOException e)
		{}
	}
}
