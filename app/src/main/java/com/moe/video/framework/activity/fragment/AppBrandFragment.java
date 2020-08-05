package com.moe.video.framework.activity.fragment;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.moe.video.framework.Engine.Engine;
import com.moe.video.framework.R;
import com.moe.video.framework.activity.ModelActivity;
import com.moe.video.framework.pkg.Packet;
import java.io.InputStreamReader;
import javax.script.ScriptException;
import android.view.MenuInflater;
import android.view.Menu;
import org.json.JSONObject;
import org.json.JSONException;
import android.widget.Toolbar;
import org.json.JSONArray;
import android.view.MenuItem;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.InterpretedFunction;
import org.mozilla.javascript.ArrowFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import android.support.v4.widget.SwipeRefreshLayout;
import com.moe.video.framework.adapter.PostAdapter;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import org.mozilla.javascript.NativeNumber;
import java.util.List;
import java.util.ArrayList;
import org.mozilla.javascript.Function;
import com.moe.video.framework.util.Space;
import org.mozilla.javascript.EcmaError;
import android.graphics.drawable.Drawable;
import android.view.WindowInsets;
import java.io.InputStream;
import android.util.TypedValue;
import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import com.caverock.androidsvg.SVG;
import android.graphics.drawable.PictureDrawable;

public class AppBrandFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,View.OnApplyWindowInsetsListener
{
	private Packet mPacket;
	private Engine mEngine;
	private SwipeRefreshLayout refresh;
	private PostAdapter pa;
	private int page=1;
	private boolean canLoadMore=true;
	private List data;

	public String getPackageName()
	{
		return mPacket.packageName;
	}
	public Menu getMenu(){
		Toolbar toolbar=getView().findViewById(R.id.toolbar);
		if(toolbar!=null)
		return toolbar.getMenu();
		return null;
	}
	@Override
	public void onAttach(Activity activity)
	{
		// TODO: Implement this method
		super.onAttach(activity);
		mPacket=((ModelActivity)activity).getPacket();
		mEngine=new Engine(this);
		Bundle mBundle= getArguments();
		String exe=mBundle.getString("exe");
		try
		{
			mEngine.eval(new InputStreamReader(mPacket.getFile(exe)));
			}
		catch (Exception e)
		{
			Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
		}
	}
	public void refresh(){
		refresh.setRefreshing(true);
		onRefresh();
	}
	public void next(){
		if(canLoadMore){
			loadMore();
			}
	}
	public void close(){
		getActivity().onBackPressed();
	}

