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

public class AppBrandFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener
{
	private Packet mPacket;
	private Engine mEngine;
	private SwipeRefreshLayout refresh;
	private PostAdapter pa;
	private int page=1;
	private boolean canLoadMore=true;
	private List data;
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
			mEngine.eval(new InputStreamReader(mPacket.getExe(exe)));
			}
		catch (Exception e)
		{
			Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
		}
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
								return 6;
							case "post":
								return 2;
							case "play":
								return 3;
						}
						return 0;
					}
				});
		mRecyclerView.setAdapter(pa=new PostAdapter(data=new ArrayList()));
		mRecyclerView.addOnScrollListener(new Scroll());
		try
		{
			NativeObject toolbar_json=(NativeObject) mEngine.eval("module.exports.toolbar()");
			Toolbar toolbar=view.findViewById(R.id.toolbar);
			toolbar.setTitle(toolbar_json.get("title").toString());
			NativeArray menu=(NativeArray) toolbar_json.get("menu");
			for (int i=0;i < menu.size();i++) {
				final NativeObject menuitem=(NativeObject) menu.get(i);
				Menu m=toolbar.getMenu();
				MenuItem item=m.add(menuitem.get("title").toString());
				item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){

						@Override
						public boolean onMenuItemClick(MenuItem p1)
						{
							Function fun=(Function) menuitem.get("onclick");
							Context c=Context.enter();
							fun.call(c,menuitem,menuitem,new Object[0]);
							c.exit();
							return true;
						}
					});
			}
		}
		catch (ScriptException e)
		{}
		catch (Exception e)
		{}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onActivityCreated(savedInstanceState);
		refresh.setRefreshing(true);
		onRefresh();
		
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
					final NativeObject data=(NativeObject) mEngine.eval("module.exports.fetch("+getArguments().getString("args","null")+","+page+");");
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
									AppBrandFragment.this.data.addAll(items);
									pa.notifyItemRangeInserted(0,items.size());
								}else{
									int count=pa.getItemCount();
									AppBrandFragment.this.data.addAll(items);
									pa.notifyItemRangeInserted(count,items.size());
									
								}
								page=ScriptRuntime.toInt32(data.get("nextPage"));
							}
						});
				}
				catch (final ScriptException e)
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
