package com.moe.video.framework.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import com.moe.neko.Neko;
import com.moe.video.framework.R;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;

public class IconViewHolder extends BaseViewHolder{
    ImageView icon;
    public IconViewHolder(View v) {
        super(v);
        icon = v.findViewById(R.id.icon);
        v.setOnClickListener(this);
    }

    @Override
    protected void onBindViewHolder(NativeObject obj) {
        Neko.with(icon).load(ScriptRuntime.toString(obj.get("thumb"))).fade(150).circleCrop().into(icon);
        
    }

    
    
}
