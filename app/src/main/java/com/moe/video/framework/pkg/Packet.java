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
	public String packageName,versionName;
	public String source;
	public String exe,title;
	public String logo;
	public int version;
	public Packet(String  path) throws IOException{
		this.source=path;
		ZipFile file=new ZipFile(path);
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
                    case "versionname":
                        versionName=value;
                        break;
                        
				}
			}
		}
		br.close();
		file.close();
	}
	
}
