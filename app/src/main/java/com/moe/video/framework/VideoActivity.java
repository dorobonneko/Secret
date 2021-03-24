package com.moe.video.framework;
import android.app.Activity;
import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Rational;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import com.moe.video.framework.R;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.pm.ActivityInfo;
import android.view.WindowManager;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.util.List;
import java.util.ArrayList;
import android.app.RemoteAction;
import android.graphics.drawable.Icon;
import android.app.PendingIntent;
import android.media.session.MediaSession;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.widget.Toast;
import android.net.Uri;
import android.support.v4.view.ViewConfigurationCompat;
import android.view.ViewConfiguration;
import java.util.HashMap;
import java.util.Map;

public class VideoActivity extends Activity implements TextureView.SurfaceTextureListener,MediaPlayer.OnPreparedListener,MediaPlayer.OnInfoListener,Handler.Callback,OnTouchListener,OnClickListener,SeekBar.OnSeekBarChangeListener,MediaPlayer.OnVideoSizeChangedListener,MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener,MediaPlayer.OnBufferingUpdateListener
{

	private View control,progressView;
	private MediaPlayer mMediaPlayer;
	private JSONObject data;
	private Handler mHandler=new Handler(this);
	private List<RemoteAction> actions;
	private BroadcastReceiver reciver=new Receiver();
	private boolean canClose;
	private TextureView mTextureView;
	private float sx,sy;
	private int scrollState,preSeek;
	private TextView tips,title;
	private AudioManager audio;
	private int scaleType,index;
	private String dataSource;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		actions = new ArrayList<>();
		actions.add(new RemoteAction(Icon.createWithResource(this, R.drawable.play), "play", "play", PendingIntent.getBroadcast(this, 0, new Intent(getPackageName()).putExtra("action", "playorpause"), 0)));
		//enterPIP();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		audio = (AudioManager) getSystemService(AUDIO_SERVICE);
		setContentView(R.layout.video_view);
		getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnInfoListener(this);
		mMediaPlayer.setOnVideoSizeChangedListener(this);
		mMediaPlayer.setOnErrorListener(this);
		mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
		control = findViewById(R.id.control);
		control.findViewById(R.id.source).setOnClickListener(this);
		control.findViewById(R.id.playorpause).setOnClickListener(this);
		control.findViewById(R.id.pip).setOnClickListener(this);
		control.findViewById(R.id.close).setOnClickListener(this);
		control.findViewById(R.id.scale).setOnClickListener(this);
		progressView = control.findViewById(R.id.loadingprogress);
		SeekBar seekbar=control.findViewById(R.id.progress);
		tips = findViewById(R.id.tips);
        title=findViewById(R.id.title);
		seekbar.setOnSeekBarChangeListener(this);
		mTextureView = findViewById(R.id.textureview);
		mTextureView.setSurfaceTextureListener(this);
		findViewById(R.id.content).setOnTouchListener(this);
		hide();
		onNewIntent(getIntent());

	}
	@Override
	protected void onNewIntent(Intent intent)
	{
		try
		{
			data = new JSONObject(intent.getStringExtra("data"));
            title.setText(data.getString("title"));
			preSeek=0;
			changeSource(0);
		}
		catch (JSONException e)
		{}

	}
	private void changeSource(int index)
	{
        if(mMediaPlayer.getDuration()>0)
            preSeek=mMediaPlayer.getCurrentPosition();
        else
            preSeek=0;
        
        this.index=index;
		mMediaPlayer.stop();
		mMediaPlayer.reset();
		if (data != null)
			try
			{
				JSONArray source=data.getJSONArray("source");
				if (source.length() > index)
				{
					JSONObject item=source.getJSONObject(index);
					try
					{
						dataSource=item.getString("src");
                        Map<String,String> headers=new HashMap<>();
                        headers.put("Accept-Encoding","identity");
                        headers.put("Referer",item.has("referer")?item.getString("referer"):dataSource);
						mMediaPlayer.setDataSource(getApplicationContext(),Uri.parse(dataSource),headers);
						mMediaPlayer.prepareAsync();
						progressView.setVisibility(View.VISIBLE);
						((ImageView)control.findViewById(R.id.playorpause)).setImageResource(R.drawable.pause);
						actions.set(0, new RemoteAction(Icon.createWithResource(this, R.drawable.pause), "play", "play", PendingIntent.getBroadcast(this, 0, new Intent(getPackageName()).putExtra("action", "playorpause"), 0)));
						if (isInPictureInPictureMode())
							enterPIP();
					}
					catch (Exception e)
					{onError(mMediaPlayer,0,0);}
					
				}
			}
			catch (JSONException e)
			{}
	}

    @Override
    public void onBufferingUpdate(MediaPlayer p1, int p2) {
        ((SeekBar)control.findViewById(R.id.progress)).setSecondaryProgress(p2);
    }


	@Override
	public void onPrepared(MediaPlayer p1)
	{
		progressView.setVisibility(View.INVISIBLE);
		p1.seekTo(preSeek);
		p1.start();
	}

	@Override
	public boolean onError(MediaPlayer p1, int p2, int p3)
	{
		switch(p2){
			case p1.MEDIA_ERROR_UNKNOWN:
				preSeek=mMediaPlayer.getCurrentPosition();
				mMediaPlayer.stop();
				mMediaPlayer.reset();
				try
				{
					mMediaPlayer.setDataSource(dataSource);
					mMediaPlayer.prepareAsync();
				}
				catch (Exception e)
				{}
				break;
			case 0:
			
			case p1.MEDIA_ERROR_IO:
			case p1.MEDIA_ERROR_TIMED_OUT:
			case p1.MEDIA_ERROR_UNSUPPORTED:
				Toast.makeText(this,"Error:"+p2,Toast.LENGTH_SHORT).show();
				showSource();
				break;
			case -38:
			case -1:
				break;
		}
		return true;
	}

	@Override
	public void onCompletion(MediaPlayer p1)
	{
		pause();
        show();
	}

	private void pause(){
        if(mMediaPlayer.getDuration()>0)
            preSeek=mMediaPlayer.getCurrentPosition();
            else
              preSeek=0;
		mMediaPlayer.pause();
		((ImageView)control.findViewById(R.id.playorpause)).setImageResource(R.drawable.play);
		actions.set(0, new RemoteAction(Icon.createWithResource(this, R.drawable.play), "play", "play", PendingIntent.getBroadcast(this, 0, new Intent(getPackageName()).putExtra("action", "playorpause"), 0)));
		if (isInPictureInPictureMode())
			enterPIP();
	}

	@Override
	public boolean onInfo(MediaPlayer p1, int p2, int p3)
	{
		switch (p2)
		{
			case p1.MEDIA_INFO_BUFFERING_START:
				progressView.setVisibility(View.VISIBLE);
				break;
			case p1.MEDIA_INFO_BUFFERING_END:
				progressView.setVisibility(View.INVISIBLE);
				break;
			case p1.MEDIA_INFO_VIDEO_RENDERING_START:
				((TextView)control.findViewById(R.id.time)).setText(getTime(p1.getDuration()));
				((SeekBar)control.findViewById(R.id.progress)).setMax(mMediaPlayer.getDuration());
				break;
		}
		return true;
	}
	private boolean move,firstMove;
	private int volume,currentPosition;
	private float brightness;
	@Override
	public boolean onTouch(View p1, MotionEvent p2)
	{
		switch (p2.getAction())
		{
			case p2.ACTION_DOWN:
				move = false;
				sx = p2.getRawX();
				sy = p2.getRawY();
				firstMove = true;
				scrollState=3;
				break;
			case p2.ACTION_MOVE:
                int touchSlop=ViewConfiguration.get(getApplicationContext()).getScaledTouchSlop();
                if(Math.max(Math.abs(p2.getRawX()-sx),Math.abs(p2.getRawY()-sy))<touchSlop)break;
				move = true;
				if (firstMove)
				{
					firstMove = false;
					if (sy > getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android")))
					{

						if (Math.abs(p2.getRawX() - sx) > Math.abs(p2.getRawY() - sy))
						{
							//横向滑动
							if(mMediaPlayer.getDuration()>0){
							scrollState = 0;
							currentPosition=mMediaPlayer.getCurrentPosition();
							mHandler.removeMessages(1);
							}else{
								scrollState=3;
							}
						}
						else
						{
							if (sx < p1.getWidth() / 4)
							{
								//左边
								scrollState = 1;
								brightness = getWindow().getAttributes().screenBrightness;

								if (brightness == -1)
									brightness = 0.5f;

							}
							else if (sx > p1.getWidth() / 4 * 3)
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
						int distance=(int)((p2.getRawX()-sx)/p1.getWidth()*300000);
						int position=currentPosition+distance;
						position=Math.max(Math.min(mMediaPlayer.getDuration(),position),0);
						((TextView)control.findViewById(R.id.current_time)).setText(getTime(position));
						((SeekBar)control.findViewById(R.id.progress)).setProgress(position);
						tips.setText((distance<0?"-":"+")+getTime(distance)+"/"+getTime(position));
						break;
					case 1:{
							WindowManager.LayoutParams params=getWindow().getAttributes();
							params.screenBrightness = brightness - (p2.getRawY() - sy) / (p1.getHeight() / 4f * 3);
							params.screenBrightness = Math.min(1, Math.max(0, params.screenBrightness));
							getWindow().setAttributes(params);
							tips.setText(params.screenBrightness + "");
						}break;
					case 2:
						{
							int vol=volume - (int)((p2.getRawY() - sy) / (p1.getHeight() / 4f * 3) * audio.getStreamMaxVolume(audio.STREAM_MUSIC));
							vol = Math.min(Math.max(0, vol), audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
							audio.setStreamVolume(audio.STREAM_MUSIC, vol, 0);
							tips.setText(vol + "");
						}
						break;
				}
				break;
			case p2.ACTION_UP:
				if (!move)
				{
                    if(mHandler.hasMessages(2)){
                        mHandler.removeMessages(2);
                        mHandler.sendEmptyMessage(3);
                    }else
					mHandler.sendEmptyMessageDelayed(2,350);
				}
			case p2.ACTION_CANCEL:
				tips.setText(null);
				if(scrollState==0){
					mMediaPlayer.seekTo(((SeekBar)control.findViewById(R.id.progress)).getProgress());
					mHandler.sendEmptyMessage(1);
					}
				break;
		}
		return true;
	}
    private int checked_index;
	private void showSource(){
		try
		{
			final JSONArray sources=data.getJSONArray("source");
			String[] items=new String[sources.length()];
			for (int i=0;i < sources.length();i++)
			{
				JSONObject item=sources.getJSONObject(i);
				items[i] = item.getString("title");
			}
			new AlertDialog.Builder(this).setTitle("换源").setSingleChoiceItems(items,index ,new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
                        checked_index=p2;
						//changeSource(p2);
					}
				}).setPositiveButton("下载", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface p1, int p2) {
                        try {
                            pause();
                            startActivity(new Intent().setDataAndType(Uri.parse(sources.getJSONObject(checked_index).getString("src")),"video/*"));
                        } catch (JSONException e) {}
                    }
                }).setNegativeButton("播放", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface p1, int p2) {
                        changeSource(checked_index);
                    }
                }).show();
		}
		catch (Exception e)
		{}
	}
	@Override
	public void onClick(View p1)
	{
        show();
		switch (p1.getId())
		{
			case R.id.source:
				showSource();
				break;
			case R.id.playorpause:
				playorpause();
				//show();
				break;
			case R.id.pip:
				enterPIP();
				break;
			case R.id.close:
				finishAndRemoveTask();
				break;
			case R.id.scale:
				scaleType++;
				if(scaleType==3)
					scaleType=0;
					scale();
				break;
		}
	}


