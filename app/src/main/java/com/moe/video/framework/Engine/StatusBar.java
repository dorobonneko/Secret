package com.moe.video.framework.Engine;

public class StatusBar {
    private Callback call;
    public StatusBar(Callback callback){
        this.call=callback;
    }
    public void light(){
        call.setLight(true);
    }
    public void dark(){
        call.setLight(false);
    }
    public static interface Callback{
        public void setLight(boolean light);
    }
    
}
