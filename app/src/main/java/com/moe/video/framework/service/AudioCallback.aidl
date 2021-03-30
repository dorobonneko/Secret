package com.moe.video.framework.service;

interface AudioCallback {
    void onProgress(int progress);
    void onPlay();
    void onPause();
    void onEnd();
    void onError(int type,int code);
    void onServiceClose();
    void onInfo(int position,int duration);
    void onSelected(String title,String icon);
}
