package com.moe.video.framework.widget;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.DataSourceDesc;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import java.util.concurrent.Executors;
import android.media.TimedMetaData;
import android.util.Size;
import android.graphics.Matrix;
import android.view.ViewConfiguration;
import android.provider.Settings;
import android.view.WindowManager;
import android.app.Activity;
import android.graphics.Canvas;
import java.io.IOException;
import android.media.MediaPlayer;
import android.net.Uri;

public class MediaView extends TextureView{
    private MediaPlayer mMediaPlayer2;
    private GestureDetector mGestureDetector;
    private TextureListener mTextureListener=new TextureListener();;
    public static final int SCALE_FULL=1;
    public static final int SCALE_SOURCE=2;
    public static final int SCALE_CUT=3;
    public static final int SCALE_AUTO=4;
    private int scale_state=SCALE_AUTO;
    private boolean playWhenPrepared;
    private Event mEvent=new Event();
    private AudioManager audio;
    public MediaView(Context context){
        this(context,null);
    }
    public MediaView(Context context,AttributeSet attrs){
        super(context,attrs);
        setOpaque(false);
        setLayerType(LAYER_TYPE_HARDWARE,null);
        audio=(AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector=new GestureDetector(context,listener);
        mGestureDetector.setOnDoubleTapListener(doubleListener);
        mMediaPlayer2=new MediaPlayer();
        mMediaPlayer2.setScreenOnWhilePlaying(true);
        mMediaPlayer2.setOnBufferingUpdateListener(mEvent);
        mMediaPlayer2.setOnCompletionListener(mEvent);
        mMediaPlayer2.setOnErrorListener(mEvent);
        mMediaPlayer2.setOnInfoListener(mEvent);
        mMediaPlayer2.setOnPreparedListener(mEvent);
        mMediaPlayer2.setOnSeekCompleteListener(mEvent);
        mMediaPlayer2.setOnVideoSizeChangedListener(mEvent);
        setSurfaceTextureListener(mTextureListener);
        mMediaPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //mMediaPlayer2.setSurface(new Surface(getSurfaceTexture()));
    }
    
    private void scale(){
        switch(scale_state){
            case SCALE_FULL:
                //拉伸
                centerFull();
                break;
            case SCALE_SOURCE:
                //原始
                center();
                break;
            case SCALE_CUT:
                //裁剪
                centerCrop();
                break;
            case SCALE_AUTO:
                //自适应
                centerInside();
                break;
        }
    }
    public void playWhenPrepared(boolean playWhenPrepared){
        this.playWhenPrepared=playWhenPrepared;
    }
    public void setScale(int scaleState){
        this.scale_state=scaleState;
        if(mMediaPlayer2.getDuration()>0){
            scale();
        }
        //改变视频尺寸
    }
    public void loadUrl(String url) throws IOException, IllegalStateException, SecurityException, IllegalArgumentException{
        mMediaPlayer2.setDataSource(getContext(),Uri.parse(url));
    }
    public void prepare(){
            mMediaPlayer2.prepareAsync();
    }
    public void play(){
        if(mMediaPlayer2.getDuration()>0)
        mMediaPlayer2.start();
        else
          playWhenPrepared(true);
    }
    public void pause(){
        playWhenPrepared(false);
        mMediaPlayer2.pause();
    }
    public void stop(){
        playWhenPrepared(false);
        mMediaPlayer2.stop();
    }
    public void release(){
        mMediaPlayer2.release();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
    
    private OnDoubleTapListener doubleListener=new OnDoubleTapListener(){

        @Override
        public boolean onSingleTapConfirmed(MotionEvent p1) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent p1) {
            mEvent.onDoubleClick();
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent p1) {
            return false;
        }
    };
    private GestureDetector.OnGestureListener listener=new OnGestureListener(){
        private boolean move,firstMove;
        private int volume;
        private long currentPosition;
        private float brightness;
        private float sx,sy;
        private int scrollState;
        @Override
        public boolean onDown(MotionEvent p1) {
            move = false;
            sx = p1.getRawX();
            sy = p1.getRawY();
            firstMove = true;
            scrollState=3;
            return true;
        }

        @Override
        public void onShowPress(MotionEvent p1) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent p1) {
            mEvent.onClick();
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent p1, MotionEvent p2, float p3, float p4) {
            int touchSlop=ViewConfiguration.get(getContext()).getScaledTouchSlop();
            if(Math.max(Math.abs(p2.getRawX()-sx),Math.abs(p2.getRawY()-sy))<touchSlop)return true;
            move = true;
            if (firstMove)
            {
                firstMove = false;
                if (sy > getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android")))
                {

                    if (Math.abs(p2.getRawX() - sx) > Math.abs(p2.getRawY() - sy))
                    {
                        //横向滑动
                        if(mMediaPlayer2.getDuration()>0){
                            scrollState = 0;
                            currentPosition=mMediaPlayer2.getCurrentPosition();
                            //mHandler.removeMessages(1);//通知停止刷新时间
                        }else{
                            scrollState=3;
                        }
                    }
                    else
                    {
                        if (sx < MediaView.this.getWidth() / 4)
                        {
                            //左边
                            scrollState = 1;
                            try {
                                brightness = Settings.System.getFloat(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS) / 255f;
                            } catch (Settings.SettingNotFoundException e) {
                                e.printStackTrace();
                            }

                            if (brightness == -1)
                                brightness = 0.5f;

                        }
                        else if (sx > MediaView.this.getWidth() / 4 * 3)
                        {
                            //右边
                            scrollState = 2;
                            volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                        }
                        else
                        {
                            scrollState = 3;
                        }
                    }
                }
                else
                {
                    scrollState = 3;
                }

            }
            switch (scrollState)
            {
                case 0:
                    int distance=(int)((p2.getRawX()-sx)/MediaView.this.getWidth()*300000);
                    long position=currentPosition+distance;
                    position=Math.max(Math.min(mMediaPlayer2.getDuration(),position),0);
                    mEvent.onSeeking(position,mMediaPlayer2.getDuration());
                    break;
                case 1:{
                        WindowManager.LayoutParams params=((Activity)getContext()).getWindow().getAttributes();
                        params.screenBrightness = brightness - (p2.getRawY() - sy) / (MediaView.this.getHeight() / 4f * 3);
                        params.screenBrightness = Math.min(1, Math.max(0, params.screenBrightness));
                        ((Activity)getContext()).getWindow().setAttributes(params);
                        mEvent.onScreenBrightness(brightness);
                    }break;
                case 2:
                    {
                        int vol=volume - (int)((p2.getRawY() - sy) / (MediaView.this.getHeight() / 4f * 3) * audio.getStreamMaxVolume(audio.STREAM_MUSIC));
                        int max=audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                        vol = Math.min(Math.max(0, vol), max);
                        audio.setStreamVolume(audio.STREAM_MUSIC, vol, 0);
                        mEvent.onVolume(vol,max);
                    }
                    break;
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent p1) {
        }

        @Override
        public boolean onFling(MotionEvent p1, MotionEvent p2, float p3, float p4) {
            return false;
        }
    };
    class TextureListener implements SurfaceTextureListener {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture p1, int p2, int p3) {
            mMediaPlayer2.setSurface(new Surface(p1));
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture p1, int p2, int p3) {
            scale();
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture p1) {
            try{mMediaPlayer2.setSurface(null);}catch(Exception e){}
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture p1) {
            mMediaPlayer2.setSurface(new Surface(p1));
        }
    };
    public class Event implements MediaPlayer.OnBufferingUpdateListener,
    MediaPlayer.OnCompletionListener,
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnInfoListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnSeekCompleteListener,
    MediaPlayer.OnVideoSizeChangedListener {

        @Override
        public void onBufferingUpdate(MediaPlayer p1, int p2) {
        }

        @Override
        public void onCompletion(MediaPlayer p1) {
        }

        @Override
        public boolean onError(MediaPlayer p1, int p2, int p3) {
            return false;
        }

        @Override
        public boolean onInfo(MediaPlayer p1, int p2, int p3) {
            return false;
        }

      
        @Override
        public void onSeekComplete(MediaPlayer p1) {
        }


        @Override
        public void onPrepared(MediaPlayer p1) {
            scale();
            if(playWhenPrepared)
                p1.start();
        }
        public void onCalled(int what){
            
        }
       
        @Override
        public void onVideoSizeChanged(MediaPlayer p1, int p2, int p3) {
            scale();
        }
        public void onClick(){
            if(mMediaPlayer2.isPlaying())
                pause();
                else
                play();
        }
        public void onDoubleClick(){
            
        }
        public void onSeeking(long seekTo,long duration){
            
        }
        public void onScreenBrightness(float brightness){
            
        }
        public void onVolume(int volume,int maxVolume){
            
        }
    }
    private void center()
    {
        Matrix matrix = new Matrix();
        matrix.preTranslate((this.getMeasuredWidth() - mMediaPlayer2.getVideoWidth()) / 2, (this.getMeasuredHeight()- mMediaPlayer2.getVideoHeight()) / 2);
        matrix.postScale(mMediaPlayer2.getVideoWidth() / (float) this.getMeasuredWidth(), mMediaPlayer2.getVideoHeight() / (float)this.getMeasuredHeight());
        this.setTransform(matrix);
        this.postInvalidate();
    }
    private void centerCrop()
    {
        Matrix matrix = new Matrix();

//第1步:把视频区移动到View区,使两者中心点重合.
        matrix.preTranslate((this.getWidth() - mMediaPlayer2.getVideoWidth()) / 2, (this.getHeight() - mMediaPlayer2.getVideoHeight()) / 2);

//第2步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
        matrix.preScale(mMediaPlayer2.getVideoWidth() / (float) this.getMeasuredWidth(), mMediaPlayer2.getVideoHeight() / (float)this.getMeasuredHeight());

//第3步,等比例放大或缩小,直到视频区的一边和View一边相等.如果另一边和view的一边不相等，则留下空隙
        float sx=this.getWidth() / (float)mMediaPlayer2.getVideoWidth();
        float sy=this.getHeight() / (float)mMediaPlayer2.getVideoHeight();
        if (sx <= sy)
        {
            matrix.postScale(sy, sy, this.getWidth() / 2, this.getHeight() / 2);
        }
        else
        {
            matrix.postScale(sx, sx, this.getWidth() / 2, this.getHeight() / 2);
        }

        this.setTransform(matrix);
        this.postInvalidate();
    }
    private void centerInside()
    {
        Matrix matrix = new Matrix();
        matrix.preTranslate((this.getWidth() - mMediaPlayer2.getVideoWidth()) / 2, (this.getHeight() - mMediaPlayer2.getVideoHeight()) / 2);
        matrix.preScale(mMediaPlayer2.getVideoWidth() / (float) this.getMeasuredWidth(), mMediaPlayer2.getVideoHeight() / (float)this.getMeasuredHeight());
        float sx=this.getWidth() / (float)mMediaPlayer2.getVideoWidth();
        float sy=this.getHeight() / (float)mMediaPlayer2.getVideoHeight();
        if (sx >= sy)
        {
            matrix.postScale(sy, sy, this.getWidth() / 2, this.getHeight() / 2);
        }
        else
        {
            matrix.postScale(sx, sx, this.getWidth() / 2, this.getHeight() / 2);
        }

        this.setTransform(matrix);
        this.postInvalidate();
	}
    private void centerFull(){
        Matrix matrix = new Matrix();
        matrix.preTranslate((this.getWidth() - mMediaPlayer2.getVideoWidth()) / 2, (this.getHeight() - mMediaPlayer2.getVideoHeight()) / 2);
        matrix.postScale(this.getMeasuredWidth()/(float)mMediaPlayer2.getVideoWidth(),this.getMeasuredHeight()/(float)mMediaPlayer2.getVideoHeight());
        this.setTransform(matrix);
        this.postInvalidate();
    }
}
