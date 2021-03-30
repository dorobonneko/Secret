package com.moe.video.framework.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import org.mozilla.javascript.json.JsonParser;
import org.json.JSONArray;
import android.view.ViewGroup;
import java.util.List;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;
import com.moe.neko.Neko;
import android.widget.ImageView;
import uk.co.senab.photoview.PhotoView;
import android.support.v4.widget.CircularProgressDrawable;
import android.graphics.Rect;
import android.content.res.Resources;

 class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<NativeObject> array;
    private Rect bounds=new Rect();
    private String key;
    public ImageAdapter(List<NativeObject> array,String key) {
        this.array = array;
        this.key=key;
        int size=(int) Resources.getSystem().getDisplayMetrics().density*32;
        bounds.right=size;
        bounds.bottom=size;
    }
    public NativeObject getObject(int position){
        return array.get(position);
    }
    @Override
    public ImageAdapter.ImageViewHolder onCreateViewHolder(ViewGroup p1, int p2) {
        switch (p2) {
            case 1:
            case 2:
                return new ImageViewHolder(new PhotoView(p1.getContext()), p2);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ImageAdapter.ImageViewHolder p1, int p2) {
        NativeObject item=array.get(p2);
        switch (getItemViewType(p2)) {
            case 1:
                CircularProgressDrawable cpd=new CircularProgressDrawable(p1.view.getContext());
                cpd.setArrowEnabled(false);
                cpd.setStyle(CircularProgressDrawable.LARGE);
                cpd.setBounds(bounds);
                cpd.setColorSchemeColors(new int[]{0xfffa8a9a});
                Neko.with(p1.view).load(ScriptRuntime.toString(item.get(key))).placeHolder(cpd).into(p1.view);
                break;
            case 2:
                break;
        }
    }


    @Override
    public int getItemViewType(int position) {
        switch (ScriptRuntime.toString(array.get(position).get(("type")))) {
            case "image":
                return 1;
            case "comic":
                return 2;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public String getId(int position){
        return ScriptRuntime.toString(array.get(position).get("id"));
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        PhotoView view;
        ImageViewHolder(View v, int type) {
            super(v);
            view = (PhotoView) v;
            switch(type){
                case 1:
                view.setAllowParentInterceptOnEdge(true);
                view.setLayoutParams(new ViewGroup.LayoutParams(-1,-1));
                view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                break;
                }
        }
    }
}
