package com.moe.video.framework.widget;
import android.widget.ImageView;
import android.content.Context;
import android.util.AttributeSet;

public class IconImageView extends ImageView{
    
    public IconImageView(Context context){
        this(context,null);
    }
    public IconImageView(Context Context,AttributeSet attrs){
        super(Context,attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w=MeasureSpec.getSize(widthMeasureSpec)/2;
        setMeasuredDimension(w,w);
    }
    
}
