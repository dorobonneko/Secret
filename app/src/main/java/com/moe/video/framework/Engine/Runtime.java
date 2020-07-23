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

public class Runtime 
{
	
	private Fragment fragment;
	public Runtime(Fragment fragment)
	{
		this.fragment=fragment;
	}

	public void toast(final String msg)
	{
		final Context context=fragment.getContext();
		if(context==null)return;
		if(Thread.currentThread()!=Looper.getMainLooper().getThread())
		{
			new Handler(Looper.getMainLooper()).post(new Runnable(){

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
}
