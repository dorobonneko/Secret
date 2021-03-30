package com.moe.video.framework.activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcel;
import com.moe.video.framework.activity.ui.ModelUI;
import com.moe.video.framework.activity.ui.ModelUI2;
import com.moe.video.framework.activity.ui.ModelUI3;
import com.moe.video.framework.activity.ui.ModelUI4;
import android.widget.Toast;

public class ActivityTask
{
	
	public static void startTask(Context context,String packageName){
        ActivityManager am=context.getSystemService(ActivityManager.class);
        int[] indexs=new int[4];//最大4个活动
        for(ActivityManager.RunningTaskInfo task:am.getRunningTasks(100)){
            int index=getIndex(task.baseActivity.getShortClassName());
            if(index!=-1){
                indexs[index]=1;
            if(packageName.equals(task.baseIntent.getAction())){
                am.moveTaskToFront(task.taskId,ActivityManager.MOVE_TASK_NO_USER_ACTION);
                return;
                }
            }
        }
        for(int i=0;i<indexs.length;i++){
            if(indexs[i]==0){
                //未使用
                context.startActivity(new Intent(context,getClass(i)).setAction(packageName).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                return;
            }
        }
        Toast.makeText(context,"任务达到上限",Toast.LENGTH_LONG).show();
		//context.startService(new Intent(context,AppTaskService.class).setAction(START).putExtra(Intent.EXTRA_PACKAGE_NAME,packageName));
	}
	public static void stopTask(Context context,String packageName){
		//context.startService(new Intent(context,AppTaskService.class).setAction(STOP).putExtra(Intent.EXTRA_PACKAGE_NAME,packageName));
        context.sendBroadcast(new Intent(packageName));
	}
    private static int getIndex(String name){
        switch(name){
            case ".activity.ui.ModelUI":
                return 0;
            case ".activity.ui.ModelUI2":
                return 1;
            case ".activity.ui.ModelUI3":
                return 2;
            case ".activity.ui.ModelUI4":
                return 3;
        }
        return -1;
    }
    private static Class getClass(int index){
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
