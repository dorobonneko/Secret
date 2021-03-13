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
import com.moe.pussy.Pussy;
import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;

public class Runtime 
{
	
	private Fragment fragment;
	private Handler mHandler;
	public Window window;
    private static SSLSocketFactory ssl;
	public Runtime(Fragment fragment)
	{
		this.fragment=fragment;
		window=new Window((AppBrandFragment)fragment);
		mHandler=new Handler(Looper.getMainLooper());
	}
    public HttpURLConnection open(String url){
        try {
            HttpURLConnection huc=(HttpURLConnection) new URL(url).openConnection();
            if(huc instanceof HttpsURLConnection)
                ((HttpsURLConnection)huc).setSSLSocketFactory(getSSLSocketFactory());
                return huc;
        } catch (IOException e) {}
        return null;
    }
    private SSLSocketFactory getSSLSocketFactory(){
        if(ssl==null){
            synchronized(SSLSocketFactory.class){
                if(ssl==null){
                    try {
                        SSLContext sc = SSLContext.getInstance("TLS");
                        sc.init(null, new TrustManager[]{new X509TrustManager(){

                                         @Override
                                         public void checkClientTrusted(X509Certificate[] p1, String p2) throws CertificateException {
                                         }

                                         @Override
                                         public void checkServerTrusted(X509Certificate[] p1, String p2) throws CertificateException {
                                         }

                                         @Override
                                         public X509Certificate[] getAcceptedIssuers() {
                                             return new X509Certificate[0];
                                         }
                                     }}, new SecureRandom());
                          ssl=sc.getSocketFactory();
                    } catch (Exception e) {}
                }
            }
        }
        return ssl;
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
			case "pussy":
				return new PussyProxy(Pussy.$(fragment.getActivity()));
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
    public void alert(final String message){
        mHandler.post(new Runnable(){
            public void run(){
        new AlertDialog.Builder(fragment.getActivity()).setMessage(message).show();
        }});
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
	public class PussyProxy{
		private Pussy pussy;
		PussyProxy(Pussy pussy){
			this.pussy=pussy;
		}
		public int getActiveResourceSize(){
			return pussy.getActiveResource().size();
		}
		public String getActiveResource(){
			return pussy.getActiveResource().toString();
		}
	}
}
