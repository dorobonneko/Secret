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
import org.mozilla.javascript.NativeObject;
import java.util.Map;
import java.util.HashMap;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.NativeArray;
import java.util.Collections;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.TreeMap;
import android.util.ArrayMap;
import java.util.LinkedHashMap;
import javax.script.ScriptException;

public class Runtime {

	private Runtime.Callback callback;
	private Handler mHandler;
	public Window window;
    private static SSLSocketFactory ssl;
    public Base64 Base64=new Base64();
	public Runtime(Runtime.Callback callback) {
		this.callback = callback;
		window = new Window((Window.Callback)callback);
        mHandler = new Handler(Looper.getMainLooper());
	}
  
    public Object exec(String path) throws ScriptException {
        return window.getEngine().eval(new InputStreamReader(window.getPacket().getFile(path)));
    }
    public void options(NativeObject obj, final Function callback) {
        final String title=ScriptRuntime.toString(obj.get("title"));
        final String defaultValue=ScriptRuntime.toString(obj.get("defaultValue"));
        NativeArray opts=(NativeArray) obj.get("options");
        final Map<String,String> opt=new LinkedHashMap<String,String>();
        if (opts != null)
            for (int i=0;i < opts.size();i++) {
                NativeObject item=(NativeObject) opts.get(i);

                opt.put(ScriptRuntime.toString(item.get("name")), ScriptRuntime.toString(item.get("value")));
            }
        final String[] values=opt.values().toArray(new String[0]);
        final int index=Arrays.asList(values).indexOf(defaultValue);

        mHandler.post(new Runnable(){

                @Override
                public void run() {
                    new AlertDialog.Builder(window.getContext()).setTitle(title).setSingleChoiceItems(opt.keySet().toArray(new String[0]), index, new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface p1, int p2) {
                                org.mozilla.javascript.Context context=org.mozilla.javascript.Context.enter();
                                callback.call(context, callback, callback, new Object[]{values[p2]});
                                context.exit();
                                p1.dismiss();
                            }
                        }).show();
                }
            });
    }
    public HttpURLConnection open(String url) {
        try {
            HttpURLConnection huc=(HttpURLConnection) new URL(url).openConnection();
            if (huc instanceof HttpsURLConnection)
                ((HttpsURLConnection)huc).setSSLSocketFactory(getSSLSocketFactory());
            return huc;
        } catch (IOException e) {}
        return null;
    }

    private SSLSocketFactory getSSLSocketFactory() {
        if (ssl == null) {
            synchronized (SSLSocketFactory.class) {
                if (ssl == null) {
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
                        ssl = sc.getSocketFactory();
                    } catch (Exception e) {}
                }
            }
        }
        return ssl;
    }
	public void toast(final String msg) {
		final Context context=window.getContext();
		if (context == null)return;
		if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
			mHandler.post(new Runnable(){

					@Override
					public void run() {
						Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
					}
				});
		} else
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public byte[] Byte(int size) {
		return new byte[size];
	}
	public Object $require(String name) {
		switch (name) {
			case "jsoup":
				return new Jsoup();
			case "html":
				return Parser.htmlParser();
			case "database":
				return new Database(window.getContext(), window.getPackageName());
			}
		return null;
	}
	
	public void copy(String text) {
		ClipboardManager cm=(ClipboardManager) window.getContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE);
		cm.setText(text);
	}
	public void play(String json) {
		window.openVideo(json);
    }
    public void alert(final String message) {
        mHandler.post(new Runnable(){
                public void run() {
                    new AlertDialog.Builder(window.getContext()).setMessage(message).show();
                }});
    }
	public void _prompt(final String message, final String defaultValue, final Object result) {
		mHandler.post(new Runnable(){
                public void run() {
                    final EditText input=new EditText(window.getContext());
                    input.setSingleLine();
                    input.setText(defaultValue);
                    final AlertDialog dialog=new AlertDialog.Builder(window.getContext()).setTitle(message).setView(input).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface p1, int p2) {
                                Function fun=(Function) result;
                                org.mozilla.javascript.Context context=org.mozilla.javascript.Context.enter();
                                fun.call(context, fun, fun, new Object[]{input.getText().toString()});
                                context.exit();
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface p1, int p2) {
                                p1.cancel();
                            }
                        }).setOnCancelListener(new DialogInterface.OnCancelListener(){

                            @Override
                            public void onCancel(DialogInterface p1) {
                                Function fun=(Function) result;
                                org.mozilla.javascript.Context context=org.mozilla.javascript.Context.enter();
                                fun.call(context, fun, fun, new Object[]{false});
                                context.exit();
                            }
                        }).show();
                }});
	}
	public void _confirm(final String title, final String message, final Object result) {
		mHandler.post(new Runnable(){
                public void run() {
                    final AlertDialog dialog=new AlertDialog.Builder(window.getContext()).setTitle(title).setMessage(message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface p1, int p2) {
                                Function fun=(Function) result;
                                org.mozilla.javascript.Context context=org.mozilla.javascript.Context.enter();
                                fun.call(context, fun, fun, new Object[]{true});
                                context.exit();
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface p1, int p2) {
                                p1.cancel();
                            }
                        }).setOnCancelListener(new DialogInterface.OnCancelListener(){

                            @Override
                            public void onCancel(DialogInterface p1) {
                                Function fun=(Function) result;
                                org.mozilla.javascript.Context context=org.mozilla.javascript.Context.enter();
                                fun.call(context, fun, fun, new Object[]{false});
                                context.exit();
                            }
                        }).show();
                }
			});
	}
	public String load(String uri) throws Exception {
		Uri url=Uri.parse(uri);
		if (url.getScheme() != null)
            switch (url.getScheme()) {
                case "file":
                    String path=url.getPath();
                    if (path.startsWith("/android_asset/"))
                        return StringUtil.toString(window.getContext().getAssets().open(path.substring(15)));
					else
                        return StringUtil.toString(new FileInputStream(uri));
            }else {
			if (uri.startsWith("/"))
				return StringUtil.toString(new FileInputStream(uri));
            else
				return null;

		}
        return StringUtil.toString(window.getContext().getContentResolver().openInputStream(Uri.parse(uri)));
	}
	public static interface Callback{}
}
