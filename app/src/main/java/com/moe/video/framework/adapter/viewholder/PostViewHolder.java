package com.moe.video.framework.adapter.viewholder;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.moe.neko.Neko;
import com.moe.neko.transform.RoundTransform;
import com.moe.pussy.widget.PercentImageView;
import com.moe.video.framework.R;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;

public class PostViewHolder extends BaseViewHolder {
    ImageView thumb;
    TextView des,title;
    public PostViewHolder(View v, int type) {
        super(v);
        title=v.findViewById(R.id.title);
        thumb = v.findViewById(R.id.icon);
        des = v.findViewById(R.id.summary);
        v.setOnClickListener(this);
        v.setOnLongClickListener(this);
        if (type == 6)
            ((PercentImageView)thumb).setPercent(0.618f);
    }

    @Override
    protected void onBindViewHolder(NativeObject obj) {
        title.setText(ScriptRuntime.toString(obj.getOrDefault("title","")));
        des.setText(ScriptRuntime.toString(obj.getOrDefault("des","")));
        Neko.with(thumb).load(ScriptRuntime.toString(obj.get("thumb"))).asBitmap().fade(300).error(new ColorDrawable(0xff000000)).scaleType(ScaleType.CENTER_CROP).transform(new RoundTransform(5)).into(thumb);
        
    }


 
    
}
