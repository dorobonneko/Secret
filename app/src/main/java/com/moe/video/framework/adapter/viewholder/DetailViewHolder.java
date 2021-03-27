package com.moe.video.framework.adapter.viewholder;

import android.text.Html;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.moe.neko.Neko;
import com.moe.neko.transform.BlurTransform;
import com.moe.neko.transform.RoundTransform;
import com.moe.video.framework.R;
import com.moe.video.framework.adapter.PostAdapter;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;
import android.widget.ImageView;

public class DetailViewHolder extends BaseViewHolder {
    ImageView thumb;
    TextView des,title,description;
    public DetailViewHolder(View v) {
        super(v);
        thumb=v.findViewById(R.id.icon);
        des=v.findViewById(R.id.summary);
        description = v.findViewById(R.id.desc);
    }

    @Override
    public void onBindViewHolder(NativeObject obj) {
        Neko.with(thumb).load(ScriptRuntime.toString(obj.get(("thumb")))).asBitmap().scaleType(ScaleType.CENTER_CROP).fade(500).transform(new RoundTransform(5)).into(thumb);
        //Pussy.$(dvh.itemView.getContext()).load(ScriptRuntime.toString(obj.get("thumb"))).execute().anime(Anim.fade(350)).transformer(new CropTransformer(Gravity.CENTER),new RoundTransformer(dvh.itemView.getResources().getDisplayMetrics(),5)).into(dvh.thumb);
        String info=ScriptRuntime.toString(obj.getOrDefault("info",""));
        des.setText(Html.fromHtml(info));
        String desc=ScriptRuntime.toString(obj.get("desc"));
        description.setText(desc == null ?null: Html.fromHtml(desc));
        Neko.with(thumb.getParent()).load(ScriptRuntime.toString(obj.get("thumb"))).asBitmap().scaleType(ScaleType.CENTER_CROP).fade(300, 200).transform(new RoundTransform(5), new BlurTransform(15)).into((View)thumb.getParent());
        //Pussy.$(dvh.itemView.getContext()).load(ScriptRuntime.toString(obj.get("thumb"))).execute().anime(Anim.fade(500)).transformer(new CropTransformer(Gravity.CENTER),new com.moe.pussy.transformer.BlurTransformer(55),new RoundTransformer(dvh.itemView.getResources().getDisplayMetrics(),5)).into((View)dvh.thumb.getParent());

    }

    @Override
    public void recycle() {
        Neko.with(thumb).clear(thumb);
        Neko.with(thumb.getParent()).clear((View)thumb.getParent());
    }

    
}
