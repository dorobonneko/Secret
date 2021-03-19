package com.moe.video.framework.Engine;
import java.util.Base64;

public class Base64 {
    
    public static String decode(String data){
        return new String(Base64.getDecoder().decode(data));
    }
    public static String encode(String data){
        return Base64.getEncoder().encodeToString(data.getBytes());
    }
}
