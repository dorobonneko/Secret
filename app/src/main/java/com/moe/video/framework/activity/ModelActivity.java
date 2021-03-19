package com.moe.video.framework.activity;
import android.app.Activity;
import android.os.Bundle;
import android.app.ActivityManager;
import com.moe.video.framework.pkg.Packet;
import com.moe.video.framework.util.StringUtil;
import java.io.IOException;
import com.moe.video.framework.pkg.PacketManager;
import android.graphics.Bitmap;
import com.moe.video.framework.util.BitmapUtil;
import java.io.Reader;
import java.io.InputStreamReader;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Intent;
import android.widget.TextView;
import com.moe.video.framework.activity.fragment.AppBrandFragment;
import android.view.View;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Toast;
import android.provider.Settings;
import android.net.Uri;
import android.content.res.Configuration;
import com.moe.video.framework.VideoActivity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.view.Gravity;
import com.moe.video.framework.R;
import android.app.AlertDialog;
import com.moe.neko.Neko;

public abstract class ModelActivity extends Activity
{
	private Packet mPacket;
	private String data;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		registerReceiver(new BroadcastReceiver(){

				@Override
				public void onReceive(Context p1, Intent p2)
				{
					if(getTaskId()==p2.getIntExtra("id",-1))
						finishAndRemoveTask();
				}
			}, new IntentFilter(getPackageName()));
		mPacket=PacketManager.getInstance().getPacket(getIntent().getAction());
		Bitmap logo=BitmapUtil.DrawableToBitmap(PacketManager.getInstance().loadLogo(mPacket));
		ActivityManager.TaskDescription task=new ActivityManager.TaskDescription(mPacket.title,logo);
		setTaskDescription(task);
		ActivityTask.TaskDesc desc=new ActivityTask.TaskDesc();
		desc.index=getIndex();
		desc.taskId=getTaskId();
		desc.packageName=mPacket.packageName;
		ActivityTask.addTask(this,desc);
		if(savedInstanceState==null){
		AppBrandFragment abf=new AppBrandFragment();
		Bundle mBundle=new Bundle();
		mBundle.putString("exe",mPacket.exe);
		abf.setArguments(mBundle);
		getFragmentManager().beginTransaction().add(android.R.id.content,abf).addToBackStack(null).commit();
		ViewGroup vg=(ViewGroup) getWindow().getDecorView();
		ImageView debug=null;
		vg.addView(debug=new ImageView(this),new FrameLayout.LayoutParams(-2,-2,Gravity.BOTTOM|Gravity.END));
		debug.setImageResource(R.drawable.close);
			debug.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View p1)
					{
                        new AlertDialog.Builder(p1.getContext()).setMessage(Neko.with(p1).printCacheStatus()).show();
						//new AlertDialog.Builder(p1.getContext()).setMessage(Pussy.$(p1.getContext()).getActiveResource().size()+"\n"+Pussy.$(p1.getContext()).getActiveResource().toString()).show();
					}
				});
		}
	}



public Packet getPacket(){
	if(mPacket==null)
		mPacket=PacketManager.getInstance().getPacket(getIntent().getAction());
	return mPacket;
}

@Override
public void finish()
{
	finishAndRemoveTask();
}

	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();
	}
	public abstract int getIndex();

	@Override
	public void onBackPressed()
	{
		// TODO: Implement this method
		super.onBackPressed();
		if(getFragmentManager().getBackStackEntryCount()==0)
			super.onBackPressed();
	}
	
}
