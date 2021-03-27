package com.moe.video.framework.service;
interface Audio{
 void loadDataSource(String json);
 void reset();
 void play();
 void pause();
 void setCallback(IBinder callback);
 boolean isPlaying();
 void loop(boolean loop);
 boolean isLoop();
 void select(int id);
 void next();
 void pre();
 int getPosition();
 void seetTo(long time);
}

