package com.moe.video.framework.adapter.viewholder;

import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.moe.neko.Neko;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;

public class ImageViewHolder extends BaseViewHolder{
    ImageView view;
    ViewGroup mRecyclerView;
    public ImageViewHolder(ViewGroup mRecyclerView,View view) {
        super(view);
        this.mRecyclerView=mRecyclerView;
        TypedArray ta=view.getContext().obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
        view.setForeground(ta.getDrawable(0));
        ta.recycle();
        this.view = (ImageView) view;
        this.view.setScaleType(ScaleType.CENTER_CROP);
        //view.setPadding(10,10,10,10);
        view.setOnClickListener(this);
    }

    @Override
    protected void onBindViewHolder(NativeObject obj) {
        int width=ScriptRuntime.toInt32(obj.getOrDefault("width",-1));
        int height=ScriptRuntime.toInt32(obj.getOrDefault("height",-1));

        int viewWidth=mRecyclerView.getWidth()/2;
        int viewHeight=(int)Math.floor(viewWidth/(float)width*height);
        ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(viewWidth,viewHeight);
        view.setLayoutParams(params);
        Neko.with(view).load(ScriptRuntime.toString(obj.get("thumb"))).fade(150).placeHolder(new ColorDrawable(randomColor())).into(view);
        
    }

    @Override
    public void recycle() {
        Neko.with(view).clear(view);
    }


    
    
}
