package com.moe.video.framework.service;
import android.app.Service;
import android.os.IBinder;
import android.content.Intent;
import android.os.RemoteException;
import android.media.MediaPlayer;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.TimedText;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.net.Uri;
import java.io.IOException;
import java.util.LinkedHashMap;
import com.moe.video.framework.util.IntMap;
import java.util.Iterator;

public class AudioService extends Service implements MediaPlayer.OnCompletionListener,
MediaPlayer.OnErrorListener,
MediaPlayer.OnBufferingUpdateListener,
MediaPlayer.OnPreparedListener,
MediaPlayer.OnInfoListener,
MediaPlayer.OnTimedTextListener {
    private Audio mAudio;
    private AudioCallback mAudioCallback;
    private MediaPlayer mMediaPlayer;
    private boolean isPlay,preparing;
    private IntMap<JSONObject> data=new IntMap<>();;
    private int index;
    @Override
    public IBinder onBind(Intent p1) {
        if (mAudio == null)
            mAudio = new AudioImpl();
        return mAudio.asBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        AudioAttributes aa=new AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build();
        mMediaPlayer.setAudioAttributes(aa);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnTimedTextListener(this);
        mMediaPlayer.setOnInfoListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer p1) {
        isPlay = false;
        try {
            if (mAudioCallback != null) {
                mAudioCallback.onPause();
                mAudioCallback.onEnd();
            }
        } catch (RemoteException e) {}
        try {
            mAudio.next();
        } catch (RemoteException e) {}
    }

    @Override
    public boolean onError(MediaPlayer p1, int p2, int p3) {
        isPlay = false;
        try {
            if (mAudioCallback != null) {
                mAudioCallback.onPause();
                mAudioCallback.onEnd();
                mAudioCallback.onError(p2, p3);
            }
        } catch (RemoteException e) {}
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer p1) {
        preparing = false;
        try {
            if (mAudioCallback != null)
                mAudioCallback.onInfo(mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration());
        } catch (RemoteException e) {}
        if (isPlay) {
            p1.start();
            try {
                if (mAudioCallback != null)
                    mAudioCallback.onPlay();
            } catch (RemoteException e) {}
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer p1, int p2) {
        try {
            if (mAudioCallback != null)
                mAudioCallback.onProgress(p2);
        } catch (RemoteException e) {}
    }

    @Override
    public void onTimedText(MediaPlayer p1, TimedText p2) {
    }

    @Override
    public boolean onInfo(MediaPlayer p1, int p2, int p3) {
        switch(p2){
            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                
                break;
        }
        return true;
    }


    public class AudioImpl extends Audio.Stub {

        @Override
        public void next() throws RemoteException {
            index++;
            if (index == data.size())
                return;//播放完毕
            try {
                mAudio.select(data.getKey(index));
            } catch (RemoteException e) {

            }
        }

        @Override
        public void pre() throws RemoteException {
            index--;
            if (index < 0)
                return;//播放完毕
            try {
                mAudio.select(data.getKey(index));
            } catch (RemoteException e) {

            }
        }



        @Override
        public void select(int id) throws RemoteException {
            reset();
            index = data.indexOfKey(id);
            JSONObject obj=data.get(id);
            try {
                HashMap<String,String> headers=new HashMap<>();
                if (obj.has("headers")) {
                    JSONObject header=obj.getJSONObject("headers");
                    Iterator<String> i=header.keys();
                    while (i.hasNext()) {
                        String key=i.next();
                        headers.put(key, header.getString(key));
                    }
                }
                mMediaPlayer.setDataSource(getApplicationContext(), Uri.parse(obj.getString("src")), headers);
                mMediaPlayer.prepareAsync();
                preparing = true;
                try {
                    if (mAudioCallback != null)
                        mAudioCallback.onSelected(obj.getString("title"), obj.getString("icon"));
                } catch (JSONException e) {} catch (RemoteException e) {}
                play();
            } catch (IOException e) {} catch (JSONException e) {} catch (IllegalStateException e) {} catch (SecurityException e) {} catch (IllegalArgumentException e) {}
        }
        @Override
        public void loadDataSource(String json) throws RemoteException {
            try {
                JSONArray arr=new JSONArray(json);
                for (int i=0;i < arr.length();i++) {
                    JSONObject obj=arr.getJSONObject(i);
                    data.put(obj.getInt("id"), obj);
                }
            } catch (JSONException e) {

            }

        }

        @Override
        public void reset() throws RemoteException {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            isPlay = false;
        }

        @Override
        public void play() throws RemoteException {
            isPlay = true;
            if (!preparing)
                mMediaPlayer.start();
            if (mAudioCallback != null)
                mAudioCallback.onPlay();
        }

        @Override
        public void pause() throws RemoteException {
            isPlay = false;
            if (!preparing)
                mMediaPlayer.pause();
            if (mAudioCallback != null)
                mAudioCallback.onPause();
        }

        @Override
        public void setCallback(IBinder callback) throws RemoteException {
            mAudioCallback = AudioCallback.Stub.asInterface(callback);
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return isPlay||mMediaPlayer.isPlaying();
        }

        @Override
        public void loop(boolean loop) throws RemoteException {
            mMediaPlayer.setLooping(loop);
        }

        @Override
        public boolean isLoop() throws RemoteException {
            return mMediaPlayer.isLooping();
        }

        @Override
        public int getPosition() throws RemoteException {
            return mMediaPlayer.getCurrentPosition();
        }

        @Override
        public void seetTo(long time) throws RemoteException {
            mMediaPlayer.seekTo(time,mMediaPlayer.SEEK_CLOSEST);
        }

        @Override
        public int getSelect() throws RemoteException {
            return data.getKey(index);
        }

        @Override
        public String[] getSelectInfo() throws RemoteException {
            JSONObject jo=data.get(getSelect());
            try {
                return new String[]{jo.getString("title"),jo.getString("icon")};
            } catch (JSONException e) {}
            return null;
        }



        

        
    }


}
