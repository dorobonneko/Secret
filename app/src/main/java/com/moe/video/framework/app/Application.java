package com.moe.video.framework.app;
import android.app.Application;
import com.moe.video.framework.pkg.PacketManager;

public class Application extends Application
{

	@Override
	public void onCreate()
	{
		// TODO: Implement this method
		super.onCreate();
		//初始化包管理器
		PacketManager.init(this);
	}
	
}
