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

public class PhotoViewFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private ImageAdapter adapter;
    private SwipeRefreshLayout refresh;

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
        
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
         Bundle bundle=getArguments();
         String key=bundle.getString("id");
         switch(bundle.getString("type")){
                case "comic":
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                    break;
                case "moebooru":
                 refresh.setEnabled(false);
                 
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
                    PagerSnapHelper psh=new PagerSnapHelper();
                    psh.attachToRecyclerView(mRecyclerView);
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
