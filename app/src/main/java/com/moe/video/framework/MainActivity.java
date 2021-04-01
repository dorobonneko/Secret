package com.moe.video.framework;

import android.app.*;
import android.os.*;
import android.widget.Toolbar;
import android.widget.GridView;
import java.util.List;
import com.moe.video.framework.adapter.AppAdapter;
import java.util.ArrayList;
import android.view.Menu;
import android.view.MenuItem;
import com.moe.video.framework.app.FolderDialog;
import java.io.File;
import android.Manifest;
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
import android.database.Cursor;
import com.moe.video.framework.aidl.PackageManager;
import com.moe.video.framework.content.Package;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import java.util.Collections;
import com.moe.video.framework.util.ChinaSort;
public class MainActivity extends Activity implements FolderDialog.Callback,GridView.OnItemClickListener,GridView.OnItemLongClickListener,
SharedPreferences.OnSharedPreferenceChangeListener
{
	private GridView mGridView;
	private AppAdapter mAppAdapter;
    private List<Package> list;
    private PackageManager pm;
    private PackageReciver pr=new PackageReciver();
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		setActionBar((Toolbar)findViewById(R.id.toolbar));
		mGridView=findViewById(R.id.gridview);
		Cursor cursor=getContentResolver().query(com.moe.video.framework.content.PackageManager.AUTH,null,null,null,null);
        pm=PackageManager.Stub.asInterface(cursor.getExtras().getBinder("packageManager"));
		cursor.close();
        mGridView.setAdapter(mAppAdapter=new AppAdapter(list=new ArrayList<Package>(),pm));
		mGridView.setNumColumns(4);
		mGridView.setOnItemClickListener(this);
		mGridView.setOnItemLongClickListener(this);
		getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		if(checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==android.content.pm.PackageManager.PERMISSION_DENIED)
			requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},4676);
        SharedPreferences preferences=getSharedPreferences("setting",MODE_PRIVATE);
        onSharedPreferenceChanged(preferences,"background");
        preferences.registerOnSharedPreferenceChangeListener(this);
        IntentFilter filter=new IntentFilter();
        filter.addAction(com.moe.video.framework.content.PackageManager.ACTION_ADD);
        filter.addAction(com.moe.video.framework.content.PackageManager.ACTION_UPDATE);
        filter.addAction(com.moe.video.framework.content.PackageManager.ACTION_REMOVE);
        try {
            List data=pm.queryAll();
            list.clear();
            list.addAll(data);
            Collections.sort(list, new ChinaSort());
            mAppAdapter.notifyDataSetChanged();
        } catch (RemoteException e) {}
        registerReceiver(pr,filter);
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
			if(grantResults[0]==android.content.pm.PackageManager.PERMISSION_DENIED)finish();
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
			pm.install(file.getAbsolutePath());
		}
		catch (Exception e)
		{Toast.makeText(getApplicationContext(),"文件错误",Toast.LENGTH_SHORT).show();}
		
	}

	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		Package p=(Package)p1.getAdapter().getItem(p3);
		ActivityTask.startTask(this,p.packageName);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		final Package p=(Package)p1.getAdapter().getItem(p3);
		new AlertDialog.Builder(this).setTitle("删除？").setMessage(p.title).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					try {
                        pm.unInstall(p.packageName);
                    } catch (RemoteException e) {}
				}
			}).setNegativeButton(android.R.string.cancel,null).show();
		return true;
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(pr);
    }

    class PackageReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context p1, Intent p2) {
            try {
                List data=pm.queryAll();
                list.clear();
                list.addAll(data);
                Collections.sort(list, new ChinaSort());
                mAppAdapter.notifyDataSetChanged();
            } catch (RemoteException e) {}
        }

    
}
	
}
