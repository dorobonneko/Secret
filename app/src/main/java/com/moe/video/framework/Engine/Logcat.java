package com.moe.video.framework.Engine;

public class Logcat {
    private StringBuilder sb=new StringBuilder();
    public void log(String log){
        sb.append(log).append("\n");
    }
    public String log(){
        return sb.toString();
    }
}