	@Override
	public WindowInsets onApplyWindowInsets(View p1, WindowInsets p2)
	{
		p1.setPadding(p2.getSystemWindowInsetLeft(),p2.getSystemWindowInsetTop(),p2.getSystemWindowInsetRight(),p2.getSystemWindowInsetBottom());
		return p2;
	}

	
	public void reload(){
		try
		{
			NativeObject toolbar_json=(NativeObject) mEngine.eval("module.exports.toolbar("+getArguments().getString("args","undefined")+")");
			if(toolbar_json==null)return;
			Toolbar toolbar=getView().findViewById(R.id.toolbar);
			toolbar.getMenu().clear();
			Object tit=toolbar_json.get("title");
			toolbar.setTitle(tit==null?null:ScriptRuntime.toString(tit));
			NativeArray menu=(NativeArray) toolbar_json.get("menu");
			if(menu!=null)
				for (int i=0;i < menu.size();i++) {
					final NativeObject menuitem=(NativeObject) menu.get(i);
					Menu m=toolbar.getMenu();
					final Object id=menuitem.get("id");
					Object title=menuitem.get("title");
					MenuItem item=m.add(0,id==null?0:id.toString().hashCode(),0,title==null?null:title.toString());
					item.setTitleCondensed(title==null?"":title.toString());
						Object icon=menuitem.get("icon");
					if(icon!=null)
					{
						InputStream input=mPacket.getFile(icon.toString());
						Drawable d=new PictureDrawable(SVG.getFromInputStream(input).renderToPicture());
						input.close();
						item.setIcon(d);
					}
					Object showAsAction=menuitem.get("showAsAction");
					if(showAsAction!=null){
						if(ScriptRuntime.toBoolean(showAsAction))
							item.setShowAsActionFlags(item.SHOW_AS_ACTION_ALWAYS);
					}
					item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){

							@Override
							public boolean onMenuItemClick(MenuItem p1)
							{
								try{
									Function fun=(Function) menuitem.get("click");
								Context c=Context.enter();
								fun.call(c,menuitem,menuitem,new Object[]{mEngine.getRuntime().window.getMenu().find(id==null?null:id.toString())});
								c.exit();
								}catch(Exception e){}
								return true;
							}
						});
				}
		}catch(ScriptException s){}
		
		catch (Exception e)
		{
			mEngine.getRuntime().toast(e.getMessage());
		}
		refresh();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		
		return inflater.inflate(R.layout.appbrand_view,container,false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		view.setOnApplyWindowInsetsListener(this);
		view.requestApplyInsets();
		refresh=view.findViewById(R.id.refresh);
		refresh.setOnRefreshListener(this);
		RecyclerView mRecyclerView=view.findViewById(R.id.recyclerview);
		mRecyclerView.addItemDecoration(new Space(getContext()));
		mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),6));
			((GridLayoutManager)mRecyclerView.getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){

					@Override
					public int getSpanSize(int p1)
					{
						switch(((NativeObject)data.get(p1)).get("type").toString()){
							case "title":
							case "detail":
							case "thumb":
								return 6;
							case "post":
							case "icon":
								return 2;
							case "play":
								return 3;
						}
						return 0;
					}
				});
		mRecyclerView.setAdapter(pa=new PostAdapter(data=new ArrayList()));
		mRecyclerView.addOnScrollListener(new Scroll());
		reload();

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onActivityCreated(savedInstanceState);
		
		
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onRefresh()
	{
		page=1;
		loadMore();
	}
	public void loadMore(){
		refresh.setRefreshing(true);
		new Thread(){
			public void run(){
				try
				{
					final NativeObject data=(NativeObject) mEngine.eval("module.exports.fetch("+getArguments().getString("args","undefined")+","+page+");");
					final NativeArray items=(NativeArray) data.get("items");
					getActivity().runOnUiThread(new Runnable(){

							@Override
							public void run()
							{
								refresh.setRefreshing(false);
								canLoadMore=page<ScriptRuntime.toInt32(data.get("nextPage"));
								if(page==1){
									AppBrandFragment.this.data.clear();
									pa.notifyDataSetChanged();
									if(items!=null){
									AppBrandFragment.this.data.addAll(items);
									pa.notifyItemRangeInserted(0,items.size());
									}
								}else{
									int count=pa.getItemCount();
									if(items!=null){
									AppBrandFragment.this.data.addAll(items);
									pa.notifyItemRangeInserted(count,items.size());
									}
									
								}
								page=ScriptRuntime.toInt32(data.get("nextPage"));
							}
						});
				}catch(ScriptException e){}
				catch (final Exception e)
				{
					getActivity().runOnUiThread(new Runnable(){

							@Override
							public void run()
							{
								refresh.setRefreshing(false);
								Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
							}
						});
				}
			}
		}.start();
	}
	class Scroll extends RecyclerView.OnScrollListener
	{

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy)
		{
			super.onScrolled(recyclerView, dx, dy);
			GridLayoutManager gld=(GridLayoutManager) recyclerView.getLayoutManager();
			if(dy>0&&!refresh.isRefreshing()&&canLoadMore&&gld.findLastVisibleItemPosition()>recyclerView.getAdapter().getItemCount()-4){
				loadMore();
			}
		}
		
	}
	
}
