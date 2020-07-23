package com.moe.video.framework.app;
import android.widget.ListAdapter;
import android.view.ViewGroup;
import android.database.DataSetObserver;
import android.view.View;
import java.util.ArrayList;
import java.io.File;
import java.util.List;
import android.view.LayoutInflater;
import android.widget.TextView;
import com.moe.video.framework.R;
import android.widget.ImageView;

public class FolderAdapter implements ListAdapter
{
	private ArrayList<DataSetObserver> mDataset=new ArrayList<>();
	private List<File> list;
	public FolderAdapter(List<File> list){
		this.list=list;
	}
	
	public void notifyDataSetChanged(){
		for(DataSetObserver dataset:mDataset)
		dataset.onChanged();
	}
	@Override
	public boolean areAllItemsEnabled()
	{
		// TODO: Implement this method
		return true;
	}

	@Override
	public boolean isEnabled(int p1)
	{
		// TODO: Implement this method
		return true;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver p1)
	{
		mDataset.add(p1);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver p1)
	{
		mDataset.remove(p1);
	}

	@Override
	public int getCount()
	{
		// TODO: Implement this method
		return list.size();
	}

	@Override
	public Object getItem(int p1)
	{
		// TODO: Implement this method
		return list.get(p1);
	}

	@Override
	public long getItemId(int p1)
	{
		// TODO: Implement this method
		return p1;
	}

	@Override
	public boolean hasStableIds()
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public View getView(int p1, View p2, ViewGroup p3)
	{
		if(p2==null)
			p2=LayoutInflater.from(p3.getContext()).inflate(R.layout.folder_item,p3,false);
		((TextView)p2.findViewById(R.id.title)).setText(list.get(p1).getName());
		((ImageView)p2.findViewById(R.id.icon)).setImageResource(list.get(p1).isDirectory()?R.drawable.folder:R.drawable.file);
		return p2;
	}

	@Override
	public int getItemViewType(int p1)
	{
		// TODO: Implement this method
		return 0;
	}

	@Override
	public int getViewTypeCount()
	{
		// TODO: Implement this method
		return 1;
	}

	@Override
	public boolean isEmpty()
	{
		// TODO: Implement this method
		return list.isEmpty();
	}
	
}
