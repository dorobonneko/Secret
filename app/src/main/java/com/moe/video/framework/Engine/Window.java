package com.moe.video.framework.Engine;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.moe.video.framework.pkg.Packet;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.json.JsonParser;
import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ConsString;
import org.mozilla.javascript.NativeString;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.IdScriptableObject;
import com.moe.video.framework.content.Package;
import java.io.FileNotFoundException;

public class Window
{
	private Window.Callback callback;
	public Window(Window.Callback f){
		this.callback=f;
	}
    public NativeObject getFocus(){
        return callback.getFocus();
    }
    public void download(String url,String type){
        callback.download(url,type);
    }
    public boolean canLoadMore(){
        return callback.canLoadMore();
    }
    public int getPage(){
        return callback.getPage();
    }
    public List<NativeObject> getList(){
        return callback.getList();
    }
    
    public Engine getEngine(){
        return callback.getEngine();
    }
	public String getPackageName(){
		return callback.getPackageName();
	}
	public void refresh(){
		callback.refresh();
	}
	public void next(){
		callback.next();
	}
	public void close(){
		callback.close();
	}
	public void reload(){
		callback.reload();
	}
	public Package getPacket(){
		return callback.getPackage();
	}
	public android.content.Context getContext(){
		return callback.getContext();
	}
    public void openVideo(String json){
        callback.openVideo(json);
    }
    public void openVideo(NativeObject video){
        Context context=Context.enter();
        openVideo(NativeJSON.stringify(context,video,video,null,null).toString());
        context.exit();
    }
    public void open(String name,NativeObject arg){
        callback.open(name,arg);        
        
    }
    public void open(String name){
        callback.open(name,null);        

    }
    public void open(IdScriptableObject name){
        if(name instanceof NativeObject){
            String type=ScriptRuntime.toString(name.get("type"));
            switch(type){
                case "audio":
                    openAudio((NativeObject)name);
                    break;
                case "video":
                    openVideo((NativeObject)name);
                    break;
            }
        }
    }
    public void openImage(String name,NativeObject arg){
        callback.openImage(name,arg);
    }
    public void openAudio(NativeObject obj){
        callback.openAudio(obj);
    }
    public Object getArg(){
        return callback.getArg();
    }
	public void post(final Function run){
		callback.post(new Runnable(){

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
    public void scrollTo(int id){
        callback.scrollTo(id);
    }
	public Menu getMenu(){
		android.view.Menu m=callback.getMenu();
		if(m!=null)
		return new Menu(callback,m);
		return null;
	}
    public StatusBar getStatusBar(){
        return new StatusBar(callback);
    }
	public static class Menu{
		android.view.Menu menu;
        Window.Callback call;
		Menu(Window.Callback call,android.view.Menu menu){
			this.menu=menu;
            this.call=call;
		}
		public void clear(){
			menu.clear();
		}
		public MenuItem add(String title,Function click){
			MenuItem item=new MenuItem(call,menu,menu.add(0,menu.size(),menu.size(),title));
			item.setClick(click);
			return item;
		}
		public MenuItem get(int index){
	
			return new MenuItem(call,menu,menu.getItem(index));
			
		}
		public static class MenuItem{
			android.view.MenuItem mMenuItem;
            android.view.Menu menu;
            Window.Callback call;
			public MenuItem(Window.Callback call,android.view.Menu menu,android.view.MenuItem item){
				mMenuItem=item;
                this.menu=menu;
                this.call=call;
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
				try {
                    InputStream input=call.getPackage().getFile(icon);
                    try {
                        Drawable d=new PictureDrawable(SVG.getFromInputStream(input).renderToPicture());
                        input.close();
                        mMenuItem.setIcon(d);

                    } catch (IOException e) {} catch (SVGParseException e) {}
                } catch (FileNotFoundException e) {}

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
	public static interface Callback extends Runtime.Callback,StatusBar.Callback{
        public Engine getEngine();
        public String getPackageName();
        public void refresh()
        public void next()
        public void close()
        public void reload()
        public Package getPackage()
        public android.content.Context getContext()
        public android.view.Menu getMenu();
        public void post(Runnable run);
        public void openVideo(String url);
        public void open(String name,NativeObject arg);
        public void openImage(String name,NativeObject arg);
        public int getPage();
        public List<NativeObject> getList();
        public Object getArg();
        public void scrollTo(int id);
        public boolean canLoadMore();
        public void download(String url,String type);
        public NativeObject getFocus();
        public void openAudio(NativeObject obj);
         }
        
}
