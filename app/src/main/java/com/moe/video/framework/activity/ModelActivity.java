package com.moe.video.framework.activity;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.moe.neko.Neko;
import com.moe.neko.transform.BlurTransform;
import com.moe.video.framework.R;
import com.moe.video.framework.activity.fragment.AppBrandFragment;
import com.moe.video.framework.aidl.PackageManager;
import com.moe.video.framework.content.Package;
import com.moe.video.framework.util.BitmapUtil;
import java.util.List;
import android.os.RemoteException;

public abstract class ModelActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener
{
	private Package mPacket;
    private FragmentLifecycle fl=new FragmentLifecycle();
    private ViewGroup view;
    private PackageManager pm;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
        Cursor cursor=getContentResolver().query(com.moe.video.framework.content.PackageManager.AUTH,null,null,null,null);
        pm=PackageManager.Stub.asInterface(cursor.getExtras().getBinder("packageManager"));
		cursor.close();
        mPacket=getPacket();
        view=findViewById(android.R.id.content);
        SharedPreferences preferences=getSharedPreferences("setting",MODE_PRIVATE);
        preferences.registerOnSharedPreferenceChangeListener(this);
		onSharedPreferenceChanged(preferences,"background");
        getFragmentManager().registerFragmentLifecycleCallbacks(fl,false);
        registerReceiver(new BroadcastReceiver(){

				@Override
				public void onReceive(Context p1, Intent p2)
				{
					finishAndRemoveTask();
				}
			}, new IntentFilter(mPacket.packageName));
		
		Bitmap logo=mPacket.loadLogo();
		ActivityManager.TaskDescription desc=new ActivityManager.TaskDescription(mPacket.title,logo);
        setTaskDescription(desc);
		//ActivityTask.addTask(this,desc);
		if(savedInstanceState==null){
		AppBrandFragment abf=new AppBrandFragment();
		Bundle mBundle=new Bundle();
		mBundle.putString("exe",mPacket.main);
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
                        List<Fragment> list=getFragmentManager().getFragments();
                        AppBrandFragment abf=(AppBrandFragment) list.get(list.size()-1);
                        new AlertDialog.Builder(p1.getContext()).setMessage(abf.log()).show();
						//new AlertDialog.Builder(p1.getContext()).setMessage(Pussy.$(p1.getContext()).getActiveResource().size()+"\n"+Pussy.$(p1.getContext()).getActiveResource().toString()).show();
					}
				});
		}
	}

    @Override
    public void onSharedPreferenceChanged(SharedPreferences p1, String p2) {
        switch(p2){
            case "background":
                String background=p1.getString(p2,null);
                if(background==null)
                    view.setBackground(null);
                else
                    Neko.with(view).load(background).scaleType(ScaleType.CENTER_CROP).fade(100).asBitmap().transform(new BlurTransform(15)).into(view);
                
                break;
        }
    }

    

public Package getPacket(){
    try {
        if (mPacket == null)
            mPacket = pm.query(getIntent().getAction());
    } catch (RemoteException e) {}
	return mPacket;
}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
         getFragmentManager().unregisterFragmentLifecycleCallbacks(fl);
        getSharedPreferences("setting",MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(this);
	}
	public abstract int getIndex();

	@Override
	public void onBackPressed()
	{
		
		if(getFragmentManager().getBackStackEntryCount()<=1)
			finish();
            else
                getFragmentManager().popBackStack();
	}
	class FragmentLifecycle extends FragmentManager.FragmentLifecycleCallbacks {

        @Override
        public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
            int count=view.getChildCount();
            for(int i=0;i<count;i++){
                view.getChildAt(i).setVisibility(View.GONE);
            }
        }

        @Override
        public void onFragmentDetached(FragmentManager fm, Fragment f) {
            if(view.getChildCount()>0)
            view.getChildAt(view.getChildCount()-1).setVisibility(View.VISIBLE);
        }
        
    }
}
