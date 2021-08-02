package com.moe.video.framework;
import android.app.Activity;
import android.os.Bundle;
import com.moe.video.framework.widget.MediaView;
import java.io.IOException;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MediaView mv=new MediaView(this);
        setContentView(mv);
        try {
            mv.loadUrl("https://sf1-ttcdn-tos.pstatp.com/obj/tos-cn-v-0004/7915797bf8d3439c91d2092bd5b9659d");
           // mv.play();
            mv.prepare();
            mv.play();
        } catch (IOException e) {} catch (IllegalStateException e) {} catch (SecurityException e) {} catch (IllegalArgumentException e) {}
        
    }
    
    
    
}
