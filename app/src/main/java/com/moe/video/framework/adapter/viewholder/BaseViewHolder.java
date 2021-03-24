package com.moe.video.framework.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import java.util.Random;
import android.graphics.Color;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
    NativeObject obj;
    BaseViewHolder(View v) {
        super(v);
        }

    @Override
    public void onClick(View p1) {
         Function fun=(Function) obj.get("click");
        if (fun != null) {
            Context context=Context.enter();
            fun.call(context, fun, fun, new Object[]{obj});
            context.exit();
        }
    }

    @Override
    public boolean onLongClick(View p1) {
        Function fun=(Function) obj.get("longClick");
        if (fun != null) {
            Context context=Context.enter();
            fun.call(context, fun, fun, new Object[]{obj});
            context.exit();
        }
        return true;
    }

    public final void onBind(NativeObject obj){
        this.obj=obj;
        onBindViewHolder(obj);
    }
    protected abstract void onBindViewHolder(NativeObject obj);
    public static int randomColor() {
        Random random = new Random();
        int r = random.nextInt(100)+100;
        int g = random.nextInt(100)+100;
        int b = random.nextInt(100)+100;
        return Color.argb(0x8a,r, g, b);
    }
    }
