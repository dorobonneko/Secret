package com.moe.video.framework;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toolbar;
import org.mozilla.javascript.NativeObject;
import com.moe.video.framework.util.StaticData;
import org.mozilla.javascript.ScriptRuntime;
import java.util.HashMap;
import com.moe.neko.Neko;
import android.widget.ImageView;
import android.content.Intent;
import com.moe.video.framework.service.AudioService;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.os.IBinder;
import com.moe.video.framework.service.Audio;
import com.moe.video.framework.service.AudioCallback;
import android.os.RemoteException;
import android.widget.SeekBar;
import android.app.Service;
import android.widget.TextView;
import com.moe.video.framework.util.TimeUtil;
import android.os.Handler;
import android.os.Message;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class AudioActivity extends Activity implements ServiceConnection,View.OnClickListener,Handler.Callback,SeekBar.OnSeekBarChangeListener{
    private Handler mHandler=new Handler(this);
    private HashMap obj;
    private Audio mAudio;
    private ImageView pre,next,play,icon;
    private Callback callback=new Callback();
    private TextView time,duration;
    private SeekBar mSeekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view=getWindow().getDecorView();
        view.setSystemUiVisibility(view.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.audio_view);
        pre=findViewById(R.id.pre);
        next=findViewById(R.id.next);
        play=findViewById(R.id.play);
        icon=findViewById(R.id.icon);
        mSeekBar=findViewById(R.id.progress);
        time=findViewById(R.id.time);
        duration=findViewById(R.id.duration);
        pre.setOnClickListener(this);
        next.setOnClickListener(this);
        play.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
        setActionBar((Toolbar)findViewById(R.id.toolbar));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        obj=(HashMap) getIntent().getSerializableExtra("key");
        setTitle(ScriptRuntime.toString(obj.getOrDefault("title","")));
        if(obj.containsKey("icon"))
            Neko.with(this).load(obj.get("icon").toString()).circleCrop().into((ImageView)findViewById(R.id.icon));
        startService(new Intent(this,AudioService.class));
        bindService(new Intent(this,AudioService.class),this,Service.BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View p1) {
        switch(p1.getId()){
            case R.id.pre:
                try {
                    if (mAudio != null)
                        mAudio.pre();
                } catch (RemoteException e) {}
                break;
            case R.id.next:
                try {
                    if (mAudio != null)
                        mAudio.next();
                } catch (RemoteException e) {}
                break;
            case R.id.play:
                try {
                    if (mAudio != null) {
                        if (mAudio.isPlaying())
                            mAudio.pause();
                        else
                            mAudio.play();
                    }
                } catch (RemoteException e) {}
                break;
        }
    }


    @Override
    public void onServiceDisconnected(ComponentName p1) {
    }

    @Override
    public void onServiceConnected(ComponentName p1, IBinder p2) {
        mAudio=Audio.Stub.asInterface(p2);
        try {
            if (mAudio.isPlaying()) {
                return;
            }
        } catch (RemoteException e) {}
        try {
            mAudio.setCallback(callback.asBinder());
            mAudio.loadDataSource((String)obj.get("source"));
            mAudio.select(Integer.parseInt(ScriptRuntime.toString(obj.getOrDefault("select","0"))));
        } catch (RemoteException e) {}
    }

    @Override
    public boolean handleMessage(Message p1) {
        switch(p1.what){
            case 0:
                try {
                    if(mAudio!=null){
                        mSeekBar.setProgress(mAudio.getPosition());
                    time.setText(TimeUtil.getTime(mAudio.getPosition()));
                    }
                } catch (RemoteException e) {}
                mHandler.sendEmptyMessageDelayed(0, 1000);
                break;
        }
        return true;
    }

    @Override
    public void onStartTrackingTouch(SeekBar p1) {
        mHandler.removeMessages(0);
    }

    @Override
    public void onStopTrackingTouch(SeekBar p1) {
        try {
            if (mAudio != null){
                mAudio.seetTo(p1.getProgress());
                if (mAudio.isPlaying())
                    mHandler.sendEmptyMessage(0);
                    }
        } catch (RemoteException e) {}
    }

    @Override
    public void onProgressChanged(SeekBar p1, int p2, boolean p3) {
        if(p3){
              time.setText(TimeUtil.getTime(p2));
        }
    }






    
    class Callback extends AudioCallback.Stub {

        @Override
        public void onSelected(String title, String icon) throws RemoteException {
            setTitle(title);
            if(icon!=null)
            Neko.with(this).load(icon).circleCrop().into((ImageView)findViewById(R.id.icon));
            
        }

        
        @Override
        public void onProgress(int progress) throws RemoteException {
        
            mSeekBar.setSecondaryProgress((int)(progress/100f*mSeekBar.getMax()));
        }

        @Override
        public void onPlay() throws RemoteException {
            play.setImageResource(R.drawable.pause);
            if(!mHandler.hasMessages(0))
                mHandler.sendEmptyMessage(0);
        }

        @Override
        public void onPause() throws RemoteException {
            play.setImageResource(R.drawable.play);
            mHandler.removeMessages(0);
        }

        @Override
        public void onEnd() throws RemoteException {
            play.setImageResource(R.drawable.play);
        }

        @Override
        public void onError(int type, int code) throws RemoteException {
            play.setImageResource(R.drawable.play);
        }

        @Override
        public void onServiceClose() throws RemoteException {
        }

        @Override
        public void onInfo(int position, int duration) throws RemoteException {
            AudioActivity.this.duration.setText(TimeUtil.getTime(duration));
            AudioActivity.this.time.setText(TimeUtil.getTime(position));
            mSeekBar.setMax(duration);
            mSeekBar.setProgress(position);
        }

        
    }
    
}