private void scale(){
	switch(scaleType){
		case 0:
			centerInside();
			break;
		case 1:
			centerCrop();
			break;
		case 2:
			center();
			break;
	}
}

	@Override
	public boolean handleMessage(Message p1)
	{
		switch (p1.what)
		{
			case 0:
				control.setVisibility(View.GONE);
				break;
			case 1:
				((TextView)control.findViewById(R.id.current_time)).setText(getTime(mMediaPlayer.getCurrentPosition()));
				((SeekBar)control.findViewById(R.id.progress)).setProgress(mMediaPlayer.getCurrentPosition());
				mHandler.sendEmptyMessageDelayed(1, 1000);
				break;
            case 2:
                if (control.getVisibility() == View.VISIBLE)
                    hide();
                else
                    show();
                break;
            case 3:
                playorpause();
                break;
                
		}
		return true;
	}

	@Override
	protected void onUserLeaveHint()
	{
		super.onUserLeaveHint();
		//离开界面
		enterPIP();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		if (!isInPictureInPictureMode())
		{
			if (mMediaPlayer.isPlaying())
				playorpause();
		}
		canClose = isInPictureInPictureMode();
	}


	@Override
	public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig)
	{
		if (isInPictureInPictureMode)
		{
			registerReceiver(reciver, new IntentFilter(getPackageName()));
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			hide();
			mTextureView.setTransform(null);
			mTextureView.postInvalidate();
		}
		else
		{
			unregisterReceiver(reciver);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

			//show();
		}
	}
	@Override
	public void onVideoSizeChanged(MediaPlayer p1, int p2, int p3)
	{
		if (!isInPictureInPictureMode())
			scale();
			//centerCrop();
			//center();
	}
	private void center()
	{
		Matrix matrix = new Matrix();

//第1步:把视频区移动到View区,使两者中心点重合.
		matrix.preTranslate((mTextureView.getWidth() - mMediaPlayer.getVideoWidth()) / 2, (mTextureView.getHeight() - mMediaPlayer.getVideoHeight()) / 2);

//第2步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
		matrix.preScale(mMediaPlayer.getVideoWidth() / (float) mTextureView.getMeasuredWidth(), mMediaPlayer.getVideoHeight() / (float)mTextureView.getMeasuredHeight());

//第3步,等比例放大或缩小,直到视频区的一边和View一边相等.如果另一边和view的一边不相等，则留下空隙
		/*float sx=mTextureView.getWidth() / (float)mMediaPlayer.getVideoWidth();
		float sy=mTextureView.getHeight() / (float)mMediaPlayer.getVideoHeight();
		if (sx >= sy)
		{
			matrix.postScale(sy, sy, mTextureView.getWidth() / 2, mTextureView.getHeight() / 2);
		}
		else
		{
			matrix.postScale(sx, sx, mTextureView.getWidth() / 2, mTextureView.getHeight() / 2);
		}
*/
		mTextureView.setTransform(matrix);
		mTextureView.postInvalidate();
	}
	private void centerCrop()
	{
		Matrix matrix = new Matrix();

//第1步:把视频区移动到View区,使两者中心点重合.
		matrix.preTranslate((mTextureView.getWidth() - mMediaPlayer.getVideoWidth()) / 2, (mTextureView.getHeight() - mMediaPlayer.getVideoHeight()) / 2);

//第2步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
		matrix.preScale(mMediaPlayer.getVideoWidth() / (float) mTextureView.getMeasuredWidth(), mMediaPlayer.getVideoHeight() / (float)mTextureView.getMeasuredHeight());

//第3步,等比例放大或缩小,直到视频区的一边和View一边相等.如果另一边和view的一边不相等，则留下空隙
		float sx=mTextureView.getWidth() / (float)mMediaPlayer.getVideoWidth();
		float sy=mTextureView.getHeight() / (float)mMediaPlayer.getVideoHeight();
		if (sx <= sy)
		{
			matrix.postScale(sy, sy, mTextureView.getWidth() / 2, mTextureView.getHeight() / 2);
		}
		else
		{
			matrix.postScale(sx, sx, mTextureView.getWidth() / 2, mTextureView.getHeight() / 2);
		}

		mTextureView.setTransform(matrix);
		mTextureView.postInvalidate();
	}
	private void centerInside()
	{
		Matrix matrix = new Matrix();

//第1步:把视频区移动到View区,使两者中心点重合.
		matrix.preTranslate((mTextureView.getWidth() - mMediaPlayer.getVideoWidth()) / 2, (mTextureView.getHeight() - mMediaPlayer.getVideoHeight()) / 2);

//第2步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
		matrix.preScale(mMediaPlayer.getVideoWidth() / (float) mTextureView.getMeasuredWidth(), mMediaPlayer.getVideoHeight() / (float)mTextureView.getMeasuredHeight());

//第3步,等比例放大或缩小,直到视频区的一边和View一边相等.如果另一边和view的一边不相等，则留下空隙
		float sx=mTextureView.getWidth() / (float)mMediaPlayer.getVideoWidth();
		float sy=mTextureView.getHeight() / (float)mMediaPlayer.getVideoHeight();
		if (sx >= sy)
		{
			matrix.postScale(sy, sy, mTextureView.getWidth() / 2, mTextureView.getHeight() / 2);
		}
		else
		{
			matrix.postScale(sx, sx, mTextureView.getWidth() / 2, mTextureView.getHeight() / 2);
		}

		mTextureView.setTransform(matrix);
		mTextureView.postInvalidate();
	}
	@Override
	protected void onStart()
	{
		super.onStart();
		mHandler.sendEmptyMessage(1);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		mHandler.removeMessages(1);
		if (canClose)
			finishAndRemoveTask();
	}

	private void show()
	{
		control.setVisibility(View.VISIBLE);
		mHandler.removeMessages(0);
		mHandler.sendEmptyMessageDelayed(0, 5000);
	}
	private void hide()
	{
		control.setVisibility(View.GONE);
	}
	@Override
	public void onBackPressed()
	{
		//super.onBackPressed();
		if(mMediaPlayer==null&&!mMediaPlayer.isPlaying())
			finishAndRemoveTask();
			else
		enterPIP();
	}

	public void enterPIP()
	{
		Rational mRational=new Rational(16, 9);
		if (mMediaPlayer != null && mMediaPlayer.getVideoWidth() > 0)
		{
			mRational = new Rational(mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
		}

		PictureInPictureParams params=new PictureInPictureParams.Builder().setAspectRatio(mRational).setActions(actions).build();
		setPictureInPictureParams(params);
		if (!isInPictureInPictureMode())
		{
			boolean f=enterPictureInPictureMode(params);
		}
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture p1, int p2, int p3)
	{
		mMediaPlayer.setSurface(new Surface(p1));
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture p1, int p2, int p3)
	{
		if (mMediaPlayer.getVideoWidth() > 0)
			scale();
			//centerCrop();
			//center();
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture p1)
	{
		try
		{mMediaPlayer.setSurface(null);}
		catch (Exception e)
		{}
		return false;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture p1)
	{
		mMediaPlayer.setSurface(new Surface(p1));
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		mMediaPlayer.stop();
		mMediaPlayer.release();
	}


	private String getTime(int time)
	{
		time = Math.abs(time);
		if (time == 0)return "00:00";
		time /= 1000;
		if (time < 60)
			return "00:".concat(getFormat(time));
		int second=time % 60;
		time /= 60;
		if (time < 60)
		{
			return getFormat(time).concat(":").concat(getFormat(second));
		}
		int minute=time % 60;
		return getFormat(time / 60).concat(":").concat(getFormat(minute)).concat(":").concat(getFormat(second));

	}
	private String getFormat(int time)
	{
		String time_=String.valueOf(time);
		if (time_.length() == 1)
			return "0" + time_;
		return time_;
	}
	private void playorpause()
	{
		if (mMediaPlayer.isPlaying())
			mMediaPlayer.pause();
		else
			mMediaPlayer.start();
		((ImageView)control.findViewById(R.id.playorpause)).setImageResource(mMediaPlayer.isPlaying() ?R.drawable.pause: R.drawable.play);
		actions.set(0, new RemoteAction(Icon.createWithResource(this, mMediaPlayer.isPlaying() ?R.drawable.pause: R.drawable.play), "play", "play", PendingIntent.getBroadcast(this, 0, new Intent(getPackageName()).putExtra("action", "playorpause"), 0)));
		if (isInPictureInPictureMode())
			enterPIP();
	}

	@Override
	public void onStartTrackingTouch(SeekBar p1)
	{
		mHandler.removeMessages(0);
		mHandler.removeMessages(1);
	}

	@Override
	public void onStopTrackingTouch(SeekBar p1)
	{
		mMediaPlayer.seekTo(p1.getProgress());
		mHandler.sendEmptyMessageDelayed(0, 3000);
		mHandler.sendEmptyMessage(1);
	}

	@Override
	public void onProgressChanged(SeekBar p1, int p2, boolean p3)
	{
		if (p3)
		{
			((TextView)control.findViewById(R.id.current_time)).setText(getTime(p2));
		}
	}






	class Receiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context p1, Intent p2)
		{
			switch (p2.getStringExtra("action"))
			{
				case "playorpause":
					playorpause();
					break;
			}
		}


	}

}
