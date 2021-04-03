package com.moe.video.framework.service;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import com.moe.video.framework.R;
import com.moe.video.framework.util.IntMap;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import android.app.PendingIntent;
import com.moe.video.framework.AudioActivity;
import android.app.NotificationChannel;
import android.graphics.drawable.Icon;
import android.support.v4.content.ContextCompat;
import android.media.session.MediaSessionManager;
import android.media.session.MediaSession;
import android.media.MediaMetadata;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.service.media.MediaBrowserService;
import android.os.Bundle;
import android.service.media.MediaBrowserService.BrowserRoot;
import android.service.media.MediaBrowserService.Result;
import java.util.List;
import android.media.browse.MediaBrowser.MediaItem;
import android.media.browse.MediaBrowser;
import org.mozilla.javascript.ast.ArrayLiteral;
import java.util.ArrayList;
import android.media.MediaDescription;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import android.media.PlaybackParams;
import android.media.session.MediaController;

public class AudioService extends MediaBrowserService implements MediaPlayer.OnCompletionListener,
MediaPlayer.OnErrorListener,
MediaPlayer.OnBufferingUpdateListener,
MediaPlayer.OnPreparedListener,
MediaPlayer.OnInfoListener,
MediaPlayer.OnTimedTextListener
{
    private MediaPlayer mMediaPlayer;
    private NotificationManager mNotificationManager;
    private Notification.Builder builder;
    private MediaSession mMediaSession;
    private PlaybackState.Builder psb;
     private MediaSession.Callback callback=new SessionCallback();
    private List<MediaMetadata> audio_list=new ArrayList<MediaMetadata>();
    private MediaMetadata media;
    private MediaController.Callback controlBack=new Callback();
    private Notification.Action actions[]=new Notification.Action[5];
    class Callback extends MediaController.Callback {

        @Override
        public void onPlaybackStateChanged(PlaybackState state) {
            switch(state.getState()){
                case state.STATE_PLAYING:
                case state.STATE_CONNECTING:{
                    builder.setActions(actions[0],actions[2],actions[3],actions[4]);
                    mNotificationManager.notify(0,builder.build());
                    }break;
                case state.STATE_PAUSED:
                case state.STATE_ERROR:
                case state.STATE_STOPPED:
                {
                    builder.setActions(actions[0],actions[1],actions[3],actions[4]);
                    mNotificationManager.notify(0,builder.build());
                }
                    break;
                
                  }
            
        }
         
     }
    @Override
    public MediaBrowserService.BrowserRoot onGetRoot(String packageName, int uid, Bundle extras) {
        //授权
        return new MediaBrowserService.BrowserRoot("audio", null);
    }


    @Override
    public void onLoadChildren(String parentId, MediaBrowserService.Result<List<MediaBrowser.MediaItem>> result) {
        //返回播放列表
        List<MediaItem> list=new ArrayList<>();
        for (MediaMetadata mmd:audio_list) {
            MediaItem mi=new MediaItem(mmd.getDescription(), MediaItem.FLAG_PLAYABLE);
            list.add(mi);
        }
        result.sendResult(list);
    }



    @Override
    public void onCreate() {
        super.onCreate();
        actions[0]=new Notification.Action.Builder(R.drawable.skip_pre,"上一个",PendingIntent.getService(this,0,new Intent(this,AudioService.class).putExtra(Intent.EXTRA_TEXT,"pre"),0)).build();
        actions[1]=new Notification.Action.Builder(R.drawable.play,"播放",PendingIntent.getService(this,1,new Intent(this,AudioService.class).putExtra(Intent.EXTRA_TEXT,"play"),0)).build();
        actions[2]=new Notification.Action.Builder(R.drawable.pause,"暂停",PendingIntent.getService(this,2,new Intent(this,AudioService.class).putExtra(Intent.EXTRA_TEXT,"pause"),0)).build();
        actions[3]=new Notification.Action.Builder(R.drawable.skip_next,"下一个",PendingIntent.getService(this,3,new Intent(this,AudioService.class).putExtra(Intent.EXTRA_TEXT,"next"),0)).build();
        actions[4]=new Notification.Action.Builder(R.drawable.close,"关闭",PendingIntent.getService(this,4,new Intent(this,AudioService.class).putExtra(Intent.EXTRA_TEXT,"close"),0)).build();
        
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel nc=mNotificationManager.getNotificationChannel("audio");
        if (nc == null)
            mNotificationManager.createNotificationChannel(nc = new NotificationChannel("audio", "音频播放通知", NotificationManager.IMPORTANCE_LOW));
        nc.setImportance(NotificationManager.IMPORTANCE_LOW);
        nc.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        nc.setShowBadge(false);
        mMediaSession = new MediaSession(this, "audio");
        mMediaSession.setCallback(callback);
        mMediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                               MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setSessionActivity(PendingIntent.getActivity(this,1,new Intent(this,AudioActivity.class),0));
        mMediaSession.setActive(true);
        mMediaSession.getController().registerCallback(controlBack);
        psb = new PlaybackState.Builder();
        psb.setActions(PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PAUSE | PlaybackState.ACTION_PLAY_FROM_MEDIA_ID | PlaybackState.ACTION_SEEK_TO | PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS);
        Notification.MediaStyle ms=new Notification.MediaStyle();
        ms.setMediaSession(mMediaSession.getSessionToken());
        setSessionToken(mMediaSession.getSessionToken());
        //ms.setShowActionsInCompactView(2,3);
        builder = new Notification.Builder(this)
        .setOnlyAlertOnce(true)
        .setOngoing(true)
        .setChannelId("audio")
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, AudioActivity.class), 0))
        .setStyle(ms)
        .setDeleteIntent(PendingIntent.getService(this,5,new Intent(this,AudioService.class).putExtra(Intent.EXTRA_TEXT,"close"),0));
        builder.setActions(actions[0],actions[1],actions[3],actions[4]);
        ms.setShowActionsInCompactView(1, 3);
        mNotificationManager.notify(0, builder.build());
        mMediaPlayer = new MediaPlayer();
        AudioAttributes aa=new AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build();
        mMediaPlayer.setAudioAttributes(aa);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnTimedTextListener(this);
        mMediaPlayer.setOnInfoListener(this);
        //mHandler.sendEmptyMessage(0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action=intent.getStringExtra(intent.EXTRA_TEXT);
        if(action!=null)
            switch(action){
                case "play":
                    callback.onPlay();
                    break;
                case "pause":
                    callback.onPause();
                    break;
                case "pre":
                    callback.onSkipToPrevious();
                    break;
                case "next":
                    callback.onSkipToNext();
                    break;
                case "close":
                    onDestroy();
                    stopSelf();
                    break;
            }
        return super.onStartCommand(intent, flags, startId);
    }

    

   
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaSession.setActive(false);
        mMediaSession.getController().unregisterCallback(controlBack);
        mMediaSession.setCallback(null);
        mNotificationManager.cancel(0);
        mMediaPlayer.release();
        mMediaSession.release();
    }


    @Override
    public void onCompletion(MediaPlayer p1) {
        psb.setState(PlaybackState.STATE_STOPPED, p1.getCurrentPosition(), 0);
        mMediaSession.setPlaybackState(psb.build());
    }

    @Override
    public boolean onError(MediaPlayer p1, int p2, int p3) {
        psb.setState(PlaybackState.STATE_ERROR, p1.getCurrentPosition(), 0);
        mMediaSession.setPlaybackState(psb.build());
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer p1) {
        p1.start();
        psb.setState(PlaybackState.STATE_PLAYING, p1.getCurrentPosition(), mMediaPlayer.getPlaybackParams().getSpeed());
        mMediaSession.setPlaybackState(psb.build());
    }

    @Override
    public void onBufferingUpdate(MediaPlayer p1, int p2) {
        psb.setState(PlaybackState.STATE_BUFFERING, p1.getCurrentPosition(), mMediaPlayer.getPlaybackParams().getSpeed());
        psb.setBufferedPosition(p2);
        mMediaSession.setPlaybackState(psb.build());
        psb.setState(p1.isPlaying()?PlaybackState.STATE_PLAYING:PlaybackState.STATE_NONE,p1.getCurrentPosition(),mMediaPlayer.getPlaybackParams().getSpeed());
        mMediaSession.setPlaybackState(psb.build());
    }

    @Override
    public void onTimedText(MediaPlayer p1, TimedText p2) {
    }

    @Override
    public boolean onInfo(MediaPlayer p1, int p2, int p3) {
        switch (p2) {
            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:

                break;
        }
        return true;
    }
    private class SessionCallback extends MediaSession.Callback {

        @Override
        public void onSetPlaybackSpeed(float speed) {
            PlaybackParams pb=new PlaybackParams();
            pb.setSpeed(speed);
            mMediaPlayer.setPlaybackParams(pb);
        }

        @Override
        public void onSeekTo(long pos) {
            mMediaPlayer.seekTo(pos, mMediaPlayer.SEEK_CLOSEST);
        }

        @Override
        public void onPlay() {
            mMediaPlayer.start();
            psb.setState(PlaybackState.STATE_PLAYING, mMediaPlayer.getCurrentPosition(), mMediaPlayer.getPlaybackParams().getSpeed());
            mMediaSession.setPlaybackState(psb.build());
        }

        @Override
        public void onPause() {
            mMediaPlayer.pause();
            psb.setState(PlaybackState.STATE_PAUSED, mMediaPlayer.getCurrentPosition(), 0);
            mMediaSession.setPlaybackState(psb.build());
        }

        @Override
        public void onSkipToPrevious() {
            int index=audio_list.indexOf(media);
            if (index == -1)return;
            index--;
            if (index < 0)return;
            psb.setState(PlaybackState.STATE_SKIPPING_TO_PREVIOUS, mMediaPlayer.getDuration(), 0);
            mMediaSession.setPlaybackState(psb.build());
            onPlayFromMediaId(audio_list.get(index).getDescription().getMediaId(), null);

        }

        @Override
        public void onSkipToNext() {
            int index=audio_list.indexOf(media);
            if (index == -1)return;
            index++;
            if (index >= audio_list.size())return;
            psb.setState(PlaybackState.STATE_SKIPPING_TO_NEXT, mMediaPlayer.getDuration(), 0);
            mMediaSession.setPlaybackState(psb.build());
            onPlayFromMediaId(audio_list.get(index).getDescription().getMediaId(), null);

        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            for (MediaMetadata mmd:audio_list) {
                if (mediaId.equals(mmd.getString(mmd.METADATA_KEY_MEDIA_ID))) {
                    AudioService.this.media = mmd;
                    builder.setContentTitle(mmd.getString(mmd.METADATA_KEY_DISPLAY_TITLE));
                    builder.setContentText(mmd.getString(mmd.METADATA_KEY_DISPLAY_SUBTITLE));
                    mNotificationManager.notify(0,builder.build());
                    mMediaPlayer.reset();
                    HashMap<String,String> headers=new HashMap<>();

                    JSONObject header=JSONObject.parseObject(mmd.getString("headers"));
                    if (header != null) {
                        Iterator<String> iter=header.keySet().iterator();
                        while (iter.hasNext()) {
                            String key=iter.next();
                            headers.put(key, header.getString(key));
                        }
                    }
                    try {
                        mMediaPlayer.setDataSource(getApplicationContext(), mmd.getDescription().getMediaUri() , headers);
                        mMediaPlayer.prepareAsync();
                        //mMediaPlayer.start();
                        psb.setState(PlaybackState.STATE_CONNECTING, mMediaPlayer.getCurrentPosition(), mMediaPlayer.getPlaybackParams().getSpeed());
                        mMediaSession.setPlaybackState(psb.build());
                        mMediaSession.setMetadata(mmd);
                    } catch (IOException e) {} catch (IllegalStateException e) {} catch (SecurityException e) {} catch (IllegalArgumentException e) {}
                }
            }
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            if ("LoadList".equals(action)) {
                JSONArray ja=JSONArray.parseArray(extras.getString(action));
                audio_list.clear();
                for (int i=0;i < ja.size();i++) {
                    JSONObject jo=ja.getJSONObject(i);
                    MediaMetadata mmd=new MediaMetadata.Builder()
                        .putString("headers", jo.getString("headers"))
                        .putText(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, jo.getString("title"))
                        .putText(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, jo.getString("artist"))
                        .putText(MediaMetadata.METADATA_KEY_ARTIST, jo.getString("artist"))
                        .putText(MediaMetadata.METADATA_KEY_TITLE, jo.getString("title"))
                        .putString(MediaMetadata.METADATA_KEY_MEDIA_URI, jo.getString("src"))
                        .putString(MediaMetadata.METADATA_KEY_ART_URI, jo.getString("icon"))
                        .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, jo.getString("id"))
                        .putLong(MediaMetadata.METADATA_KEY_DURATION, jo.getLongValue("duration"))
                        .build();
                    audio_list.add(mmd);
                }

            }else if("GetState".equals(action)){
                mMediaSession.setMetadata(media);
                mMediaSession.setPlaybackState(psb.build());
                
            }
        }


    }



}
