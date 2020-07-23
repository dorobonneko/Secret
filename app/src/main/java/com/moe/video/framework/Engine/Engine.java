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


public class Engine
{
	private RhinoScriptEngine engine;
	public Engine(Fragment fragment){
		engine=new RhinoScriptEngine();
		final Runtime runtime=new Runtime(fragment);
		engine.put("runtime",runtime);
		try
		{
			engine.eval(new InputStreamReader(fragment.getContext().getAssets().open("function.js")));
		}
		catch (ScriptException e)
		{}
		catch (IOException e)
		{}
	}
	public Object eval(String script) throws ScriptException{
		return engine.eval(script);
	}
	public Object eval(Reader reader) throws ScriptException{
		return engine.eval(reader);
	}
	public Object invokeMethod(String name,Object... args) throws NoSuchMethodException, ScriptException{
		return engine.invokeFunction(name,args);
	}
}
