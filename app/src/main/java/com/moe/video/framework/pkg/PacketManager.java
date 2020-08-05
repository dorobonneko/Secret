package com.moe.video.framework.pkg;
import android.graphics.drawable.Drawable;
import android.util.LruCache;
import android.graphics.drawable.BitmapDrawable;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import android.content.Context;
import java.io.File;
import java.io.IOException;
import com.moe.video.framework.util.FileUtil;
import java.io.OutputStream;
import java.nio.file.Files;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URI;
import android.net.Uri;

public class PacketManager
{
	private List<OnPacketChangedListener> listener=new ArrayList<>();
	private static PacketManager mPacketManager;
	private Map<String,Packet> list=new LinkedHashMap<>();
	private LruCache<String,Drawable> cache=new LruCache<String,Drawable>(100){

		@Override
		protected void entryRemoved(boolean evicted, String key, Drawable oldValue, Drawable newValue)
		{
		}
		
		
	};
	private File workPath;
	private PacketManager(){}
	public static PacketManager getInstance(){
		if(mPacketManager==null)
			synchronized(PacketManager.class){
				if(mPacketManager==null)
					mPacketManager=new PacketManager();
			}
		return mPacketManager;
	}
	public static void init(Context context){
		PacketManager.getInstance().workPath=context.getFileStreamPath("app");
		if(!getInstance().workPath.exists())getInstance().workPath.mkdirs();
		//加载包
		File[] files=getInstance().workPath.listFiles();
		if(files!=null)
		for(File f:files){
			try
			{
				Packet p=new Packet(f.getAbsolutePath(),false);
				getInstance().list.put(p.packageName,p);
			}
			catch (IOException e)
			{
				FileUtil.delete(f);
			}
		}
	}
	public Drawable loadLogo(Packet packet){
		Drawable icon=cache.get(packet.packageName+"#"+packet.logo);
		if(icon!=null)
			return icon;
			try{
			cache.put(packet.packageName+"#"+packet.logo,icon=BitmapDrawable.createFromStream(packet.loadLogo(),null));
			}catch(Exception e){}
		return icon;
	}
	public Packet getPacket(String packageName){
		Packet p= list.get(packageName);
		if(p==null)
		{
			try
			{
				p = new Packet(new File(workPath, packageName).getAbsolutePath(), false);
				list.put(p.packageName,p);
			}
			catch (IOException e)
			{}
		}
		return p;
	}
	public List<Packet> getAllPacket(){
		return new ArrayList<Packet>(list.values());
	}
	public void installPacket(String path) throws Exception{
		//安装，安装完通知监听者
		boolean update=false;
		Packet p=new Packet(path,true);
		File file=new File(workPath,p.packageName);
		if(file.exists())
			update=true;
		Files.copy(Paths.get(URI.create(Uri.fromFile(new File(path)).toString())),new FileOutputStream(file));
		Packet old=list.get(p.packageName);
		list.put(p.packageName,new Packet(file.getAbsolutePath(),false));
		
		for(OnPacketChangedListener l:listener)
		if(update)
			l.onPacketUpdate(old,list.get(p.packageName));
			else
		l.onPacketAdded(list.get(p.packageName));
		
	}
	public void uninstallPacket(String packageName){
		Packet packet=list.remove(packageName);
		new File(packet.source).delete();
		for(OnPacketChangedListener l:listener)
		l.onPacketRemoved(packet);
	}
	public void registerOnPacketChangedListener(OnPacketChangedListener l){
		if(!listener.contains(l))
		listener.add(l);
	}
	public void unregisterOnPacketChangedListener(OnPacketChangedListener l){
		listener.remove(l);
	}
	public interface OnPacketChangedListener{
		void onPacketAdded(Packet packet);
		void onPacketUpdate(Packet old,Packet new_);
		void onPacketRemoved(Packet packet);
	}
}
