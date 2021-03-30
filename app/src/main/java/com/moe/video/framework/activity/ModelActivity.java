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
import android.app.Fragment;
import java.util.List;
import android.content.SharedPreferences;
import android.widget.ImageView.ScaleType;
import android.app.FragmentManager;
import com.moe.neko.transform.BlurTransform;

public abstract class ModelActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener
{
	private Packet mPacket;
    private FragmentLifecycle fl=new FragmentLifecycle();
    private ViewGroup view;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
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
		
		Bitmap logo=BitmapUtil.DrawableToBitmap(PacketManager.getInstance().loadLogo(mPacket));
		ActivityManager.TaskDescription desc=new ActivityManager.TaskDescription(mPacket.title,logo);
        setTaskDescription(desc);
		//ActivityTask.addTask(this,desc);
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

    

public Packet getPacket(){
    if(mPacket!=null&&mPacket.isClose())
        mPacket=null;
	if(mPacket==null)
		mPacket=PacketManager.getInstance().getPacket(getIntent().getAction());
	return mPacket;
}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
        mPacket.close();
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
