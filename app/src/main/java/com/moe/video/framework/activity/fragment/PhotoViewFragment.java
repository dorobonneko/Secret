package com.moe.video.framework.activity.fragment;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import com.moe.video.framework.adapter.ImageAdapter;
import android.app.Fragment;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.moe.neko.R;
import org.mozilla.javascript.NativeObject;
import java.util.List;
import com.moe.video.framework.util.StaticData;
import android.widget.Toolbar;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;
import android.widget.ActionMenuView.OnMenuItemClickListener;
import android.view.MenuItem;
import org.mozilla.javascript.ScriptRuntime;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

public class PhotoViewFragment extends Fragment implements View.OnApplyWindowInsetsListener,Toolbar.OnMenuItemClickListener{
    private RecyclerView mRecyclerView;
    private ImageAdapter adapter;
    private SwipeRefreshLayout refresh;
    private PagerSnapHelper mPagerSnapHelper;
    private String type,key;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        View v=activity.getWindow().getDecorView();
        v.setSystemUiVisibility(v.getSystemUiVisibility()&(~v.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        View v=getActivity().getWindow().getDecorView();
        v.setSystemUiVisibility(v.getSystemUiVisibility()|v.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        
        
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.appbrand_view,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(0xff000000);
        mRecyclerView=view.findViewById(R.id.recyclerview);
        refresh=view.findViewById(R.id.refresh);
        Toolbar toolbar=view.findViewById(R.id.toolbar);
        getActivity().getMenuInflater().inflate(R.menu.photo,toolbar.getMenu());
        //toolbar.setTheme(android.R.style.ThemeOverlay_Material);
        View v=(View) toolbar.getParent();
        v.setOnApplyWindowInsetsListener(this);
        v.setFitsSystemWindows(true);
        v.requestApplyInsets();
        toolbar.setOnMenuItemClickListener(this);
    }

    @Override
    public WindowInsets onApplyWindowInsets(View p1, WindowInsets p2) {
        p1.setPadding(0,p2.getSystemWindowInsetTop(),0,0);
        return p2;
    }

    @Override
    public boolean onMenuItemClick(MenuItem p1) {
        switch(p1.getItemId()){
            case R.id.download:
                int position=mRecyclerView.getChildAdapterPosition(mPagerSnapHelper.findSnapView(mRecyclerView.getLayoutManager()));
                if(position==-1)break;
                NativeObject obj=adapter.getObject(position);
                switch(type){
                    case "moebooru":{
                        String url=ScriptRuntime.toString(obj.get("file_url"));
                        startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(url),"image/*"));
                        }break;
                    case "picture":
                        String url=ScriptRuntime.toString(obj.get(key));
                        if(!TextUtils.isEmpty(url))
                        startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(url),"image/*"));
                        
                        break;
                }
                break;
        }
        return true;
    }


    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
         Bundle bundle=getArguments();
         this.key=bundle.getString("key","thumb");
         String key=bundle.getString("id");
         type=bundle.getString("type");
         switch(type){
                case "comic":
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                    break;
                case "picture":
                case "moebooru":
                 refresh.setEnabled(false);
                 
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
                    mPagerSnapHelper=new PagerSnapHelper();
                    mPagerSnapHelper.attachToRecyclerView(mRecyclerView);
                 mRecyclerView.setAdapter(adapter=new ImageAdapter((List<NativeObject>)StaticData.delete(key),bundle.getString("key","thumb")));
                 mRecyclerView.scrollToPosition(findPosition(key));
                break;
                default:
                throw new IllegalArgumentException("不知道加载图片的类型");
            }
            
        
    }
    
    private int findPosition(String id){
        for(int i=0;i<adapter.getItemCount();i++){
            if(id.equals(adapter.getId(i)))
                return i;
        }
        return -1;
    }
    
}
