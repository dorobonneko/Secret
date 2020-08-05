package com.moe.video.framework.Engine;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import com.caverock.androidsvg.SVG;
import com.moe.video.framework.activity.ModelActivity;
import com.moe.video.framework.activity.fragment.AppBrandFragment;
import com.moe.video.framework.pkg.Packet;
import java.io.InputStream;
import org.mozilla.javascript.Function;
import java.io.IOException;
import com.caverock.androidsvg.SVGParseException;

public class Window
{
	private AppBrandFragment f;
	public Window(AppBrandFragment f){
		this.f=f;
	}
	public String getPackageName(){
		return f.getPackageName();
	}
	public void refresh(){
		f.refresh();
	}
	public void next(){
		f.next();
	}
	public void close(){
		f.close();
	}
	public void reload(){
		f.reload();
	}
	public Packet getPackage(){
		return ((ModelActivity)f.getActivity()).getPacket();
	}
	public Context getContext(){
		return f.getContext();
	}
	public void post(final Object run){
		f.getActivity().runOnUiThread(new Runnable(){

				@Override
				public void run()
				{
					Function fun=(Function) run;
					org.mozilla.javascript.Context context=org.mozilla.javascript.Context.enter();
					fun.call(context,fun,fun,new Object[0]);
					context.exit();
				}
			});
	}
	public Menu getMenu(){
		android.view.Menu m=f.getMenu();
		if(m!=null)
		return new Menu(m);
		return null;
	}
	public class Menu{
		android.view.Menu menu;
		Menu(android.view.Menu menu){
			this.menu=menu;
		}
		public void clear(){
			menu.clear();
		}
		public MenuItem add(String key,String title,Function click){
			MenuItem item=find(key);
			if(item==null){
				item=new MenuItem(menu.add(0,key.hashCode(),0,title));
			}
			item.setTitle(title);
			item.setClick(click);
			return item;
		}
		public MenuItem find(String key){
			if(key!=null)
			return new MenuItem(menu.findItem(key.hashCode()));
			return null;
		}
		public class MenuItem{
			android.view.MenuItem mMenuItem;
			MenuItem(android.view.MenuItem item){
				mMenuItem=item;
			}
			public void delete(){
				menu.removeItem(mMenuItem.getItemId());
			}
			public void setTitle(String title){
				mMenuItem.setTitle(title);
				mMenuItem.setTitleCondensed(title);
			}
			public void setShowAsAction(boolean show){
				mMenuItem.setShowAsActionFlags(show?mMenuItem.SHOW_AS_ACTION_ALWAYS:0);
			}
			public void setIcon(String icon){
				InputStream input=getPackage().getFile(icon);
				try
				{
					Drawable d=new PictureDrawable(SVG.getFromInputStream(input).renderToPicture());
					input.close();
					mMenuItem.setIcon(d);
					
				}
				catch (IOException e)
				{}
				catch (SVGParseException e)
				{}

				}
			public void setClick(final Function fun){
				mMenuItem.setOnMenuItemClickListener(new android.view.MenuItem.OnMenuItemClickListener(){

						@Override
						public boolean onMenuItemClick(android.view.MenuItem p1)
						{
							org.mozilla.javascript.Context context=org.mozilla.javascript.Context.enter();
								
							fun.call(context,fun,fun,new Object[0]);
							context.exit();
							return true;
						}
					});
			}
		}
	}
	
}
