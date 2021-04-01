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
import com.moe.video.framework.Engine.Window;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView.ItemAnimator.ItemHolderInfo;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.content.Intent;
import com.moe.video.framework.VideoActivity;
import android.provider.Settings;
import android.net.Uri;
import com.moe.video.framework.util.StaticData;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import com.moe.video.framework.util.ScriptUtil;
import java.util.function.Consumer;
import org.mozilla.javascript.NativeJavaArray;
import org.mozilla.javascript.NativeString;
import org.mozilla.javascript.NativeJavaObject;
import java.util.function.Predicate;
import org.mozilla.javascript.Scriptable;
import java.io.IOException;
import com.moe.video.framework.AudioActivity;
import com.moe.video.framework.content.Package;
public class AppBrandFragment extends Fragment implements Window.Callback,SwipeRefreshLayout.OnRefreshListener,View.OnApplyWindowInsetsListener {
	private Package mPacket;
	private Engine mEngine;
	private SwipeRefreshLayout refresh;
    private RecyclerView mRecyclerView;
	private PostAdapter pa;
	private int page=1;
	private boolean canLoadMore=true;
	private List<NativeObject> data;
    private String video,layout;
    private Object arg;
    private NativeObject exports;

    @Override
    public void onResume() {
        super.onResume();
        Function fun=(Function)exports.get("onresume");
        if(fun!=null)
            fun.call(mEngine.getContext(),fun,fun,new Object[]{arg});
    }

    @Override
    public void openAudio(NativeObject obj) {
        startActivity(new Intent(getContext(),AudioActivity.class).putExtra("key",obj));
    }

    
    
    
    
    public String log(){
        return mEngine.getRuntime().log();
    }

    @Override
    public Scriptable getScriptable() {
        return mEngine.getScriptable();
    }

    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setLight(true);
        if(exports==null)return;
        Function fun=(Function) exports.get("onload");
        if(fun!=null){
            fun.call(mEngine.getContext(),fun,fun,new Object[]{arg});
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(exports==null)return;
        Function fun=(Function) exports.get("onclose");
        if(fun!=null){
            Context context=Context.enter();
            fun.call(context,fun,fun,new Object[]{arg});
            context.exit();
        }
    }

 
    
    @Override
    public void setLight(boolean light) {
        View v=getActivity().getWindow().getDecorView();
        v.setSystemUiVisibility(light?View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR:0);
    }


    
    @Override
    public NativeObject getFocus() {
        View v=mRecyclerView.getLayoutManager().getChildAt(0);
        return data.get(mRecyclerView.getChildAdapterPosition(v));
    }


    @Override
    public boolean canLoadMore() {
        return canLoadMore;
    }

