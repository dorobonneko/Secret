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

public abstract class ModelActivity extends Activity
{
	private Packet mPacket;
	private String data;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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
		}
	}
public void play(final String json){
	if(Settings.canDrawOverlays(getApplicationContext())){
					startActivity(new Intent(getApplicationContext(),VideoActivity.class).putExtra("data",json));
						}else{
							ModelActivity.this.data=json;
						Intent request = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
						request.setData(Uri.parse("package:" + getPackageName()));
						request.removeFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivityForResult(request,14);
						
						}

}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data)
{
	super.onActivityResult(requestCode, resultCode, data);
	if(requestCode==14&&resultCode==RESULT_OK){
		startActivity(new Intent(getApplicationContext(),VideoActivity.class).putExtra("data",this.data));
		
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
