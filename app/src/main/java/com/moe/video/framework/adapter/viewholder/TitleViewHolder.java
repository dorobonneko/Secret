package com.moe.video.framework.adapter.viewholder;
import android.view.View;
import android.widget.TextView;
import com.moe.video.framework.R;
import com.moe.video.framework.adapter.PostAdapter;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;

public class TitleViewHolder extends BaseViewHolder {
    TextView title,more;
    public TitleViewHolder(View v) {
        super(v);
        title=v.findViewById(R.id.title);
        more = v.findViewById(R.id.summary);
        more.setOnClickListener(this);
    }

    @Override
    protected void onBindViewHolder(NativeObject obj) {
        title.setText(ScriptRuntime.toString(obj.get("title")));
        more.setVisibility(obj.get("click") == null ?View.INVISIBLE: View.VISIBLE);
        
    }

    @Override
    public void recycle() {
    }


    
}
