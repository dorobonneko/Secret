package com.moe.video.framework.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.moe.video.framework.R;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;
import android.text.Html;
import com.moe.neko.Neko;

public class ItemViewHolder extends BaseViewHolder {
    TextView title,summary;
    ImageView icon;
    public ItemViewHolder(View v){
        super(v);
        v.setOnClickListener(this);
        title=v.findViewById(R.id.title);
        summary=v.findViewById(R.id.summary);
        icon=v.findViewById(R.id.icon);
    }

    @Override
    protected void onBindViewHolder(NativeObject obj) {
        title.setText(ScriptRuntime.toString(obj.getOrDefault("title","")));
        if(obj.has("des",obj)){
            summary.setVisibility(View.VISIBLE);
        summary.setText(Html.fromHtml(ScriptRuntime.toString(obj.getOrDefault("des",""))));
        }else{
            summary.setVisibility(View.GONE);
        }
        if(obj.has("icon",obj)){
            icon.setVisibility(View.VISIBLE);
            Neko.with(icon).load(ScriptRuntime.toString(obj.get("icon"))).asBitmap().fade(300).into(icon);
        }else{
            icon.setVisibility(View.GONE);
        }
        
    }

    @Override
    public void recycle() {
        Neko.with(icon).clear(icon);
    }


    
    
}
