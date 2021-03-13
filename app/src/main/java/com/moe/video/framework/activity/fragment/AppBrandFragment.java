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
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.app.AlertDialog;
import android.graphics.Color;

public class AppBrandFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,View.OnApplyWindowInsetsListener {
	private Packet mPacket;
	private Engine mEngine;
	private SwipeRefreshLayout refresh;
    private RecyclerView mRecyclerView;
	private PostAdapter pa;
	private int page=1;
	private boolean canLoadMore=true;
	private List data;
    private String layout="grid";
	public String getPackageName() {
		return mPacket.packageName;
	}
	public Menu getMenu() {
		Toolbar toolbar=getView().findViewById(R.id.toolbar);
		if (toolbar != null)
            return toolbar.getMenu();
		return null;
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO: Implement this method
		super.onAttach(activity);
		mPacket = ((ModelActivity)activity).getPacket();
		mEngine = new Engine(this);
		Bundle mBundle= getArguments();
		String exe=mBundle.getString("exe");
		try {
			mEngine.eval(new InputStreamReader(mPacket.getFile(exe)));
        } catch (Exception e) {
			new AlertDialog.Builder(getContext()).setMessage(e.toString()).show();
        }
	}
	public void refresh() {
		refresh.setRefreshing(true);
		onRefresh();
	}
	public void next() {
		if (canLoadMore) {
			loadMore();
        }
	}
	public void close() {
		getActivity().onBackPressed();
	}

	@Override
	public WindowInsets onApplyWindowInsets(final View p1, final WindowInsets p2) {
        final View toolbar=getView().findViewById(R.id.toolbar);
        View view=(View) toolbar.getParent();
        view.setPadding(p2.getSystemWindowInsetLeft(), p2.getSystemWindowInsetTop(), p2.getSystemWindowInsetRight(), p2.getSystemWindowInsetBottom());
        toolbar.post(new Runnable(){

                @Override
                public void run() {
                    p1.setPadding(p2.getSystemWindowInsetLeft(), p2.getSystemWindowInsetTop()+toolbar.getHeight(), p2.getSystemWindowInsetRight(), p2.getSystemWindowInsetBottom());
                    
                }
            });
        
		return p2;
	}


