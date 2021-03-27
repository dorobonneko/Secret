package com.moe.video.framework;

import android.app.*;
import android.os.*;
import android.widget.Toolbar;
import android.widget.GridView;
import com.moe.video.framework.pkg.Packet;
import java.util.List;
import com.moe.video.framework.adapter.AppAdapter;
import java.util.ArrayList;
import com.moe.video.framework.pkg.PacketManager;
import android.view.Menu;
import android.view.MenuItem;
import com.moe.video.framework.app.FolderDialog;
import java.io.File;
import android.Manifest;
import android.content.pm.PackageManager;
import java.io.IOException;
import android.widget.Toast;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.content.Intent;
import com.moe.video.framework.activity.ModelActivity;
import com.moe.video.framework.activity.ActivityTask;
import com.moe.video.framework.Engine.Engine;
import android.widget.AdapterView.OnItemLongClickListener;
import android.content.DialogInterface;
import android.net.Uri;
import java.net.HttpURLConnection;
import android.content.SharedPreferences;
import com.moe.neko.Neko;
import android.widget.ImageView.ScaleType;

public class MainActivity extends Activity implements FolderDialog.Callback,GridView.OnItemClickListener,GridView.OnItemLongClickListener,PacketManager.OnPacketChangedListener,
SharedPreferences.OnSharedPreferenceChangeListener
{
	private GridView mGridView;
	private AppAdapter mAppAdapter;
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		setActionBar((Toolbar)findViewById(R.id.toolbar));
		mGridView=findViewById(R.id.gridview);
		PacketManager.getInstance().registerOnPacketChangedListener(this);
		mGridView.setAdapter(mAppAdapter=new AppAdapter(PacketManager.getInstance()));
		mGridView.setNumColumns(4);
		mGridView.setOnItemClickListener(this);
		mGridView.setOnItemLongClickListener(this);
		getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		if(checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED)
			requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},4676);
        SharedPreferences preferences=getSharedPreferences("setting",MODE_PRIVATE);
        onSharedPreferenceChanged(preferences,"background");
        preferences.registerOnSharedPreferenceChangeListener(this);
        //startActivity(new Intent(this,AudioActivity.class));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences p1, String p2) {
        switch(p2){
            case "background":
                View view=findViewById(android.R.id.content);
                String background=p1.getString(p2,null);
                if(background==null)
                    view.setBackground(null);
                    else
                    Neko.with(view).load(background).scaleType(ScaleType.CENTER_CROP).fade(100).into(view);
                
                break;
        }
    }

       
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		if(requestCode==4676){
			if(grantResults[0]==PackageManager.PERMISSION_DENIED)finish();
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu,menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case R.id.add:
				FolderDialog fd=new FolderDialog(this);
				fd.setCallback(this);
				fd.show();
				break;
            case R.id.setting:
                startActivity(new Intent(this,SettingActivity.class));
                break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPath(File file)
	{
		try
		{
			PacketManager.getInstance().installPacket(file.getAbsolutePath());
		}
		catch (Exception e)
		{Toast.makeText(getApplicationContext(),"文件错误",Toast.LENGTH_SHORT).show();}
		
	}

	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		Packet p=(Packet)p1.getAdapter().getItem(p3);
		ActivityTask.startTask(this,p.packageName);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		final Packet p=(Packet)p1.getAdapter().getItem(p3);
		new AlertDialog.Builder(this).setTitle("删除？").setMessage(p.title).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					PacketManager.getInstance().uninstallPacket(p.packageName);
				}
			}).setNegativeButton(android.R.string.cancel,null).show();
		return true;
	}

	@Override
	public void onPacketUpdate(Packet old, Packet new_)
	{
		ActivityTask.stopTask(this,old.packageName);
	}

	@Override
	public void onPacketRemoved(Packet packet)
	{
		ActivityTask.stopTask(this,packet.packageName);
	}

	@Override
	public void onPacketAdded(Packet packet)
	{
	}






	
}
