package com.moe.video.framework.Engine;

public class Image {
    private Image.Callback call;
    public Image(Image.Callback callback){
        this.call=callback;
    }
    public void moebooru(int id,String key){
        call.moebooru(id,key);
    }
    public void picture(int id,String key){
        call.picture(id,key);
    }
    public static interface Callback extends Runtime.Callback{
        void moebooru(int id,String key);
        void picture(int id,String key);
    }
}
