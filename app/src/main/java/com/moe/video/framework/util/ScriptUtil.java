package com.moe.video.framework.util;
import org.mozilla.javascript.ScriptRuntime;

public class ScriptUtil {
    
    public static String toString(Object o){
        if(o==null)
            return null;
            return ScriptRuntime.toString(o);
    }
    
}