    @Override
    public void download(String url, String type) {
        startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(url),type));
    }

    

    @Override
    public void scrollTo(final int id) {
        data.stream().anyMatch(new Predicate<NativeObject>(){

                @Override
                public boolean test(NativeObject p1) {
                    boolean flag=ScriptRuntime.toInt32(p1.get("id"))==id;
                    if(flag)
                    mRecyclerView.scrollToPosition(data.indexOf(p1));
                    return flag;
                }
            });
        
    }

    
    @Override
    public List<NativeObject> getList() {
        return data;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public Object getArg() {
        return arg;
    }


    
    @Override
    public Package getPackage() {
        return mPacket;
    }
    
    @Override
    public void post(Runnable run) {
        getActivity().runOnUiThread(run);
    }

    @Override
    public void openVideo(String json) {
        if(Settings.canDrawOverlays(getContext())){
            startActivity(new Intent(getContext(),VideoActivity.class).putExtra("data",json));
        }else{
            video=json;
            Intent request = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            request.setData(Uri.parse("package:" + getPackageName()));
            request.removeFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(request,14);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==14&&resultCode==Activity.RESULT_OK){
            startActivity(new Intent(getContext(),VideoActivity.class).putExtra("data",this.video));

        }
    }

    

    @Override
    public void openImage(String name, NativeObject arg) {
        AppBrandFragment abf=new AppBrandFragment();
        Bundle bundle=new Bundle();
        String key=StaticData.uid();
        StaticData.put(key,arg);
        bundle.putString("key",key);
        bundle.putString("exe",name.concat(".js"));
        abf.setArguments(bundle);
        getFragmentManager().beginTransaction().add(android.R.id.content, abf).addToBackStack(null).commitAllowingStateLoss();
        
    }







    @Override
    public void open(String name,NativeObject arg) {
        AppBrandFragment abf=new AppBrandFragment();
        Bundle bundle=new Bundle();
        String key=StaticData.uid();
        StaticData.put(key,arg);
        bundle.putString("key",key);
        bundle.putString("exe",name.concat(".js"));
        abf.setArguments(bundle);
        getFragmentManager().beginTransaction().add(android.R.id.content, abf).addToBackStack(null).commitAllowingStateLoss();

    }


    public Engine getEngine() {
        return mEngine;
    }
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
		try {
            mEngine = new Engine(this);
            Bundle mBundle= getArguments();
            arg=StaticData.delete(mBundle.getString("key",null));
            data=new ArrayList<>();
            if(arg instanceof NativeObject){
                NativeObject args=(NativeObject) arg;
                page=ScriptRuntime.toInt32(args.getOrDefault("nextPage",1));
                canLoadMore=ScriptRuntime.toBoolean(args.getOrDefault("canLoadMore",true));
                Object items=args.get("items");
                if(items!=null)
                    data.addAll((List<NativeObject>) items);
            }
            exports=(NativeObject) mEngine.eval(new InputStreamReader(mPacket.getFile(mBundle.getString("exe"))),mBundle.getString("exe"));
            
        } catch (IOException e) {
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
        switch(ScriptRuntime.toString(mEngine.getOrDefault("layout","grid"))){
                case "gallery":
                    return p2;
            }
        toolbar.post(new Runnable(){

                @Override
                public void run() {
                    p1.setPadding(p2.getSystemWindowInsetLeft(), p2.getSystemWindowInsetTop() + toolbar.getHeight(), p2.getSystemWindowInsetRight(), p2.getSystemWindowInsetBottom());

                }
            });

		return p2;
	}

    @Override
	public void reload() {
		try {
            try {
                mRecyclerView.setBackgroundColor(Color.parseColor(ScriptRuntime.toString(mEngine.getOrDefault("bgColor","#00000000"))));
            } catch (Exception e) {}
            layout = ScriptRuntime.toString(mEngine.getOrDefault("layout","grid"));
            initLayout(layout);
            if(!layout.equals("gallery"))
                refresh();
            Function toolbar=(Function) exports.get("toolbar");
            if(toolbar==null){
                return;}
            Context context=Context.enter();
            final NativeObject toolbar_json=(NativeObject) toolbar.call(context,toolbar,toolbar,new Object[]{arg});
            context.exit();
			if (toolbar_json == null)return;
			Toolbar toolbar_view=getView().findViewById(R.id.toolbar);
			final Menu menu=toolbar_view.getMenu();
            menu.clear();
			toolbar_view.setTitle(ScriptUtil.toString(toolbar_json.getOrDefault("title",null)));
            toolbar_view.setSubtitle(ScriptUtil.toString(toolbar_json.getOrDefault("subTitle",null)));
            
			NativeArray menu_items=(NativeArray) toolbar_json.get("menu");
			if (menu_items != null)
				for (int i=0;i < menu_items.size();i++) {
					final NativeObject menuitem=(NativeObject) menu_items.get(i);
					
					MenuItem item=menu.add(0,i,i,ScriptRuntime.toString(menuitem.get("title")));
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
                                    fun.call(c, fun, fun, new Object[]{new Window.Menu.MenuItem(AppBrandFragment.this,menu,p1)});
                                    c.exit();
								} catch (Exception e) {
                                    Toast.makeText(getContext(),e.toString(),Toast.LENGTH_LONG).show();
                                }
								return true;
							}
						});
				}
		}
        catch (Exception e) {
			mEngine.getRuntime().toast(e.getMessage());
		}
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
        mRecyclerView.setFitsSystemWindows(true);
        mRecyclerView.setOnApplyWindowInsetsListener(this);
        mRecyclerView.requestApplyInsets();
        //((DefaultItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
		mRecyclerView.addItemDecoration(new Space(getContext()));
        
		mRecyclerView.setAdapter(pa = new PostAdapter(data==null?data = new ArrayList<NativeObject>():data));
		mRecyclerView.addOnScrollListener(new Scroll());
        
        
		reload();

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
                    Function fetch=(Function) exports.get("fetch");
                    Context context=Context.enter();
                    final NativeObject data=(NativeObject) fetch.call(context,fetch,fetch,new Object[]{arg,page});
                    context.exit();
					//final NativeObject data=(NativeObject)((Function)exports.get("fetch")).,arg,page);
					final List items=(List) data.get("items");
					getActivity().runOnUiThread(new Runnable(){

							@Override
							public void run() {
								refresh.setRefreshing(false);
								canLoadMore = page < ScriptRuntime.toInt32(data.get("nextPage"));
								if (page == 1) {
                                    int size=AppBrandFragment.this.data.size();
									AppBrandFragment.this.data.clear();
									pa.notifyItemRangeRemoved(0,size);
                                    //重建布局

									if (items != null) {
                                        AppBrandFragment.this.data.addAll(items);
                                        pa.notifyItemRangeInserted(0, items.size());
									}
                                    //pa.notifyDataSetChanged();
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
                    if (getView() != null)
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
    private void initLayout(String layout) {
        switch (layout) {
            case "gallery":
                refresh.setEnabled(false);
                SnapHelper snapHelper=new PagerSnapHelper();
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
                snapHelper.attachToRecyclerView(mRecyclerView);
                mRecyclerView.getLayoutManager().setAutoMeasureEnabled(true);
                break;
            case "waterflow":
                //瀑布流
                mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                StaggeredGridLayoutManager sglm=(StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
                sglm.setAutoMeasureEnabled(true);
                sglm.setGapStrategy(sglm.GAP_HANDLING_NONE);
                break;
            default:

                mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 6));
                ((GridLayoutManager)mRecyclerView.getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){

                        @Override
                        public int getSpanSize(int p1) {
                            switch (((NativeObject)data.get(p1)).get("type").toString()) {
                                case "title":
                                case "detail":
                                case "thumb":
                                case "item":
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
        }
    }
	class Scroll extends RecyclerView.OnScrollListener {

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if(!refresh.isRefreshing() && canLoadMore){
			if (dy > 0 ) {
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
            }else if(dx>0){
                RecyclerView.LayoutManager lm=recyclerView.getLayoutManager();
                 if(lm instanceof LinearLayoutManager){
                    LinearLayoutManager llm=(LinearLayoutManager) lm;
                    if(llm.findLastVisibleItemPosition()>llm.getItemCount()-3)
                        loadMore();
                }
            }
            }
		}

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if(newState==recyclerView.SCROLL_STATE_IDLE){
                recyclerView.invalidateItemDecorations();
            }
            /*RecyclerView.LayoutManager lm=recyclerView.getLayoutManager();
            if(lm instanceof StaggeredGridLayoutManager)
                ((StaggeredGridLayoutManager)lm).invalidateSpanAssignments();*/
        }
        

	}
}
