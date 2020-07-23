package com.moe.video.framework.service;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.moe.video.framework.activity.ActivityTask;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import android.app.ActivityManager;
import com.moe.video.framework.activity.ui.ModelUI;
import com.moe.video.framework.activity.ui.ModelUI2;
import com.moe.video.framework.activity.ui.ModelUI3;
import com.moe.video.framework.activity.ui.ModelUI4;
import java.util.List;

public class AppTaskService extends Service
{
	private ActivityTask.TaskDesc[] tasks=new ActivityTask.TaskDesc[4];
	private Queue<ActivityTask.TaskDesc> queue=new ArrayBlockingQueue(4);
	@Override
	public IBinder onBind(Intent p1)
	{
		// TODO: Implement this method
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		out:
		switch(intent.getAction()){
			case ActivityTask.ADD:{
				ActivityTask.TaskDesc td=(ActivityTask.TaskDesc) intent.getSerializableExtra(Intent.EXTRA_REFERRER);
				queue.offer(td);
				tasks[td.index]=td;
				}break;
			case ActivityTask.DELETE:{
				int index=intent.getIntExtra(intent.EXTRA_INDEX,0);
				if(tasks[index]==null)break;
				queue.remove(tasks[index]);
				tasks[index]=null;
				}break;
			case ActivityTask.START:{
				String packageName=intent.getStringExtra(intent.EXTRA_PACKAGE_NAME);
				for(ActivityTask.TaskDesc td:tasks){
					if(td!=null&&td.packageName.equals(packageName)){
						startActivity(new Intent(this,getClass(td.index)).setAction(packageName).setFlags(intent.FLAG_ACTIVITY_NEW_TASK));
						
						//getSystemService(ActivityManager.class).moveTaskToFront(td.taskId,ActivityManager.MOVE_TASK_WITH_HOME);
						break out;
					}
				}
				//未找到
					for(int i=0;i<tasks.length;i++){
						if(tasks[i]==null)
						{
							startActivity(new Intent(this,getClass(i)).setAction(packageName).setFlags(intent.FLAG_ACTIVITY_NEW_TASK));
							break;
						}
					}
				
				}break;
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onTaskRemoved(Intent rootIntent)
	{ 
	switch(rootIntent.getComponent().getShortClassName()){
			case ".activity.ui.ModelUI":
				ActivityTask.deleteTask(this,0);
				break;
			case ".activity.ui.ModelUI2":
				ActivityTask.deleteTask(this,1);
				break;
			case ".activity.ui.ModelUI3":
				ActivityTask.deleteTask(this,2);
				break;
			case ".activity.ui.ModelUI4":
				ActivityTask.deleteTask(this,3);
				break;
		}
	}
	
	private Class getClass(int index){
		switch(index){
			case 0:
				return ModelUI.class;
			case 1:
				return ModelUI2.class;
			case 2:
				return ModelUI3.class;
			case 3:
				return ModelUI4.class;
		}
		return null;
	}
}
