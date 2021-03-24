package com.moe.video.framework.adapter.viewholder;

import android.content.res.TypedArray;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;

public class PlayViewHolder extends BaseViewHolder {
    TextView title;
    public PlayViewHolder(View v) {
        super(v);
        title = (TextView) v;
        TypedArray ta=v.getContext().obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
        title.setBackgroundDrawable(ta.getDrawable(0));
        ta.recycle();
        title.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        title.setAllCaps(false);
        title.setOnClickListener(this);
    }

    @Override
    protected void onBindViewHolder(NativeObject obj) {
        title.setText(ScriptRuntime.toString(obj.getOrDefault("title","#$")));
    }

    
}
