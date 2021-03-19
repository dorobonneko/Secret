package com.moe.video.framework.util;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StaticData {
    
    private final static Map<String,Object> data=new HashMap<String,Object>();
    
    public final static synchronized void put(String key,Object value){
        data.put(key,value);
    }
    public final static synchronized <O extends Object> O get(String key){
        return (O)data.get(key);
    }
    public final static synchronized <O extends Object> O delete(String key){
        return (O)data.remove(key);
    }
    public final static String uid(){
        return UUID.randomUUID().toString();
    }
}
