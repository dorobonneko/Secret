package com.moe.video.framework.adapter;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.moe.video.framework.R;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import com.moe.video.framework.pkg.Packet;
import com.moe.video.framework.pkg.PacketManager;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.text.Collator;
import java.util.Locale;

public class AppAdapter extends BaseAdapter implements PacketManager.OnPacketChangedListener,Comparator<Packet>
{
	private List<Packet> list;
	private Collator mCollator;
	private PacketManager pm;
	public AppAdapter(PacketManager pm){
		this.pm=pm;
		this.list=pm.getAllPacket();
		Collections.sort(list,this);
		pm.registerOnPacketChangedListener(this);
		mCollator=Collator.getInstance(Locale.CHINA);
	}

	@Override
	public void onPacketRemoved(Packet packet)
	{
		list.remove(packet);
		notifyDataSetChanged();
	}

	@Override
	public void onPacketAdded(Packet packet)
	{
		list.add(packet);
		Collections.sort(list,this);
		notifyDataSetChanged();
	}

	@Override
	public void onPacketUpdate(Packet old, Packet new_)
	{
		int index=list.indexOf(old);
		if(index==-1){
			list.add(new_);
			Collections.sort(list,this);
			}else{
				list.set(index,new_);
			}
			notifyDataSetChanged();
	}

	@Override
	public int compare(Packet p1, Packet p2)
	{
		if(p1.title==null){
			return 0;
		}
		if(p2.title==null)
			return 1;
			String name1=p1.title;
			String name2=p2.title;
		char n1=0;
		char n2=0;
		for(int i=0;i<Math.min(name1.length(),name2.length());i++){
			n1=name1.charAt(i);
			n2=name2.charAt(i);
			if(n1!=n2)
				break;
		}
		if(n1==n2)
		//判断长度
			return Integer.compare(name1.length(),name2.length());
		//非中文，直接比对
		if(n1<123&&n2<123){
			if(Character.toLowerCase(n1)==Character.toLowerCase(n2)){
				if(n1<='Z'&&n2>='a')
					return 1;
				else
				if(n1>='a'&&n2<='Z')
					return -1;
			}
			return Character.compare(Character.toLowerCase(n1),Character.toLowerCase(n2));

		}
		//第一个是中文，调换位置
		if(n1>128&&n2<128)
			return 1;
		//第二个是中文，位置不变
		if(n1<128&&n2>128)
			return -1;
		//都是中文
		return mCollator.compare(name1,name2);
		
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
			Packet p=list.get(p1);
			vh.title.setText(p.title);
			vh.icon.setImageDrawable(pm.loadLogo(p));
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
