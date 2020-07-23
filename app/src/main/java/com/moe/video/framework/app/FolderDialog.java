package com.moe.video.framework.app;
import android.app.Activity;
import android.app.AlertDialog;
import android.widget.ListView;
import android.content.DialogInterface;
import java.util.List;
import java.io.File;
import java.util.ArrayList;
import android.os.Environment;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.view.View;
import android.widget.TextView;
import java.io.FileFilter;
import java.util.Collections;
import java.util.Arrays;
import java.util.Comparator;
import android.text.TextUtils;
import android.widget.Toast;
import android.os.Build;
import java.text.Collator;
import java.util.Locale;
import com.moe.video.framework.R;

public class FolderDialog implements DialogInterface.OnClickListener,ListView.OnItemClickListener,FileFilter,Comparator<File>
{


	private AlertDialog dialog,create;
	private ListView mListView;
	private List<File> list;
	private FolderAdapter mFolderAdapter;
	private File root,current;
	private Activity activity;
	private Callback call;
	private Collator mCollator;
	public FolderDialog(Activity activity)
	{
		this.activity = activity;
		mCollator = Collator.getInstance(Locale.CHINA);
		root = Environment.getExternalStorageDirectory();
		//mListView=(ListView) activity.getLayoutInflater().inflate(R.layout.listview,null);
		mListView = new ListView(activity);
		mListView.setOnItemClickListener(this);
		mListView.setAdapter(mFolderAdapter = new FolderAdapter(list = new ArrayList<>()));
		View v=activity.getLayoutInflater().inflate(R.layout.folder_item, null);
		((TextView)v.findViewById(R.id.title)).setText("..");
		mListView.addHeaderView(v);
		dialog = new AlertDialog.Builder(activity)
			.setTitle("选择文件")
			.setView(mListView)
			//.setPositiveButton(android.R.string.ok,this)
			.setNegativeButton(android.R.string.cancel, null)
			//.setNeutralButton(R.string.create_folder,this)
			.create();
	}
	public void show()
	{
		open(root);
		dialog.show();
	}
	public void dismiss()
	{
		dialog.dismiss();
	}
	@Override
	public void onClick(DialogInterface p1, int p2)
	{
		switch (p2)
		{
			case AlertDialog.BUTTON_POSITIVE:
				if (call != null)
					call.onPath(current);
				break;
			case AlertDialog.BUTTON_NEUTRAL:
				/*if(create==null)
				 create=new AlertDialog.Builder(activity).setTitle(R.string.create_folder).setView(R.layout.edittext).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

				 @Override
				 public void onClick(DialogInterface p1, int p2)
				 {
				 String name=((TextView)create.findViewById(R.id.edittext)).getText().toString().trim();
				 if(TextUtils.isEmpty(name)||!new File(current,name).mkdirs())
				 Toast.makeText(activity,R.string.create_fail,Toast.LENGTH_SHORT).show();

				 }
				 }).setNegativeButton(android.R.string.cancel, null)
				 .setOnDismissListener(new DialogInterface.OnDismissListener(){

				 @Override
				 public void onDismiss(DialogInterface p1)
				 {
				 dialog.show();
				 open(current);
				 }
				 }).create();
				 create.show();
				 ((TextView)create.findViewById(R.id.edittext)).setText(null);*/
				break;
		}
	}

	@Override
	public boolean accept(File p1)
	{
		//return p1.isDirectory();
		return true;
	}

	@Override
	public int compare(File p1, File p2)
	{
		if (p1.isFile() && p2.isDirectory())
			return 1;
		else if (p1.isDirectory() && p2.isFile())
			return -1;
		char n1=0;
		char n2=0;
		for (int i=0;i < Math.min(p1.getName().length(), p2.getName().length());i++)
		{
			n1 = p1.getName().charAt(i);
			n2 = p2.getName().charAt(i);
			if (n1 != n2)
				break;
		}
		if (n1 == n2)
		//判断长度
			return Integer.compare(p1.getName().length(), p2.getName().length());
		//非中文，直接比对
		if (n1 < 123 && n2 < 123)
		{
			if (Character.toLowerCase(n1) == Character.toLowerCase(n2))
			{
				if (n1 <= 'Z' && n2 >= 'a')
					return 1;
				else
				if (n1 >= 'a' && n2 <= 'Z')
					return -1;
			}
			return Character.compare(Character.toLowerCase(n1), Character.toLowerCase(n2));

		}
		//第一个是中文，调换位置
		if (n1 > 128 && n2 < 128)
			return 1;
		//第二个是中文，位置不变
		if (n1 < 128 && n2 > 128)
			return -1;
		//都是中文
		return compare(p1.getName(), p2.getName());
	}


	private int compare(String name1, String name2)
	{
		return mCollator.compare(name1, name2);
		/*if(compare<0)return -1;
		 else if(compare>0)return 1;
		 return 0;*/
	}



	private void open(File file)
	{
		current = file;
		//dialog.setMessage(current.getAbsolutePath());
		File[] folderList=file.listFiles(this);
		if (folderList != null)
			Arrays.sort(folderList, this);
		list.clear();
		Collections.addAll(list, folderList);
		mFolderAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
	{
		if (p3 == 0)
		{
			//返回上级
			if (!current.equals(root))
				open(current.getParentFile());
		}
		else
		{
			File click=list.get(p3 - mListView.getHeaderViewsCount());
			
			if (click.isFile())
			{
				if (call != null)
					call.onPath(click);
				dismiss();
			}
			else

				open(click);
		}
	}
	public void setCallback(Callback call)
	{
		this.call = call;
	}
	public interface Callback
	{
		void onPath(File file);
	}
}
