package com.moe.video.framework.app;
import android.app.Application;
import android.os.Looper;
import android.util.Printer;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class Application extends Application
{

	@Override
	public void onCreate()
	{
		// TODO: Implement this method
		super.onCreate();
		//初始化包管理器
		
		Looper.getMainLooper().setMessageLogging(new Printer() {
				//分发和处理消息开始前的log
				private static final String START = ">>>>> Dispatching";
				//分发和处理消息结束后的log
				private static final String END = "<<<<< Finished";

				@Override
				public void println(String x) {
					if (x.startsWith(START)) {
						//开始计时
						LogMonitor.getInstance().startMonitor();
					}
					if (x.startsWith(END)) {
						//结束计时，并计算出方法执行时间
						LogMonitor.getInstance().removeMonitor();
					}
				}
			});
	}
	public final static class LogMonitor {
		private static final String TAG = "LogMonitor";
		private static LogMonitor sInstance = new LogMonitor();
		private Handler mIoHandler;
		//方法耗时的卡口,300毫秒
		private static final long TIME_BLOCK = 100L;

		private LogMonitor() {
			HandlerThread logThread = new HandlerThread("log");
			logThread.start();
			mIoHandler = new Handler(logThread.getLooper());
		}

		private static Runnable mLogRunnable = new Runnable() {
			@Override
			public void run() {
				//打印出执行的耗时方法的栈消息
				StringBuilder sb = new StringBuilder();
				StackTraceElement[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();
				for (StackTraceElement s : stackTrace) {
					sb.append(s.toString());
					sb.append("\n");
				}
				Log.e(TAG, sb.toString());
			}
		};

		public static LogMonitor getInstance() {
			return sInstance;
		}


		/**
		 * 开始计时
		 */
		public void startMonitor() {
			mIoHandler.postDelayed(mLogRunnable, TIME_BLOCK);
		}

		/**
		 * 停止计时
		 */
		public void removeMonitor() {
			mIoHandler.removeCallbacks(mLogRunnable);
		}

	}
}
