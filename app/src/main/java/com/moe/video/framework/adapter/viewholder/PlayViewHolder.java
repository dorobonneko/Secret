package com.moe.video.framework.adapter.viewholder;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.moe.video.framework.R;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;
import android.graphics.drawable.VectorDrawable;

public class PlayViewHolder extends BaseViewHolder {
    TextView title;
    public PlayViewHolder(View v) {
        super(v);
        title = (TextView) v;
        title.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        title.setAllCaps(false);
        title.setOnClickListener(this);
        title.setForeground(v.getResources().getDrawable(R.drawable.ripple,title.getContext().getTheme()));
        
    }

    @Override
    protected void onBindViewHolder(NativeObject obj) {
        title.setText(ScriptRuntime.toString(obj.getOrDefault("title","#$")));
        title.setBackgroundColor(Color.parseColor(ScriptRuntime.toString(obj.getOrDefault("color","#00ffffff"))));
        title.setTextColor(Color.parseColor(ScriptRuntime.toString(obj.getOrDefault("textColor","#ff303030"))));
    }

    @Override
    public void recycle() {
    }


    
}
