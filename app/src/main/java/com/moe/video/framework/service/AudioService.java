package com.moe.video.framework.service;
import android.app.Service;
import android.os.IBinder;
import android.content.Intent;
import android.os.RemoteException;
import android.media.MediaPlayer;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.TimedText;

public class AudioService extends Service implements MediaPlayer.OnCompletionListener,
MediaPlayer.OnErrorListener,
MediaPlayer.OnBufferingUpdateListener,
MediaPlayer.OnPreparedListener,
MediaPlayer.OnTimedTextListener{
    private Audio mAudio;
    private AudioCallback mAudioCallback;
    private MediaPlayer mMediaPlayer;
    private boolean isPlay;
    @Override
    public IBinder onBind(Intent p1) {
        if(mAudio==null)
            mAudio=new AudioImpl();
        return mAudio.asBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer=new MediaPlayer();
        AudioAttributes aa=new AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build();
       mMediaPlayer.setAudioAttributes(aa);
       mMediaPlayer.setOnCompletionListener(this);
       mMediaPlayer.setOnErrorListener(this);
       mMediaPlayer.setOnBufferingUpdateListener(this);
       mMediaPlayer.setOnPreparedListener(this);
       mMediaPlayer.setOnTimedTextListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer p1) {
        try {
            if (mAudioCallback != null)
                mAudioCallback.onEnd();
        } catch (RemoteException e) {}
    }

    @Override
    public boolean onError(MediaPlayer p1, int p2, int p3) {
        try {
            if (mAudioCallback != null)
                mAudioCallback.onError(p2, p3);
        } catch (RemoteException e) {}
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer p1) {
        if(isPlay){
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





    
    class AudioImpl extends Audio.Stub {

        @Override
        public void loadDataSource(String json) throws RemoteException {
        }

        @Override
        public void reset() throws RemoteException {
        }

        @Override
        public void play() throws RemoteException {
        }

        @Override
        public void pause() throws RemoteException {
        }

        @Override
        public void setCallback(IBinder callback) throws RemoteException {
            mAudioCallback=AudioCallback.Stub.asInterface(callback);
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return false;
        }

        @Override
        public void loop() throws RemoteException {
        }

        @Override
        public boolean isLoop() throws RemoteException {
            return false;
        }
    }
    
    
}