	public void reload() {
		try {
			NativeObject toolbar_json=(NativeObject) mEngine.eval("module.exports.toolbar(" + getArguments().getString("args", "undefined") + ")");
			if (toolbar_json == null)return;
			Toolbar toolbar=getView().findViewById(R.id.toolbar);
			toolbar.getMenu().clear();
			Object tit=toolbar_json.get("title");
			toolbar.setTitle(tit == null ?null: ScriptRuntime.toString(tit));
			NativeArray menu=(NativeArray) toolbar_json.get("menu");
			if (menu != null)
				for (int i=0;i < menu.size();i++) {
					final NativeObject menuitem=(NativeObject) menu.get(i);
					Menu m=toolbar.getMenu();
					final Object id=menuitem.get("id");
					Object title=menuitem.get("title");
					MenuItem item=m.add(0, id == null ?0: id.toString().hashCode(), 0, title == null ?null: title.toString());
					item.setTitleCondensed(title == null ?"": title.toString());
                    Object icon=menuitem.get("icon");
					if (icon != null) {
						InputStream input=mPacket.getFile(icon.toString());
						Drawable d=new PictureDrawable(SVG.getFromInputStream(input).renderToPicture());
						input.close();
						item.setIcon(d);
					}
					Object showAsAction=menuitem.get("showAsAction");
					if (showAsAction != null) {
						if (ScriptRuntime.toBoolean(showAsAction))
							item.setShowAsActionFlags(item.SHOW_AS_ACTION_ALWAYS);
					}
					item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){

							@Override
							public boolean onMenuItemClick(MenuItem p1) {
								try {
									Function fun=(Function) menuitem.get("click");
                                    Context c=Context.enter();
                                    fun.call(c, menuitem, menuitem, new Object[]{mEngine.getRuntime().window.getMenu().find(id == null ?null: id.toString())});
                                    c.exit();
								} catch (Exception e) {}
								return true;
							}
						});
				}
		} catch (ScriptException s) {} catch (Exception e) {
			mEngine.getRuntime().toast(e.getMessage());
		}
		refresh();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return inflater.inflate(R.layout.appbrand_view, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		refresh = view.findViewById(R.id.refresh);
		refresh.setOnRefreshListener(this);
		mRecyclerView = view.findViewById(R.id.recyclerview);
        mRecyclerView.setClipToPadding(false);
        mRecyclerView.setOnApplyWindowInsetsListener(this);
        mRecyclerView.requestApplyInsets();
		mRecyclerView.addItemDecoration(new Space(getContext()));
        
		mRecyclerView.setAdapter(pa = new PostAdapter(data = new ArrayList()));
		mRecyclerView.addOnScrollListener(new Scroll());
        try {
            mRecyclerView.setBackgroundColor(Color.parseColor(ScriptRuntime.toString(mEngine.eval("module.bgColor"))));
        } catch (Exception e) {}
		reload();

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onActivityCreated(savedInstanceState);


	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onRefresh() {
		page = 1;
		loadMore();
	}
	public void loadMore() {
		refresh.setRefreshing(true);
		new Thread(){
			public void run() {
				try {
					final NativeObject data=(NativeObject) mEngine.eval("module.exports.fetch(" + getArguments().getString("args", "undefined") + "," + page + ");");
					layout = ScriptRuntime.toString(data.getOrDefault("layout", "grid"));
                    final NativeArray items=(NativeArray) data.get("items");
					getActivity().runOnUiThread(new Runnable(){

							@Override
							public void run() {
								refresh.setRefreshing(false);
								canLoadMore = page < ScriptRuntime.toInt32(data.get("nextPage"));
								if (page == 1) {
									AppBrandFragment.this.data.clear();
									pa.notifyDataSetChanged();
                                    //重建布局
                                    initLayout();
									if (items != null) {
                                        AppBrandFragment.this.data.addAll(items);
                                        pa.notifyItemRangeInserted(0, items.size());
									}
								} else {
									int count=pa.getItemCount();
									if (items != null) {
                                        AppBrandFragment.this.data.addAll(items);
                                        pa.notifyItemRangeInserted(count, items.size());
									}

								}
								page = ScriptRuntime.toInt32(data.get("nextPage"));
							}
						});
				} catch (final Exception e) {
					getView().post(new Runnable(){

							@Override
							public void run() {
								refresh.setRefreshing(false);
								new AlertDialog.Builder(getActivity()).setMessage(e.getMessage()).show();
							}
						});
				}
			}
		}.start();
	}
    private void initLayout() {
        switch (layout) {
            case "grid":

                 mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 6));
                ((GridLayoutManager)mRecyclerView.getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){

                        @Override
                        public int getSpanSize(int p1) {
                            switch (((NativeObject)data.get(p1)).get("type").toString()) {
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
                break;
            case "waterflow":
                //瀑布流
                 mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                 StaggeredGridLayoutManager sglm=(StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
                sglm.setAutoMeasureEnabled(true);
                break;
        }
    }
	class Scroll extends RecyclerView.OnScrollListener {

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
            if (dy > 0 && !refresh.isRefreshing() && canLoadMore) {
                RecyclerView.LayoutManager lm= recyclerView.getLayoutManager();
                if (lm instanceof GridLayoutManager) {
                    GridLayoutManager gld=(GridLayoutManager)lm;
                    if (gld.findLastVisibleItemPosition() > recyclerView.getAdapter().getItemCount() - 4) {
                        loadMore();
                    }
                } else if (lm instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager sglm=(StaggeredGridLayoutManager) lm;
                    int[] positions=new int[sglm.getSpanCount()];
                    sglm.findLastVisibleItemPositions(positions);
                    if (positions[0] >= recyclerView.getAdapter().getItemCount() - sglm.getSpanCount() * 2)
                        loadMore();
                }
            }
            
		}

	}

}
