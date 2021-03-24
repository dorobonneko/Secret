package com.moe.video.framework.service;

interface AudioCallback {
    void onProgress(int progress);
    void onPlay();
    void onPause();
    void onEnd();
    void onError(int type,int code);
    void onServiceClose();
    
}
