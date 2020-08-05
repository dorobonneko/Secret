package com.moe.video.framework.activity;
import android.app.ActivityManager;
import android.content.Context;
import java.util.Map;
import java.util.LinkedHashMap;
import android.app.Activity;
import com.moe.video.framework.service.AppTaskService;
import android.content.Intent;
import java.io.Serializable;

public class ActivityTask
{
	public static final String START="moe.model.Start",STOP="moe.model.Stop",ADD="moe.model.Add",DELETE="moe.model.Delete";
	public static void addTask(Context context,TaskDesc td){
		context.startService(new Intent(context,AppTaskService.class).setAction(ADD).putExtra(Intent.EXTRA_REFERRER,td));
		
	}
	public static void deleteTask(Context context,int index){
		context.startService(new Intent(context,AppTaskService.class).setAction(DELETE).putExtra(Intent.EXTRA_INDEX,index));
		
	}
	public static void startTask(Context context,String packageName){
		context.startService(new Intent(context,AppTaskService.class).setAction(START).putExtra(Intent.EXTRA_PACKAGE_NAME,packageName));
	}
	public static void stopTask(Context context,String packageName){
		context.startService(new Intent(context,AppTaskService.class).setAction(STOP).putExtra(Intent.EXTRA_PACKAGE_NAME,packageName));
	}
	public static class TaskDesc implements Serializable{
		public int taskId;
		public int index;
		public String packageName;
	}
}
