package com.moe.video.framework.Engine;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ContextFactory;
import com.sun.script.javascript.RhinoScriptEngine;
import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.IOException;
import org.mozilla.javascript.NativeJSON;
import java.io.Reader;
import org.jsoup.Jsoup;
import android.app.Fragment;
import com.moe.video.framework.activity.fragment.AppBrandFragment;
import org.mozilla.javascript.NativeJavaPackage;
import java.lang.reflect.InvocationTargetException;
import org.mozilla.javascript.NativeJavaTopPackage;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.NativeJavaObject;


public class Engine
{
	//private RhinoScriptEngine engine;
    private Scriptable script;
    private Context context;
    public Engine(Window.Callback fragment) throws IOException{
		//engine=new RhinoScriptEngine();
        context=Context.enter();
        context.setOptimizationLevel(-1);
        context.setLanguageVersion(Context.VERSION_ES6);
        final Runtime runtime=new Runtime(fragment,this);
        script=context.initSafeStandardObjects();
        NativeJavaTopPackage.init(context,script,false);
        script.put("runtime",script,runtime);
        eval(new InputStreamReader(fragment.getContext().getAssets().open("function.js")), "function.js");
	}
    public NativeObject getModule(){
         return (NativeObject) script.get("module",script);
        
    }
    public <T extends Object> T getOrDefault(String key,String defaultValue){
        return (T)getModule().getOrDefault(key,defaultValue);
    }
    public Scriptable getScriptable() {
        return script;
    }
	public Runtime getRuntime(){
		return (Runtime)script.get("runtime",script);
	}
	public Object eval(String script,String name){
		Object result=null;
        result=context.evaluateString(this.script,script, name, 1, null);
        return result;
	}
	public Object eval(Reader reader,String name) throws IOException{
		Object result=null;
        result=context.evaluateReader(script, reader, name, 1, null);
         return result;
	}
    public Context getContext(){
        return context;
    }
	/*public Object invokeMethod(String name,Object... args) throws NoSuchMethodException, ScriptException{
		return engine.invokeMethod(engine,name,args);
	}*/
}
