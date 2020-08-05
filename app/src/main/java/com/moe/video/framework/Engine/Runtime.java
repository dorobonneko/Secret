package com.moe.video.framework.Engine;
import android.widget.Toast;
import android.os.Looper;
import android.os.Handler;
import com.sun.script.javascript.RhinoScriptEngine;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import android.app.Fragment;
import android.content.Context;
import com.moe.video.framework.activity.fragment.AppBrandFragment;
import android.os.Bundle;
import android.content.ClipboardManager;
import android.content.Intent;
import android.provider.Settings;
import android.net.Uri;
import com.moe.video.framework.activity.ModelActivity;
import android.widget.EditText;
import android.app.AlertDialog;
import android.content.DialogInterface;
import org.mozilla.javascript.Function;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import com.moe.video.framework.util.StringUtil;
import java.io.FileInputStream;

public class Runtime 
{
	
	private Fragment fragment;
	private Handler mHandler;
	public Window window;
	public Runtime(Fragment fragment)
	{
		this.fragment=fragment;
		window=new Window((AppBrandFragment)fragment);
		mHandler=new Handler(Looper.getMainLooper());
	}

	public void toast(final String msg)
	{
		final Context context=fragment.getContext();
		if(context==null)return;
		if(Thread.currentThread()!=Looper.getMainLooper().getThread())
		{
			mHandler.post(new Runnable(){

					@Override
					public void run()
					{
						Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
					}
				});
		}else
		Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
	}
	
	public byte[] Byte(int size){
		return new byte[size];
	}
	public Object $require(String name){
		switch(name){
			case "jsoup":
				return new Jsoup();
			case "html":
				return Parser.htmlParser();
			case "database":
				return new Database(fragment.getContext(),window.getPackageName());
		}
		return null;
	}
	public void open(String type,String args){
		Fragment f=new AppBrandFragment();
				Bundle b=new Bundle();
				b.putString("exe",type.concat(".js"));
				b.putString("args",args);
				f.setArguments(b);
				fragment.getFragmentManager().beginTransaction().add(android.R.id.content,f).addToBackStack(null).commitAllowingStateLoss();
		
	}
	public void copy(String text){
		ClipboardManager cm=(ClipboardManager) fragment.getContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE);
		cm.setText(text);
	}
	public void play(String json){
		((ModelActivity)fragment.getActivity()).play(json);
		}
	public void _prompt(final String message,final String defaultValue,final Object result){
		mHandler.post(new Runnable(){
			public void run(){
		final EditText input=new EditText(fragment.getContext());
		input.setSingleLine();
		input.setText(defaultValue);
		final AlertDialog dialog=new AlertDialog.Builder(fragment.getActivity()).setTitle(message).setView(input).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					Function fun=(Function) result;
					org.mozilla.javascript.Context context=org.mozilla.javascript.Context.enter();
					fun.call(context,fun,fun,new Object[]{input.getText().toString()});
					context.exit();
				}
			}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					p1.cancel();
				}
			}).setOnCancelListener(new DialogInterface.OnCancelListener(){

				@Override
				public void onCancel(DialogInterface p1)
				{
					Function fun=(Function) result;
					org.mozilla.javascript.Context context=org.mozilla.javascript.Context.enter();
					fun.call(context,fun,fun,new Object[]{false});
					context.exit();
				}
			}).show();
			}});
	}
	public void _confirm(final String title,final String message,final Object result){
		mHandler.post(new Runnable(){
			public void run(){
		final AlertDialog dialog=new AlertDialog.Builder(fragment.getActivity()).setTitle(title).setMessage(message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					Function fun=(Function) result;
					org.mozilla.javascript.Context context=org.mozilla.javascript.Context.enter();
					fun.call(context,fun,fun,new Object[]{true});
					context.exit();
				}
			}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					p1.cancel();
				}
			}).setOnCancelListener(new DialogInterface.OnCancelListener(){

				@Override
				public void onCancel(DialogInterface p1)
				{
					Function fun=(Function) result;
					org.mozilla.javascript.Context context=org.mozilla.javascript.Context.enter();
					fun.call(context,fun,fun,new Object[]{false});
					context.exit();
				}
			}).show();
			}
			});
	}
	public String load(String uri) throws Exception{
		Uri url=Uri.parse(uri);
		if(url.getScheme()!=null)
		switch(url.getScheme()){
			case "file":
				String path=url.getPath();
				if(path.startsWith("/android_asset/"))
					return StringUtil.toString(fragment.getContext().getAssets().open(path.substring(15)));
					else
					return StringUtil.toString(new FileInputStream(uri));
		}else{
			if(uri.startsWith("/"))
				return StringUtil.toString(new FileInputStream(uri));
				else
				return null;
		}
		 return StringUtil.toString(fragment.getContext().getContentResolver().openInputStream(Uri.parse(uri)));
	}
	/*public void prompt(String message,Object result){
		prompt(message,null,result);
	}*/
}
