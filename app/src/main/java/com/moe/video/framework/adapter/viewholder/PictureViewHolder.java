package com.moe.video.framework.adapter.viewholder;

import android.support.v4.widget.CircularProgressDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.moe.neko.Neko;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;
import uk.co.senab.photoview.PhotoView;
import android.graphics.Rect;
import android.content.res.Resources;

public class PictureViewHolder extends BaseViewHolder {
    
    PhotoView view;
    Rect bounds=new Rect();
    public PictureViewHolder(View v) {
        super(v);
        int size=(int) Resources.getSystem().getDisplayMetrics().density*32;
        bounds.right=size;
        bounds.bottom=size;
        view = (PhotoView) v;
        view.setAllowParentInterceptOnEdge(true);
        view.setLayoutParams(new ViewGroup.LayoutParams(-1,-1));
        view.setScaleType(ImageView.ScaleType.FIT_CENTER);


    }

    @Override
    protected void onBindViewHolder(NativeObject obj) {
        CircularProgressDrawable cpd=new CircularProgressDrawable(itemView.getContext());
        cpd.setArrowEnabled(false);
        cpd.setStyle(CircularProgressDrawable.LARGE);
        cpd.setBounds(bounds);
        cpd.setColorSchemeColors(new int[]{0xfffa8a9a});
        Neko.with(view).load(ScriptRuntime.toString(obj.get(obj.getOrDefault("thumb_key","thumb")))).placeHolder(cpd).into(view);
        
    }

    @Override
    public void recycle() {
        Neko.with(view).clear(view);
    }


    
}
