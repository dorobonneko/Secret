package com.moe.video.framework.Engine;
import java.util.List;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import org.mozilla.javascript.NativeObject;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaArray;

public class M3u {
    private Scriptable script;
    public M3u(Scriptable script){
        this.script=script;
    }
    public NativeObject[] parse(String data) throws IOException{
        NativeArray list=new NativeArray(1);
        BufferedReader input=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data.getBytes())));
        String line=null;
        int count=0;
        while((line=input.readLine())!=null){
            if(line.startsWith("#EXT-X-STREAM-INF")){
                NativeObject item=new NativeObject();
                item.setParentScope(list);
                line=line.substring(18);
                int index=0;
                while((index=line.indexOf("=",index))!=-1)
                {
                    int end=-1;
                    if(line.charAt(index+1)=='"'){
                        end=line.indexOf("\"",index);
                    }else{
                        int _end=line.indexOf(",",index);
                        if(_end==-1)
                            end=line.length();
                        else
                            end=_end;
                    }
                    item.putConstProperty(item,line.substring(0,index),line.substring(index+1,end));
                    index=0;
                    if(end==line.length())break;
                    line=line.substring(end+1);
                }
                item.put("url",item,input.readLine());
                list.put(count,list,item);
                count++;
            }
            
        }
        return (NativeObject[])list.toArray(new NativeObject[0]);
    }
    
}
