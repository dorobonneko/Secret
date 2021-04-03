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
import android.view.MenuItem;
import android.media.browse.MediaBrowser;
import java.util.List;
import android.media.browse.MediaBrowser.MediaItem;
import android.media.session.MediaController;
import android.media.MediaMetadata;
import android.media.session.PlaybackState;
import android.media.MediaDescription;
import android.os.SystemClock;

public class AudioActivity extends Activity implements View.OnClickListener,
SeekBar.OnSeekBarChangeListener,
Handler.Callback
{
    private Handler mHandler=new Handler(this);
    private HashMap obj;
    private ImageView pre,next,play,icon;
    private TextView time,duration;
    private SeekBar mSeekBar;
    private boolean touching;
    private MediaBrowser.ConnectionCallback connectCallback=new Connection();
    private MediaBrowser mMediaBrowser;
    private MediaBrowser.SubscriptionCallback subscript=new Subscribe();
    private MediaController mMediaController;
    private MediaController.Callback controlBack=new ControlCallback();
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
        if(obj!=null){
        setTitle(ScriptRuntime.toString(obj.getOrDefault("title","")));
        if(obj.containsKey("icon"))
            Neko.with(this).load(obj.get("icon").toString()).circleCrop().into((ImageView)findViewById(R.id.icon));
            }
     startService(new Intent(this,AudioService.class));
        mMediaBrowser=new MediaBrowser(this,new ComponentName(this,AudioService.class),connectCallback,null);
        if(!mMediaBrowser.isConnected())
            mMediaBrowser.connect();
        }


    @Override
    public void onClick(View p1) {
        switch(p1.getId()){
            case R.id.pre:
                if(mMediaController!=null)
                    mMediaController.getTransportControls().skipToPrevious();
                break;
            case R.id.next:
                if(mMediaController!=null)
                    mMediaController.getTransportControls().skipToNext();
                break;
            case R.id.play:
                if(mMediaController!=null){
                    PlaybackState ps=mMediaController.getPlaybackState();
                    if(ps!=null&&ps.getState()==PlaybackState.STATE_PLAYING)
                        mMediaController.getTransportControls().pause();
                        else
                        mMediaController.getTransportControls().play();
                    }
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar p1) {
        touching=true;
        mHandler.removeMessages(0);
    }

    @Override
    public void onStopTrackingTouch(SeekBar p1) {
        if(mMediaController!=null)
            mMediaController.getTransportControls().seekTo(p1.getProgress());
            touching=false;
            mHandler.sendEmptyMessage(0);
    }

    @Override
    public void onProgressChanged(SeekBar p1, int p2, boolean p3) {
        if(p3){
              time.setText(TimeUtil.getTime(p2));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                moveTaskToBack(true);
                break;
        }
        return true;
    }


    class Connection extends MediaBrowser.ConnectionCallback {

        @Override
        public void onConnectionSuspended() {
            super.onConnectionSuspended();
            mMediaBrowser.unsubscribe("id");
        }

        @Override
        public void onConnectionFailed() {
            super.onConnectionFailed();
        }

        @Override
        public void onConnected() {
            super.onConnected();
            //导入播放数据
            mMediaController=new MediaController(getApplicationContext(),mMediaBrowser.getSessionToken());
            mMediaController.registerCallback(controlBack);
           
            mMediaBrowser.subscribe("id",subscript);
            
        }
}

    class Subscribe extends MediaBrowser.SubscriptionCallback {

        @Override
        public void onChildrenLoaded(String parentId, List<MediaBrowser.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);
            //得到播放列表
            if(children.isEmpty()){
                //无数据
                if(obj==null)return;
                Bundle bundle=new Bundle();
                bundle.putString("LoadList",(String)obj.get("source"));
                mMediaController.getTransportControls().sendCustomAction("LoadList",bundle);
                mMediaController.getTransportControls().playFromMediaId(obj.get("select").toString(),null);
            }else{
                mMediaController.getTransportControls().sendCustomAction("GetState",null);
            }
        }
    
}

    class ControlCallback extends MediaController.Callback {

       
        @Override
        public void onMetadataChanged(MediaMetadata metadata) {
            super.onMetadataChanged(metadata);
            //音频信息改变
            MediaDescription md=metadata.getDescription();
            setTitle(md.getTitle());
            long duration=metadata.getLong(metadata.METADATA_KEY_DURATION);
            AudioActivity.this.duration.setText(TimeUtil.getTime(duration));
            mSeekBar.setMax((int)duration);
            Neko.with(icon).load(md.getIconUri().toString()).circleCrop().into(icon);
        }

        @Override
        public void onPlaybackStateChanged(PlaybackState state) {
            super.onPlaybackStateChanged(state);
            //播放状态改变
            switch(state.getState()){
                 case state.STATE_PLAYING:
                     if(!mHandler.hasMessages(0))
                        mHandler.sendEmptyMessage(0);
                case state.STATE_CONNECTING:
                    play.setImageResource(R.drawable.pause);
                    break;
                case state.STATE_ERROR:
                      mMediaController.getTransportControls().skipToNext();
                    
                case state.STATE_PAUSED:
                case state.STATE_STOPPED:
                    play.setImageResource(R.drawable.play);
                    mHandler.removeMessages(0);
                    break;
                case state.STATE_BUFFERING:
                    mSeekBar.setSecondaryProgress((int)(state.getBufferedPosition()/100f*mSeekBar.getMax()));
                    break;
            }
            if(!touching){
                long pos=(long)(state.getPosition()+(SystemClock.elapsedRealtime()-state.getLastPositionUpdateTime())*state.getPlaybackSpeed());
                mSeekBar.setProgress((int)pos);
                time.setText(TimeUtil.getTime(pos));
            }
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            finishAndRemoveTask();
        }
        
        
        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaBrowser.disconnect();
    }

    @Override
    public boolean handleMessage(Message p1) {
        switch(p1.what){
            case 0:
                if(mMediaController!=null){
                    PlaybackState ps=mMediaController.getPlaybackState();
                    if(ps!=null){
                long pos=(long)(ps.getPosition()+(SystemClock.elapsedRealtime()-ps.getLastPositionUpdateTime())*ps.getPlaybackSpeed());
                mSeekBar.setProgress((int)pos);
                time.setText(TimeUtil.getTime(pos));
                }
                }
                mHandler.sendEmptyMessageDelayed(0,1000);
                break;
        }
        return true;
    }

    
}
