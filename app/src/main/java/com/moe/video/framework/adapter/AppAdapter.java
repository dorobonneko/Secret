package com.moe.video.framework.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.moe.video.framework.R;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import com.moe.video.framework.aidl.PackageManager;
import com.moe.video.framework.content.Package;
import android.os.RemoteException;
public class AppAdapter extends BaseAdapter
{
	private List<Package> list;
    private PackageManager pm;
	public AppAdapter(List<Package> list,PackageManager pm){
		this.list=list;
        this.pm=pm;
		}


	
	@Override
	public int getCount()
	{
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
	public View getView(int p1, View p2, ViewGroup p3)
	{
		if(p2==null){
			p2=LayoutInflater.from(p3.getContext()).inflate(R.layout.app_view,p3,false);
		}
		ViewHolder vh=(AppAdapter.ViewHolder) p2.getTag();
		if(vh==null)
			p2.setTag(vh=new ViewHolder(p2));
			Package p=list.get(p1);
			vh.title.setText(p.title);
            vh.icon.setImageBitmap(p.loadLogo());
		return p2;
	}
	class ViewHolder{
		ImageView icon;
		TextView title;
		ViewHolder(View v){
			icon=v.findViewById(R.id.icon);
			title=v.findViewById(R.id.title);
		}
	}
}
